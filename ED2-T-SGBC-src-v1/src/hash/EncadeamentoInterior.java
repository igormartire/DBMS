/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hash;

import dominio.Atributo;
import dominio.Registro;
import dominio.Tabela;
import dominio.Valor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;


public class EncadeamentoInterior {

    private final int HASH_FILE_SIZE;
    
    public EncadeamentoInterior(int tam){
        HASH_FILE_SIZE = tam;
    }
    
    private int calcHash(int cod) {
        return cod % HASH_FILE_SIZE;
    }
    
    /**
     * Cria uma tabela hash vazia de tamanho HASH_FILE_SIZE, e salva no arquivo de mesmo nome da tabela
     * Compartimento que não tem lista encadeada associada deve ter registro com chave de Registro igual a -1
     *     Quando o ponteiro para próximo for null, ele deve ser igual ao endereço do compartimento
     * @param tabela Tabela para a qual o arquivo será criado
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public void criaHash(Tabela tabela) throws FileNotFoundException, IOException {
        String nomeArquivoHash = tabela.getNome()+".dat";
        
        String textoVazio = "";
        for (int i = 0; i < Valor.TAMANHO_LIMITE_TEXTO; i++) {
            textoVazio += " ";
        }
        
        List<Valor> valoresVaziosAtributos = new ArrayList<Valor>();
        for(Atributo atr : tabela.getAtributos()) {            
            switch(atr.getTipo()) {
                case Atributo.TIPO_INTEIRO:
                    valoresVaziosAtributos.add(new Valor(0));
                    break;
                case Atributo.TIPO_TEXTO:
                    valoresVaziosAtributos.add(new Valor(textoVazio));
                    break;
            }                            
        }
        
        RandomAccessFile tabelaHash = new RandomAccessFile(nomeArquivoHash,"rw");
        for (int i = 0; i < HASH_FILE_SIZE; i++) {
            Registro registro = new Registro(-1,valoresVaziosAtributos,i,Registro.LIBERADO);
            registro.salva(tabelaHash);
        }
        tabelaHash.close();
    }
    
    /**
    * Executa busca em Arquivos por Encadeamento Interior (Hash)
    * Assumir que ponteiro para próximo nó é igual ao endereço do compartimento quando não houver próximo nó
    * @param codCli: chave do cliente que está sendo buscado
    * @param nomeArquivoHash nome do arquivo que contém a tabela Hash
    * @return Result contendo a = 1 se registro foi encontrado, e end igual ao endereco onde o cliente foi encontrado
    *                ou a = 2 se o registro não foi encontrado, e end igual ao primeiro endereço livre encontrado na lista encadeada, ou -1 se não encontrou endereço livre
    * @throws java.io.FileNotFoundException
    * @throws java.io.IOException
    */
    public Result busca(int codCli, String nomeArquivoHash) throws FileNotFoundException, IOException {
        RandomAccessFile tabelaHash = new RandomAccessFile(nomeArquivoHash,"r");
        int encontrou = 0;
        int endAtual = calcHash(codCli);
        int endResposta = -1;
        while (encontrou == 0) {
            tabelaHash.seek(endAtual*Cliente.tamanhoRegistro);
            Cliente clienteAtual = Cliente.le(tabelaHash);
            if ((clienteAtual.flag == Cliente.LIBERADO) && (endResposta == -1)){
                endResposta = endAtual; // primeiro endereço livre
            }
            if ((clienteAtual.codCliente == codCli) && (clienteAtual.flag == Cliente.OCUPADO)){
                endResposta = endAtual; // endereço do cliente achado
                encontrou = 1; // sai do loop; cliente encontrado
            }
            else if (clienteAtual.prox == endAtual) {
                encontrou = 2; // sai do loop; cliente não encontrado
            }
            else {
                endAtual = clienteAtual.prox; // vai para próximo cliente da lista
            }
        }        
        tabelaHash.close();
        return new Result(encontrou,endResposta);
    }

