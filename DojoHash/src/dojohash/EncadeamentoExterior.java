/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dojohash;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

public class EncadeamentoExterior {

    private static final int TAM = 7;

    private int hash(int cod) {
        return (cod % TAM)*CompartimentoHash.tamanhoRegistro;
    }

    /**
     * Cria uma tabela hash vazia de tamanho tam, e salva no arquivo
     * nomeArquivoHash Compartimento que não tem lista encadeada associada deve
     * ter valor igual a -1
     *
     * @param nomeArquivoHash nome do arquivo hash a ser criado
     * @param tam tamanho da tabela hash a ser criada
     */
    public void criaHash(String nomeArquivoHash, int tam) throws FileNotFoundException, Exception {
        RandomAccessFile tabHash = null;
        try {
            tabHash = new RandomAccessFile(new File(nomeArquivoHash), "rw");
            CompartimentoHash c = new CompartimentoHash(-1);
            for (int i = 0; i < tam; i++) {
                c.salva(tabHash);
            }
        }
        finally{
            if(tabHash != null) tabHash.close();
        }
    }

    /**
     * Executa busca em Arquivos por Encadeamento Exterior (Hash) Assumir que
     * ponteiro para próximo nó é igual a -1 quando não houver próximo nó
     *
     * @param codCli: chave do cliente que está sendo buscado
     * @param nomeArquivoHash nome do arquivo que contém a tabela Hash
     * @param nomeArquivoDados nome do arquivo onde os dados estão armazenados
     * @return o endereco onde o cliente foi encontrado, ou -1 se não for
     * encontrado
     */
    public int busca(int codCli, String nomeArquivoHash, String nomeArquivoDados) throws FileNotFoundException, Exception {
        RandomAccessFile tabHash = null;
        RandomAccessFile tabDados = null;
        try {
            tabHash = new RandomAccessFile(new File(nomeArquivoHash), "r");
            tabHash.seek(hash(codCli));
            CompartimentoHash compHash = CompartimentoHash.le(tabHash);
            int prox = compHash.prox;
            if (prox != -1) {
                tabDados = new RandomAccessFile(new File(nomeArquivoDados), "r");
                do {
                    tabDados.seek(prox*Cliente.tamanhoRegistro);
                    Cliente cliente = Cliente.le(tabDados);
                    if (cliente.codCliente == codCli) {
                        if (cliente.flag == Cliente.OCUPADO) {
                            return prox;
                        }
                    }
                    prox = cliente.prox;
                } while (prox != -1);
            }
            return prox;
        }
        finally{
            if(tabHash != null) tabHash.close();
            if(tabDados != null) tabDados.close();
        }
    }

    /**
     * Executa inserção em Arquivos por Encadeamento Exterior (Hash)
     *
     * @param codCli: código do cliente a ser inserido
     * @param nomeCli: nome do Cliente a ser inserido
     * @param nomeArquivoHash nome do arquivo que contém a tabela Hash
     * @param nomeArquivoDados nome do arquivo onde os dados estão armazenados
     * @param numRegistros numero de registros que já existem gravados no
     * arquivo
     * @return endereço onde o cliente foi inserido, -1 se não conseguiu inserir
     */
    public int insere(int codCli, String nomeCli, String nomeArquivoHash, String nomeArquivoDados, int numRegistros) throws Exception {
        RandomAccessFile tabHash = null;
        RandomAccessFile tabDados = null;
        try {
            tabHash = new RandomAccessFile(new File(nomeArquivoHash), "rw");
            tabHash.seek(hash(codCli));
            CompartimentoHash compHash = CompartimentoHash.le(tabHash);
            int prox = compHash.prox;
            //Caso não haja cliente algum no compartimento
            if (prox == -1) {
                int endCliente = numRegistros * Cliente.tamanhoRegistro;
                Cliente cliente = new Cliente(codCli, nomeCli, -1, Cliente.OCUPADO);
                tabDados = new RandomAccessFile(new File(nomeArquivoDados), "rw");
                tabDados.seek(endCliente);
                cliente.salva(tabDados);
                compHash.prox = numRegistros;
                tabHash.seek(hash(codCli));
                compHash.salva(tabHash);
                return numRegistros;
            } else {
                int endPrimeiroLiberado = -1;
                Cliente cliente;
                tabDados = new RandomAccessFile(new File(nomeArquivoDados), "rw");
                int ant_prox=prox;
                do {
                    tabDados.seek(prox*Cliente.tamanhoRegistro);
                    cliente = Cliente.le(tabDados);
                    if ((cliente.flag == Cliente.LIBERADO) && (endPrimeiroLiberado == -1))
                        endPrimeiroLiberado = prox;
                    if (cliente.codCliente == codCli)
                        if (cliente.flag == Cliente.OCUPADO)
                            return -1;
                        else
                            break;
                    ant_prox = prox;
                    prox = cliente.prox;
                } while (prox != -1);

                if (endPrimeiroLiberado != -1){
                    tabDados.seek(endPrimeiroLiberado*Cliente.tamanhoRegistro);
                    cliente = Cliente.le(tabDados);
                    int proxCliente = cliente.prox;
                    cliente = new Cliente(codCli, nomeCli, proxCliente, Cliente.OCUPADO);
                    tabDados.seek(endPrimeiroLiberado*Cliente.tamanhoRegistro);
                    cliente.salva(tabDados);
                    return endPrimeiroLiberado;
                } else {
                    //Esse cliente é o último da lista, pois se não teria entrado no if acima
                    int endCliente = numRegistros * Cliente.tamanhoRegistro;                    
                    cliente.prox = numRegistros;
                    tabDados.seek(ant_prox*Cliente.tamanhoRegistro);
                    cliente.salva(tabDados);
                    cliente = new Cliente(codCli, nomeCli, -1, Cliente.OCUPADO);
                    tabDados.seek(endCliente);
                    cliente.salva(tabDados);
                    return numRegistros;                
                }
            }
        }
        finally{
            if(tabHash != null) tabHash.close();
            if(tabDados != null) tabDados.close();
        }
    }

    /**
     * Executa exclusão em Arquivos por Encadeamento Exterior (Hash)
     *
     * @param codCli: chave do cliente a ser excluído
     * @param nomeArquivoHash nome do arquivo que contém a tabela Hash
     * @param nomeArquivoDados nome do arquivo onde os dados estão armazenados
     * @return endereço do cliente que foi excluído, -1 se cliente não existe
     */
    public int exclui(int codCli, String nomeArquivoHash, String nomeArquivoDados) throws FileNotFoundException, Exception {                
        int end = busca(codCli, nomeArquivoHash, nomeArquivoDados);
        RandomAccessFile tabDados = null;
        try {
            if (end != -1) {
                tabDados = new RandomAccessFile(new File(nomeArquivoDados), "rw");
                tabDados.seek(end*Cliente.tamanhoRegistro);
                Cliente cliente = Cliente.le(tabDados);
                cliente.flag = Cliente.LIBERADO;
                tabDados.seek(end*Cliente.tamanhoRegistro);
                cliente.salva(tabDados);
            }
            return end;
        }
        finally {
            if (tabDados != null) tabDados.close();
        }
    }

}
