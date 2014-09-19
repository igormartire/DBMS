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
        return cod % TAM;
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
        RandomAccessFile out = new RandomAccessFile(new File(nomeArquivoHash), "rw");
        CompartimentoHash c = new CompartimentoHash(-1);
        for (int i = 0; i < tam; i++) {
            c.salva(out);
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
        RandomAccessFile tabHash = new RandomAccessFile(new File(nomeArquivoHash), "r");
        tabHash.seek(hash(codCli));
        CompartimentoHash compHash = CompartimentoHash.le(tabHash);
        int prox = compHash.prox;
        if (prox != -1) {
            RandomAccessFile tabDados = new RandomAccessFile(new File(nomeArquivoDados), "r");
            do {
                tabDados.seek(prox);
                Cliente cliente = Cliente.le(tabDados);
                if (cliente.codCliente == codCli) {
                    if (cliente.flag == Cliente.OCUPADO) {
                        return prox;
                    } else {
                        return -1;
                    }
                }
                prox = cliente.prox;
            } while (prox != -1);
        }
        return prox;
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
        RandomAccessFile tabHash = new RandomAccessFile(new File(nomeArquivoHash), "rw");
        tabHash.seek(hash(codCli));
        CompartimentoHash compHash = CompartimentoHash.le(tabHash);
        int prox = compHash.prox;
        //Caso não haja cliente algum no compartimento
        if (prox == -1) {
            int endCliente = numRegistros * Cliente.tamanhoRegistro;
            Cliente cliente = new Cliente(codCli, nomeCli, -1, Cliente.OCUPADO);
            RandomAccessFile tabDados = new RandomAccessFile(new File(nomeArquivoDados), "rw");
            tabDados.seek(endCliente);
            cliente.salva(tabDados);
            compHash.prox = endCliente;
            tabHash.seek(hash(codCli));
            compHash.salva(tabHash);
            return endCliente;
        } else {
            int endPrimeiroLiberado = -1;
            Cliente cliente;
            RandomAccessFile tabDados = new RandomAccessFile(new File(nomeArquivoDados), "rw");
            do {
                tabDados.seek(prox);
                cliente = Cliente.le(tabDados);
                if ((cliente.flag == Cliente.LIBERADO) && (endPrimeiroLiberado == -1))
                    endPrimeiroLiberado = prox;
                if (cliente.codCliente == codCli)
                    if (cliente.flag == Cliente.OCUPADO)
                        return -1;
                    else
                        break;
                prox = cliente.prox;
            } while (prox != -1);
            
            if (endPrimeiroLiberado != -1){
                tabDados.seek(endPrimeiroLiberado);
                cliente = Cliente.le(tabDados);
                int proxCliente = cliente.prox;
                cliente = new Cliente(codCli, nomeCli, proxCliente, Cliente.OCUPADO);
                tabDados.seek(endPrimeiroLiberado);
                cliente.salva(tabDados);
                return endPrimeiroLiberado;
            } else {
                int endCliente = numRegistros * Cliente.tamanhoRegistro;
                //Esse cliente é o último da lista, pois se não teria entrado no if acima
                cliente.prox = endCliente;
                cliente = new Cliente(codCli, nomeCli, -1, Cliente.OCUPADO);
                tabDados.seek(endCliente);
                cliente.salva(tabDados);
                return endCliente;                
            }
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
        if (end != -1) {
            RandomAccessFile tabDados = new RandomAccessFile(new File(nomeArquivoDados), "rw");
            tabDados.seek(end);
            Cliente cliente = Cliente.le(tabDados);
            cliente.flag = Cliente.LIBERADO;
            tabDados.seek(end);
            cliente.salva(tabDados);
        }
        return end;
    }

}