    /**
    * Executa inserção em Arquivos por Encadeamento Exterior (Hash)
    * @param codCli: código do cliente a ser inserido
    * @param nomeCli: nome do Cliente a ser inserido
    * @param nomeArquivoHash nome do arquivo que contém a tabela Hash
    * @return endereço onde o cliente foi inserido, -1 se não conseguiu inserir 
    * pois já existia um registro com a mesma chave e -2 se não conseguir 
    * inserir porque não havia mais espaço para inserir registros (overflow)
    * @throws java.io.FileNotFoundException
    * @throws java.io.IOException
    */
    public int insere(int codCli, String nomeCli, String nomeArquivoHash) throws FileNotFoundException, IOException {
        Result resultBusca = busca(codCli,nomeArquivoHash);
        if (resultBusca.a != 1) {            
            RandomAccessFile tabelaHash = new RandomAccessFile(nomeArquivoHash,"rw");
            int endInserir;
            if (resultBusca.end != -1) {
                endInserir = resultBusca.end; // endereço livre onde devemos inserir                
            }
            else { // nao ha espaço livre na lista encadeada; vamos buscar um espaço livre a partir do hash da chave que queremos inserir                
                endInserir = calcHash(codCli);
                boolean overflow = true;
                for (int i = 0 ; i < HASH_FILE_SIZE; i++) {
                    tabelaHash.seek(endInserir*Cliente.tamanhoRegistro);
                    Cliente clienteAtual = Cliente.le(tabelaHash);
                    if (clienteAtual.flag == Cliente.OCUPADO) {
                        endInserir = calcHash(endInserir + 1); // aponta para proximo espaço
                    }
                    else {
                        overflow = false;
                        break; // endInserir esta apontando para espaço livre
                    }
                }
                if (overflow) { // nao ha nenhum espaço livre na tabela
                    tabelaHash.close();
                    return -2; // inserçao invalida: overflow
                }
                else { // endInserir esta apontando para o espaço livre onde devemos inserir
                    // devemos adicionar este novo espaco no final da lista encadeada que começa no endereço calcHash(codCli)
                    int endAtual = calcHash(codCli);
                    tabelaHash.seek(endAtual*Cliente.tamanhoRegistro);
                    Cliente clienteAtual = Cliente.le(tabelaHash);
                    while (clienteAtual.prox != endAtual) {
                        endAtual = clienteAtual.prox;
                        tabelaHash.seek(endAtual*Cliente.tamanhoRegistro);
                        clienteAtual = Cliente.le(tabelaHash);
                    }
                    clienteAtual.prox = endInserir; // último cliente da lista encadeada passa a apontar para endInserir
                }
            }
            tabelaHash.seek(endInserir*Cliente.tamanhoRegistro);
            Cliente antigoCliente = Cliente.le(tabelaHash);            
            Cliente novoCliente = new Cliente(codCli, nomeCli, antigoCliente.prox, Cliente.OCUPADO);
            tabelaHash.seek(endInserir*Cliente.tamanhoRegistro);
            novoCliente.salva(tabelaHash);
            tabelaHash.close();
            return endInserir;
        }
        else {
            return -1; // inserçao invalida: chave ja existe
        }        
    }

    /**
    * Executa exclusão em Arquivos por Encadeamento Exterior (Hash)
    * @param codCli: chave do cliente a ser excluído
    * @param nomeArquivoHash nome do arquivo que contém a tabela Hash
    * @return endereço do cliente que foi excluído, -1 se cliente não existe
    * @throws java.io.FileNotFoundException
    * @throws java.io.IOException
    */
    public int exclui(int codCli, String nomeArquivoHash) throws FileNotFoundException, IOException {
        Result resultBusca = busca(codCli,nomeArquivoHash);
        if (resultBusca.a == 1) {
            RandomAccessFile tabelaHash = new RandomAccessFile(nomeArquivoHash,"rw");
            tabelaHash.seek(resultBusca.end*Cliente.tamanhoRegistro);
            Cliente cliente = Cliente.le(tabelaHash);
            cliente.flag = Cliente.LIBERADO;
            tabelaHash.seek(resultBusca.end*Cliente.tamanhoRegistro);
            cliente.salva(tabelaHash);
            tabelaHash.close();
            return resultBusca.end;
        }
        else { //cliente não existe
            return -1;
        }
    }

}
