/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dojoarvorebmais;

import java.io.File;
import java.io.RandomAccessFile;

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
     * @param nomeArquivoDados nome do arquivo de dados (que contém as folhas da arvore B+)* @return endereço da folha onde o cliente foi inserido, -1 se não conseguiu inserir
     * retorna ponteiro para a folha onde o registro foi inserido
     */
    public int insere(int codCli, String nomeCli, String nomeArquivoMetadados, String nomeArquivoIndice, String nomeArquivoDados) throws Exception {
        //TODO: Inserir aqui o código do algoritmo de inserção        
        return Integer.MAX_VALUE;        
    }

    /**
     * Executa exclusão em Arquivos Indexados por Arvores B+
     * @param codCli: chave do cliente a ser excluído
     * @param nomeArquivoMetadados nome do arquivo de metadados 
     * @param nomeArquivoIndice nome do arquivo de indice (que contém os nós internos da arvore B+)
     * @param nomeArquivoDados nome do arquivo de dados (que contém as folhas da arvore B+) * @return endereço do cliente que foi excluído, -1 se cliente não existe
     * retorna ponteiro para a folha onde o registro foi excluido
     */
    public int exclui(int CodCli, String nomeArquivoMetadados, String nomeArquivoIndice, String nomeArquivoDados) {
        //TODO: Inserir aqui o código do algoritmo de remoção
        return Integer.MAX_VALUE;
    }
}
