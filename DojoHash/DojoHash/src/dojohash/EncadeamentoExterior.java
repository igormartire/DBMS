/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dojohash;


public class EncadeamentoExterior {

    private static final int TAM = 7;

    private int hash(int cod) {
        return cod % TAM;
    }

    /**
     * Cria uma tabela hash vazia de tamanho tam, e salva no arquivo nomeArquivoHash
     * Compartimento que não tem lista encadeada associada deve ter valor igual a -1
     * @param nomeArquivoHash nome do arquivo hash a ser criado
     * @param tam tamanho da tabela hash a ser criada
     */
    public void criaHash(String nomeArquivoHash, int tam) {
        RandomAccessFile out = new RandomAccessFile(new File(nomeArquivoHash), "w");
        CompartimentoHash c = new CompartimentoHash(-1);
        for(int i = 0; i < tam; i++){
            c.salva(out);
        }
    }
    
    /**
    * Executa busca em Arquivos por Encadeamento Exterior (Hash)
    * Assumir que ponteiro para próximo nó é igual a -1 quando não houver próximo nó
    * @param codCli: chave do cliente que está sendo buscado
    * @param nomeArquivoHash nome do arquivo que contém a tabela Hash
    * @param nomeArquivoDados nome do arquivo onde os dados estão armazenados    
    * @return o endereco onde o cliente foi encontrado, ou -1 se não for encontrado
    */
    public int busca(int codCli, String nomeArquivoHash, String nomeArquivoDados) throws Exception {
        RandomAccessFile tabHash = new RandomAccessFile(new File(nomeArquivoHash), "r");
        tabHash.seek(hash(codCli));
        CompartimentoHash compHash = CompartimentoHash.le(tabHash);
        int prox = compHash.prox;
        if(prox != -1) {
            RandomAccessFile tabDados = new RandomAccessFile(new File(nomeArquivoDados), "r");
            //le cliente, se codCli bate então se ocupado return pos atual, senao retorna -1, fazer isso ate o final da lista 
            do {
                tabDados.seek(prox);
                Cliente cliente = Cliente.le(tabDados);
                if(cliente.codCliente == codCli)
                    if(cliente.flag == Cliente.OCUPADO)
                        return prox;
                    else
                        return -1;
                prox = cliente.prox;
            }while(prox != -1);
        }
        return prox;
    }

    /**
    * Executa inserção em Arquivos por Encadeamento Exterior (Hash)
    * @param codCli: código do cliente a ser inserido
    * @param nomeCli: nome do Cliente a ser inserido
    * @param nomeArquivoHash nome do arquivo que contém a tabela Hash
    * @param nomeArquivoDados nome do arquivo onde os dados estão armazenados
     *@param numRegistros numero de registros que já existem gravados no arquivo
    * @return endereço onde o cliente foi inserido, -1 se não conseguiu inserir
    */
    public int insere(int codCli, String nomeCli, String nomeArquivoHash, String nomeArquivoDados, int numRegistros) throws Exception {
        //TODO: Inserir aqui o código do algoritmo de inserção
        return Integer.MAX_VALUE;
    }

    /**
    * Executa exclusão em Arquivos por Encadeamento Exterior (Hash)
    * @param codCli: chave do cliente a ser excluído
    * @param nomeArquivoHash nome do arquivo que contém a tabela Hash
    * @param nomeArquivoDados nome do arquivo onde os dados estão armazenados
    * @return endereço do cliente que foi excluído, -1 se cliente não existe
    */
    public int exclui(int CodCli, String nomeArquivoHash, String nomeArquivoDados) {
        //TODO: Inserir aqui o código do algoritmo de remoção
        return Integer.MAX_VALUE;
    }

}
