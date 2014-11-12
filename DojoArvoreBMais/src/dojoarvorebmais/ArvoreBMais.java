/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dojoarvorebmais;

import java.io.EOFException;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArvoreBMais {

    /**
     * Executa busca em Arquivos utilizando Arvore B+ como indice
     * Assumir que ponteiro para próximo nó é igual a -1 quando não houver próximo nó
     * @param codCli: chave do cliente que está sendo buscado
     * @param nomeArquivoMetadados nome do arquivo de metadados 
     * @param nomeArquivoIndice nome do arquivo de indice (que contém os nós internos da arvore B+)
     * @param nomeArquivoDados nome do arquivo de dados (que contém as folhas da arvore B+)
     * @return uma instancia de ResultBusca, preenchida da seguinte forma:
     * Caso a chave codCli seja encontrada:
         encontrou = true
         pontFolha aponta para a página folha que contém a chave
         pos aponta para a posição em que a chave se encontra dentro da página

       Caso a chave codCli não seja encontrada:
         encontrou = false
         pontFolha aponta para a última página folha examinada
         pos informa a posição, nessa página, onde a chave deveria estar inserida
     */
    public ResultBusca busca(int codCli, String nomeArquivoMetadados, String nomeArquivoIndice, String nomeArquivoDados) throws Exception {
        //Lê metadados
        RandomAccessFile metaFile = new RandomAccessFile(new File(nomeArquivoMetadados), "r");
        Metadados metadados = Metadados.le(metaFile);
        
        //Acha nó folha correspondente ao codCli buscado
        int pontNo = metadados.pontRaiz;
        if (!metadados.raizFolha) {            
            RandomAccessFile indexFile = new RandomAccessFile(new File(nomeArquivoIndice), "r");
            NoInterno no;
            do {
                //Lê nó interno
                indexFile.seek(pontNo);
                no = NoInterno.le(indexFile);

                //Pega ponteiro para o nó filho correspondente ao codCli buscado
                int pos = 0;
                while ((pos < no.m) && (codCli >= no.chaves.get(pos))) {
                    pos++;
                }
                pontNo = no.p.get(pos);                
            } while (!no.apontaFolha);
        }        
        
        //Lê nó folha  correspondente ao codCli buscado
        RandomAccessFile dataFile = new RandomAccessFile(new File(nomeArquivoDados), "r");
        dataFile.seek(pontNo);
        NoFolha no = NoFolha.le(dataFile);
        
        //Procura por codCli no nó folha
        int pos = 0;
        int codApontado = no.clientes.get(0).codCliente;
        while ((pos < no.m) && (codCli > codApontado)) {            
            pos++;
            codApontado = no.clientes.get(pos).codCliente;
        }
        boolean encontrou;
        if ((pos == no.m) || (codCli < codApontado)) {
            encontrou = false;
        }
        else { // codCli == codApontado
            encontrou = true;
        }        
        
        //Retorna resultado
        ResultBusca result = new ResultBusca(pontNo, pos, encontrou);
        return result;
    }

    /**
     * Executa inserção em Arquivos Indexados por Arvore B+
     * @param codCli: código do cliente a ser inserido
     * @param nomeCli: nome do Cliente a ser inserido
     * @param nomeArquivoMetadados nome do arquivo de metadados 
     * @param nomeArquivoIndice nome do arquivo de indice (que contém os nós internos da arvore B+)
     * @param nomeArquivoDados nome do arquivo de dados (que contém as folhas da arvore B+)
     * @return endereço da folha onde o cliente foi inserido, -1 se não conseguiu inserir
     * retorna ponteiro para a folha onde o registro foi inserido
     */
    public int insere(int codCli, String nomeCli, String nomeArquivoMetadados, String nomeArquivoIndice, String nomeArquivoDados) throws Exception {
        ResultBusca resBusca = busca(codCli,nomeArquivoMetadados,nomeArquivoIndice,nomeArquivoDados);
        if (resBusca.encontrou) {
            return -1;
        }
        else {
            //Cria cliente a ser inserido
            Cliente novoCli = new Cliente(codCli, nomeCli);
            
            //Lê nó folha correspondente ao codCli buscado
            RandomAccessFile dataFile = new RandomAccessFile(new File(nomeArquivoDados), "rw");
            dataFile.seek(resBusca.pontFolha);
            NoFolha no = NoFolha.le(dataFile);
            
            //Se há espaço para inserir na folha, simplesmente insere na posição certa e salva
            if (no.m < 2*NoFolha.d) {
                no.clientes.add(resBusca.pos, novoCli);
                no.m++;
                dataFile.seek(resBusca.pontFolha);
                no.salva(dataFile);
                return resBusca.pontFolha;
            }
            else { //É necessário particionar a folha
                
                //Adiciona o cliente que se deseja inserir
                no.clientes.add(resBusca.pos, novoCli);
                no.m++; // no.m == 2*NoFolha.d + 1
                
                //Cria um novo nó com os clientes a partir da posição d da lista de clientes do nó estourado
                //Esse nó fica, visualmente, à direita do nó antigo. Ou seja, ele tem clientes de códigos maiores.
                //Assim sendo, o novo nó aponta para o nó folha que o nó antigo apontava. E o nó antigo irá apontar para esse novo nó.
                NoFolha novoNo = new NoFolha(NoFolha.d + 1, no.pontPai, no.pontProx, no.clientes.subList(NoFolha.d, no.m));
                
                //Agora o nó acredita que só tem os primeiros d clientes de sua lista (os demais estão no novo nó)
                //Obs.: Isso funciona porque, ao salvarmos o nó, o que é levado em consideração é o valor de m e não o tamanho real da lista de clientes
                no.m = NoFolha.d;
                
                //Pega o endereço onde o novo nó será salvo
                RandomAccessFile metaFile = new RandomAccessFile(new File(nomeArquivoMetadados), "rw");
                Metadados metadados = Metadados.le(metaFile);                
                int endNovoNo = metadados.pontProxNoFolhaLivre;
                
                //Atualiza o ponteiro do nó folha antigo
                no.pontProx = endNovoNo;
                                
                //Atualiza metadados.pontProxNoFolhaLivre
                try {
                    dataFile.seek(endNovoNo);
                    
                    //Tenta ler na posição indicada atualmente como livre
                    NoFolha noLiberado = NoFolha.le(dataFile);
                    
                    //Se conseguir ler, quer dizer que essa posição contém uma folha que foi excluída
                    //Essa folha tem como valor de pontProx a próxima posição livre
                    //O valor de pontProx será o novo metadados.pontProxNoFolhaLivre                    
                    //O novoNo será gravado em cima dessa folha apagada
                    metadados.pontProxNoFolhaLivre = noLiberado.pontProx;                    
                }
                catch (EOFException ex) {
                    //Se for o fim do arquivo, simplesmente atualiza metadados.pontProxNoFolhaLivre
                    metadados.pontProxNoFolhaLivre += NoFolha.TAMANHO;
                }
                
                //Salva os nós folhas
                dataFile.seek(resBusca.pontFolha);
                no.salva(dataFile);
                dataFile.seek(endNovoNo);
                novoNo.salva(dataFile);
                
                //Salva os metadados
                metaFile.seek(0);
                metadados.salva(metaFile);
                
                //Insere no pai a chave do primeiro cliente do novoNo
                List<Integer> nosFolhaComPaisTrocados = insereEmArvoreB(novoNo.pontPai, novoNo.clientes.get(0).codCliente, endNovoNo, true, nomeArquivoMetadados, nomeArquivoIndice);
                
                //Se houve nós folha com pais trocados, então atualiza o pai desses nós
                if (!nosFolhaComPaisTrocados.isEmpty()) {
                    int endNovoPai = nosFolhaComPaisTrocados.get(0);
                    for (int i = 1; i < nosFolhaComPaisTrocados.size(); i++) {
                        int pontN = nosFolhaComPaisTrocados.get(i);
                        dataFile.seek(pontN);
                        NoFolha n = NoFolha.le(dataFile);
                        n.pontPai = endNovoPai;
                        dataFile.seek(pontN);
                        n.salva(dataFile);
                    }
                }         
                
                //Cria ponteiro para a folha onde o cliente foi inserido (pois esse ponteiro será o valor de retorno desse método)
                int pontFolhaComClienteInserido;
                //Verifica se cliente está no nó folha antigo. Se não estiver, então está no nó folha novo.                
                int pos = 0;
                while ((pos < no.m) && (no.clientes.get(pos).codCliente != codCli)) {
                    pos++;
                }
                if (pos == no.m) { //cliente não está no nó antigo, logo está inserido no nó novo
                    pontFolhaComClienteInserido = endNovoNo;
                }
                else { //cliente está inserido no nó antigo
                    pontFolhaComClienteInserido = resBusca.pontFolha;
                }
                
                return pontFolhaComClienteInserido;
            }
        }  
    }

    /**
     * Executa exclusão em Arquivos Indexados por Arvores B+
     * @param codCli: chave do cliente a ser excluído
     * @param nomeArquivoMetadados nome do arquivo de metadados 
     * @param nomeArquivoIndice nome do arquivo de indice (que contém os nós internos da arvore B+)
     * @param nomeArquivoDados nome do arquivo de dados (que contém as folhas da arvore B+)
     * @return endereço do cliente que foi excluído, -1 se cliente não existe
     * retorna ponteiro para a folha onde o registro foi excluido
     */
    public int exclui(int codCli, String nomeArquivoMetadados, String nomeArquivoIndice, String nomeArquivoDados) throws Exception {
        ResultBusca resBusca = busca(codCli,nomeArquivoMetadados,nomeArquivoIndice,nomeArquivoDados);
        if (!resBusca.encontrou) { //cliente não existe
            return -1; 
        }
        else { //achou o cliente
            
            //Lê nó folha onde o cliente foi achado
            RandomAccessFile dataFile = new RandomAccessFile(new File(nomeArquivoDados),"rw");
            dataFile.seek(resBusca.pontFolha);
            NoFolha no = NoFolha.le(dataFile);
            
            //Remove o cliente da folha
            no.clientes.remove(resBusca.pos);
            no.m--;
            
            //Se possui ao menos d clientes ou for raiz, entao basta salvar o no
            if ((no.m >= NoFolha.d) || (no.pontPai == -1)) {
                dataFile.seek(resBusca.pontFolha);
                no.salva(dataFile);
            }
            else { //no.m < d e nao e raiz: concatenar ou redistribuir
                /*
                * Chegando-se aqui, sabemos o seguinte a respeito do no:
                *  - No é folha e possui pai (pois nao e raiz)
                *  - Tem ao menos 1 nó vizinho.
                */
                
                //Pega os vizinhos
                int pos = 0;
                //Le o no pai
                RandomAccessFile indexFile = new RandomAccessFile(new File(nomeArquivoIndice),"rw");
                indexFile.seek(no.pontPai);
                NoInterno W = NoInterno.le(indexFile);
                //Acha a posicao do ponteiro para o filho do qual o cliente foi excluido
                while ((pos < W.m) && (codCli >= W.chaves.get(pos))) {
                    pos++;
                }
                //W.p.get(pos) é o ponteiro para o nó folha do qual o cliente foi excluido                
                int pontNoVizEsq = pos-1; //se pos == 0, entao pontNoVizEsq == -1
                int pontNoVizDir = (pos < W.m) ? pos+1 : -1;
                //pontNoVizEsq/Dir == -1 significa que nao tem o vizinho da esquerda ou da direita
                //Contudo, sabemos que um deles existe, pois o no folha tem que ter ao menos 1 vizinho
                
                //Define quem é P e Q
                NoFolha vizEsq = null, vizDir = null;
                int somaEsq = 0 , somaDir = 0, soma = 0;
                if (pontNoVizEsq != -1) {
                    pontNoVizEsq = W.p.get(pontNoVizEsq);
                    dataFile.seek(pontNoVizEsq);
                    vizEsq = NoFolha.le(dataFile);
                    somaEsq = no.m + vizEsq.m;
                }
                if (pontNoVizDir != -1) {
                    pontNoVizDir = W.p.get(pontNoVizDir);
                    dataFile.seek(pontNoVizDir);
                    vizDir = NoFolha.le(dataFile);
                    somaDir = no.m + vizDir.m;
                }
                NoFolha P, Q;                
                int posW; //posW e a posiçao da chave em W que fica entre os ponteiros para P e Q
                if ((vizDir != null) && (somaDir >= somaEsq)) {
                    //trabalharemos (concatenaçao ou redistribuiçao) com o vizinho direito
                    P = no;
                    Q = vizDir;
                    posW = pos;
                    soma = somaDir;
                }
                else { //trabalharemos (concatenaçao ou redistribuiçao) com o vizinho esquerdo
                    P = vizEsq;
                    Q = no;
                    posW = pos-1;
                    soma = somaEsq;
                }
                
                //Faremos redistribuicao se a soma da quantidade de registros em P e Q for maior ou igual a 2*d
                boolean redistribuicao = (soma >= 2*NoFolha.d);
                
                if(redistribuicao) {
                    List<Cliente> concatClientes = new ArrayList<Cliente>();
                    concatClientes.addAll(P.clientes);
                    concatClientes.addAll(Q.clientes);
                    //concatCliente ja esta com os clientes ordenados
                    //Agora redistribuiremos os clientes entre P e Q
                    //P fica com os primeiros d clientes
                    //Q fica com o resto
                    P.clientes = concatClientes.subList(0, NoFolha.d);
                    P.m = NoFolha.d;
                    Q.clientes = concatClientes.subList(NoFolha.d, soma);
                    Q.m = soma - NoFolha.d;
                    //Temos que atualizar a chave de indice posW para a chave do primeiro cliente de Q
                    W.chaves.set(posW, Q.clientes.get(0).codCliente);
                    //Salva nos folhas e o pai
                    dataFile.seek(W.p.get(posW));
                    P.salva(dataFile);
                    dataFile.seek(W.p.get(posW+1));
                    Q.salva(dataFile);
                    indexFile.seek(P.pontPai);
                    W.salva(indexFile);
                }
                else { //concatenacao
                    //Passa todos os clientes de Q para P (já ficam ordenados)
                    P.clientes.addAll(Q.clientes);
                    P.m = P.clientes.size();
                    
                    //Atualiza lista encadeada
                    P.pontProx = Q.pontProx;
                    
                    //Libera a folha Q (e atualiza os metadados)
                    RandomAccessFile metaFile = new RandomAccessFile(new File(nomeArquivoMetadados), "rw");                    
                    Metadados metadados = Metadados.le(metaFile);                    
                    Q.pontProx = metadados.pontProxNoFolhaLivre;
                    int pontP = W.p.get(posW);
                    int pontQ = W.p.get(posW+1);
                    metadados.pontProxNoFolhaLivre = pontQ;
                    
                    //Salva nos folhas e metadados
                    dataFile.seek(pontP);
                    P.salva(dataFile);
                    dataFile.seek(pontQ);
                    Q.salva(dataFile);
                    metaFile.seek(0);
                    metadados.salva(metaFile);
                    
                    //Exclui a chave de W de posição posW bem como o ponteiro de posição posW+1
                    List<Integer> nosComPaisTrocados = excluiEmArvoreB(P.pontPai, posW, nomeArquivoMetadados, nomeArquivoIndice);
                                        
                    //Se houve nós com pais trocados, então atualiza o pai desses nós
                    if (!nosComPaisTrocados.isEmpty()) {
                        int endNovoPai = nosComPaisTrocados.get(0);
                        for (int i = 1; i < nosComPaisTrocados.size(); i++) {
                            int pontN = nosComPaisTrocados.get(i);
                            dataFile.seek(pontN);
                            NoFolha n = NoFolha.le(dataFile);
                            n.pontPai = endNovoPai;
                            dataFile.seek(pontN);
                            n.salva(dataFile);
                        }
                    } 
                }
            }
            return resBusca.pontFolha;
        }
    }
        
    /**
     * Insere, no Nó de Índice de endereço especificado, a chave especificada e o ponteiro especificado
     * @param pontNo endereço do no de indice; -1 se for para criar um nó de índice que se tornará raiz
     * @param chave a chave a ser adicionada no no de indice
     * @param pontFilho o ponteiro a ser adicionado no no de indice
     * @param apontaFolha true caso o no de indice aponte para folhas, false caso contrario
     * @param nomeArquivoMetadados nome do arquivo de metadados 
     * @param nomeArquivoIndice nome do arquivo de indice (que contém os nós internos da arvore B+)
     * @return se apontaFolha = true, retorna lista de endereços de nós folhas cujo pontPai mudou para o endereço armazenado na posição 0 da lista retornada
     *         se apontaFolha = false, retorna lista de endereços de nós de índice cujo pontPai mudou para o endereço armazenado na posição 0 da lista retornada
     */
    private List<Integer> insereEmArvoreB(int pontNo, int chave, int pontFilho, boolean apontaFolha, String nomeArquivoMetadados, String nomeArquivoIndice) throws Exception {
        if (pontNo == -1) { //não existe nó de índice para inserir a chave. É preciso criar um, que será a nova raiz.
            /* 
            * O filho que chamou a função não tem pai (é particionamento de uma raiz).
            * Criaremos um nó raiz que terá *chave* como único elemento. (a raiz pode ter m == 1)
            * O ponteiro da esquerda dessa chave será a raiz atual.
            * O ponteiro da direita será pontFilho
            */
            RandomAccessFile metaFile = new RandomAccessFile(new File(nomeArquivoMetadados), "rw");
            Metadados metadados = Metadados.le(metaFile);
            NoInterno novaRaiz = new NoInterno(1,apontaFolha,-1,Arrays.asList(metadados.pontRaiz,pontFilho), Arrays.asList(chave));
            
            //Atualiza metadados.pontRaiz e metadados.raizFolha
            metadados.pontRaiz = metadados.pontProxNoInternoLivre;
            metadados.raizFolha = false;
            
            //Atualiza metadados.pontProxNoInternoLivre
            RandomAccessFile indexFile = new RandomAccessFile(new File(nomeArquivoIndice), "rw");
            try {
                indexFile.seek(metadados.pontProxNoInternoLivre);

                //Tenta ler na posição indicada atualmente como livre
                NoInterno noLiberado = NoInterno.le(indexFile);
                
                //Se conseguir ler, quer dizer que essa posição contém um no que foi excluído
                //Esse nó tem como valor de pontPai a próxima posição livre
                //O valor de pontPai será o novo metadados.pontProxNoFolhaLivre                    
                //A novaRaiz será gravada em cima desse nó apagado
                metadados.pontProxNoInternoLivre = noLiberado.pontPai;                    
            }
            catch (EOFException ex) {
                //Se for o fim do arquivo, simplesmente atualiza metadados.pontProxNoFolhaLivre
                metadados.pontProxNoInternoLivre += NoInterno.TAMANHO;
            }
            
            //Salva o nó de indice
            indexFile.seek(metadados.pontRaiz);
            novaRaiz.salva(indexFile);

            //Salva os metadados
            metaFile.seek(0);
            metadados.salva(metaFile);
            
            List<Integer> retorno = new ArrayList<Integer>();
            retorno.add(metadados.pontRaiz);
            retorno.addAll(novaRaiz.p);
            return retorno;
        }
        else { //pontNo aponta para um nó de índice, onde deve ser inserida a chave
            
            //Lê o nó de índice
            RandomAccessFile indexFile = new RandomAccessFile(new File(nomeArquivoIndice), "rw");
            indexFile.seek(pontNo);
            NoInterno no = NoInterno.le(indexFile);
            
            //Busca a posição onde inserir a chave
            int pos = 0;
            int chaveApontada = no.chaves.get(0);
            while ((pos < no.m) && (chave > chaveApontada)) {            
                pos++;
                chaveApontada = no.chaves.get(pos);
            }

            //Insere a chave
            no.chaves.add(pos, chave);
            no.m++;
            
            //Insere o ponteiro
            no.p.add(pos+1, pontFilho);
            
            //Se não estourou a capacidade do nó, simplesmente salva o nó e é o fim da inserção
            if (no.m <= 2*NoFolha.d) {
                //Salva o nó
                indexFile.seek(pontNo);
                no.salva(indexFile);
                
                //Retorna lista vazia, pois não houve mudança de pai para nenhum filho, já que não houve mudança de estrutura
                return new ArrayList<Integer>();
            }
            else { //É necessário particionar o nó
                
                // no.m == 2*NoFolha.d + 1
                
                //Cria um novo nó de índices com as chaves e ponteiros a partir da posição d+1 da lista de chaves e ponteiros do nó estourado.
                //Esse nó fica, visualmente, à direita do nó antigo. Ou seja, ele tem chaves de códigos maiores.
                NoInterno novoNo = new NoInterno(NoInterno.d, no.apontaFolha, no.pontPai, no.p.subList(NoInterno.d + 1, no.m + 1), no.chaves.subList(NoInterno.d + 1, no.m));
                
                //Agora o nó acredita que só tem as primeiros d chaves e os primeiros d+1 ponteiros de suas listas de chaves e ponteiros
                //Obs.: Isso funciona porque, ao salvarmos o nó, o que é levado em consideração é o valor de m e não o tamanho real da lista de clientes
                no.m = NoInterno.d;
                
                //Pega o endereço onde o novo nó será salvo
                RandomAccessFile metaFile = new RandomAccessFile(new File(nomeArquivoMetadados), "rw");
                Metadados metadados = Metadados.le(metaFile);                
                int endNovoNo = metadados.pontProxNoInternoLivre;
                                
                //Atualiza metadados.pontProxNoInternoLivre
                try {
                    indexFile.seek(endNovoNo);

                    //Tenta ler na posição indicada atualmente como livre
                    NoInterno noLiberado = NoInterno.le(indexFile);

                    //Se conseguir ler, quer dizer que essa posição contém um no que foi excluído
                    //Esse nó tem como valor de pontPai a próxima posição livre
                    //O valor de pontPai será o novo metadados.pontProxNoFolhaLivre                    
                    //A novaRaiz será gravada em cima desse nó apagado
                    metadados.pontProxNoInternoLivre = noLiberado.pontPai;                    
                }
                catch (EOFException ex) {
                    //Se for o fim do arquivo, simplesmente atualiza metadados.pontProxNoFolhaLivre
                    metadados.pontProxNoInternoLivre += NoInterno.TAMANHO;
                }

                //Salva os nós de indice
                indexFile.seek(pontNo);
                no.salva(indexFile);
                indexFile.seek(endNovoNo);
                novoNo.salva(indexFile);

                //Salva os metadados
                metaFile.seek(0);
                metadados.salva(metaFile);
                
                //Insere no pai a chave de índice d do nó estourado e o ponteiro para o novo nó
                List<Integer> nosIndiceComPaisTrocados = insereEmArvoreB(no.pontPai, no.chaves.get(NoInterno.d), endNovoNo, false, nomeArquivoMetadados, nomeArquivoIndice);
                
                //Se houve nós de índice com pais trocados, então atualiza o pai desses nós
                if (!nosIndiceComPaisTrocados.isEmpty()) {
                    int endNovoPai = nosIndiceComPaisTrocados.get(0);
                    for (int i = 1; i < nosIndiceComPaisTrocados.size(); i++) {
                        int pontN = nosIndiceComPaisTrocados.get(i);
                        indexFile.seek(pontN);
                        NoInterno n = NoInterno.le(indexFile);
                        n.pontPai = endNovoPai;
                        indexFile.seek(pontN);
                        n.salva(indexFile);
                    }
                }                
                                
                List<Integer> retorno = new ArrayList<Integer>();
                retorno.add(endNovoNo);
                retorno.addAll(novoNo.p);
                return retorno;
            }
        }
    }

    /**
     * Exclui a chave de posição posChave (incluindo o ponteiro de posição posChave+1) do nó de índices de posição pontNo no arquivo de índices.
     * @param pontNo posição no arquivo de índices do nó do qual se deseja excluir a chave
     * @param posChave posição da chave que se deseja excluir
     * @param nomeArquivoMetadados nome do arquivo de metadados 
     * @param nomeArquivoIndice nome do arquivo de indice (que contém os nós internos da arvore B+)
     * @return retorna lista de endereços de nós cujo pontPai mudou para o endereço armazenado na posição 0 da lista retornada     *         
     */
    private List<Integer> excluiEmArvoreB(int pontNo, int posChave, String nomeArquivoMetadados, String nomeArquivoIndice) throws Exception {
        
        //Lê nó do qual deseja-se excluir a chave
        RandomAccessFile indexFile = new RandomAccessFile(new File(nomeArquivoIndice),"rw");
        indexFile.seek(pontNo);
        NoInterno no = NoInterno.le(indexFile);
        
        //Remove a chave de posição posChave
        int chaveExcluida = no.chaves.get(posChave);
        no.chaves.remove(posChave);
        no.p.remove(posChave+1);
        no.m--;
        
        //Se for raiz e não tiver mais nenhum elemento, então tem que excluir este no (raiz)
        if ((no.pontPai == -1) && (no.m == 0)) {
            /*
            * O nó só tem um filho, apontado por no.p.get(0)
            * Esse filho se tornará a raiz
            * Os metadados devem ser atualizados
            */
            //Lê metadados
            RandomAccessFile metaFile = new RandomAccessFile(new File(nomeArquivoMetadados), "rw");                    
            Metadados metadados = Metadados.le(metaFile);
            //Exclui raiz
            no.pontPai = metadados.pontProxNoInternoLivre;
            metadados.pontProxNoInternoLivre = pontNo;
            //Atualiza ponteiro pra raiz
            metadados.pontRaiz = no.p.get(0);
            metadados.raizFolha = no.apontaFolha;
            //Salva no e metadados
            indexFile.seek(pontNo);
            no.salva(indexFile);
            metaFile.seek(0);
            metadados.salva(metaFile);
            List<Integer> retorno = new ArrayList<Integer>();
            retorno.add(-1);
            retorno.add(metadados.pontRaiz);
            return retorno;
            
        }
        //Senão, se possui ao menos d chaves ou for raiz, entao basta salvar o no
        else if ((no.m >= NoInterno.d) || (no.pontPai == -1)) {
            indexFile.seek(pontNo);
            no.salva(indexFile);
            List<Integer> retorno = new ArrayList<Integer>();
            return retorno;
        }
        else { //no.m < d e nao e raiz: concatenar ou redistribuir
            /*
            * Chegando-se aqui, sabemos o seguinte a respeito do no:
            *  - Nó possui pai (pois não é raiz)
            *  - Tem ao menos 1 nó vizinho.
            */
        
            //Pega os vizinhos
            int pos = 0;
            //Le o no pai
            indexFile.seek(no.pontPai);
            NoInterno W = NoInterno.le(indexFile);
            //Acha a posicao do ponteiro para o filho do qual a chave foi excluída
            int algumaChaveDoFilho = no.chaves.get(0);
            while ((pos < W.m) && (algumaChaveDoFilho >= W.chaves.get(pos))) {
                pos++;
            }
            //W.p.get(pos) é o ponteiro para o nó do qual a chave foi excluída
            int pontNoVizEsq = pos-1; //se pos == 0, entao pontNoVizEsq == -1
            int pontNoVizDir = (pos < W.m) ? pos+1 : -1;
            //pontNoVizEsq/Dir == -1 significa que nao tem o vizinho da esquerda ou da direita
            //Contudo, sabemos que um deles existe, pois o nó tem que ter ao menos 1 vizinho

            //Define quem é P e Q
            NoInterno vizEsq = null, vizDir = null;
            int somaEsq = 0 , somaDir = 0, soma = 0;
            if (pontNoVizEsq != -1) {
                pontNoVizEsq = W.p.get(pontNoVizEsq);
                indexFile.seek(pontNoVizEsq);
                vizEsq = NoInterno.le(indexFile);
                somaEsq = no.m + vizEsq.m;
            }
            if (pontNoVizDir != -1) {
                pontNoVizDir = W.p.get(pontNoVizDir);
                indexFile.seek(pontNoVizDir);
                vizDir = NoInterno.le(indexFile);
                somaDir = no.m + vizDir.m;
            }
            NoInterno P, Q;                
            int posW; //posW e a posiçao da chave em W que fica entre os ponteiros para P e Q
            if ((vizDir != null) && (somaDir >= somaEsq)) {
                //trabalharemos (concatenaçao ou redistribuiçao) com o vizinho direito
                P = no;
                Q = vizDir;
                posW = pos;
                soma = somaDir;
            }
            else { //trabalharemos (concatenaçao ou redistribuiçao) com o vizinho esquerdo
                P = vizEsq;
                Q = no;
                posW = pos-1;
                soma = somaEsq;
            }

            //Faremos redistribuicao se a soma da quantidade de chaves em P e Q for maior ou igual a 2*d
            boolean redistribuicao = (soma >= 2*NoInterno.d);

            if(redistribuicao) {
                
                if (P.m < NoInterno.d) { //é P que está precisando de chaves de Q
                    P.chaves.add(W.chaves.get(posW));
                    P.m++;
                    W.chaves.set(posW, Q.chaves.remove(0));
                    Q.m--;
                    P.p.add(Q.p.remove(0));                  
                }
                else { //Q.m < NoInterno.d; é Q que está precisando de chaves de Q
                    Q.chaves.add(0,W.chaves.get(posW));
                    Q.m++;
                    W.chaves.set(posW, P.chaves.remove(P.m-1));
                    P.m--;
                    Q.p.add(0,P.p.remove(P.m+1));
                }
                                
                //Salva nós e o pai
                indexFile.seek(W.p.get(posW));
                P.salva(indexFile);
                indexFile.seek(W.p.get(posW+1));
                Q.salva(indexFile);
                indexFile.seek(P.pontPai);
                W.salva(indexFile);
                List<Integer> retorno = new ArrayList<Integer>();
                return retorno; 
            }
            else { //concatenacao
                
                //Adiciona a chave de W que separa P e Q em P e adiciona tudo de Q em P.
                P.chaves.add(W.chaves.get(posW));
                P.chaves.addAll(Q.chaves);
                P.p.addAll(Q.p);
                P.m = P.chaves.size();
                
                //Libera o nó Q (e atualiza os metadados)
                RandomAccessFile metaFile = new RandomAccessFile(new File(nomeArquivoMetadados), "rw");                    
                Metadados metadados = Metadados.le(metaFile);                    
                Q.pontPai = metadados.pontProxNoInternoLivre;
                int pontP = W.p.get(posW);
                int pontQ = W.p.get(posW+1);
                metadados.pontProxNoInternoLivre = pontQ;

                //Salva nos folhas e metadados
                indexFile.seek(pontP);
                P.salva(indexFile);
                indexFile.seek(pontQ);
                Q.salva(indexFile);
                metaFile.seek(0);
                metadados.salva(metaFile);

                //Exclui a chave de W de posição posW bem como o ponteiro de posição posW+1
                List<Integer> nosComPaisTrocados = excluiEmArvoreB(P.pontPai, posW, nomeArquivoMetadados, nomeArquivoIndice);
                
                //Se houve nós de índice com pais trocados, então atualiza o pai desses nós
                if (!nosComPaisTrocados.isEmpty()) {
                    int endNovoPai = nosComPaisTrocados.get(0);
                    for (int i = 1; i < nosComPaisTrocados.size(); i++) {
                        int pontN = nosComPaisTrocados.get(i);
                        indexFile.seek(pontN);
                        NoInterno n = NoInterno.le(indexFile);
                        n.pontPai = endNovoPai;
                        indexFile.seek(pontN);
                        n.salva(indexFile);
                    }
                }
                
                List<Integer> retorno = new ArrayList<Integer>();
                retorno.add(pontP);
                retorno.addAll(Q.p);
                return retorno;
            }
        }
    }
}