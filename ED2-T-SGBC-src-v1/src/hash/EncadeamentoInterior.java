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
    
    public String getNomeArquivoHash(Tabela tabela) {
        return tabela.getNome()+".dat";
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
        String nomeArquivoHash = getNomeArquivoHash(tabela);
        
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
    * @param codReg: chave do registro que está sendo buscado
    * @param tabela tabela na qual o registro está sendo buscado
    * @return Result contendo a = 1 se registro foi encontrado, e end igual ao endereco onde o registro foi encontrado
    *                ou a = 2 se o registro não foi encontrado, e end igual ao primeiro endereço livre encontrado na lista encadeada, ou -1 se não encontrou endereço livre
    * @throws java.io.FileNotFoundException
    * @throws java.io.IOException
    */
    public Result busca(int codReg, Tabela tabela) throws FileNotFoundException, IOException {
        String nomeArquivoHash = getNomeArquivoHash(tabela);
        RandomAccessFile tabelaHash = new RandomAccessFile(nomeArquivoHash,"r");
        int encontrou = 0;
        int endAtual = calcHash(codReg);
        int endResposta = -1;
        int tamanhoRegistro = tabela.getTamanhoRegistro();
        while (encontrou == 0) {
            tabelaHash.seek(endAtual*tamanhoRegistro);
            Registro registroAtual = Registro.le(tabelaHash, tabela);
            if ((registroAtual.getFlag() == Registro.LIBERADO) && (endResposta == -1)){
                endResposta = endAtual; // primeiro endereço livre
            }
            if ((registroAtual.getValorChave() == codReg) && (registroAtual.getFlag() == Registro.OCUPADO)){
                endResposta = endAtual; // endereço do registro achado
                encontrou = 1; // sai do loop; registro encontrado
            }
            else if (registroAtual.getProx() == endAtual) {
                encontrou = 2; // sai do loop; registro não encontrado
            }
            else {
                endAtual = registroAtual.getProx(); // vai para próximo registro da lista
            }
        }        
        tabelaHash.close();
        return new Result(encontrou,endResposta);
    }

    /**
    * Executa inserção em Arquivos por Encadeamento Exterior (Hash)
    * @param codReg: código do registro a ser inserido
    * @param valoresAtributos: lista com os valores dos atributos armazenados no registro
    * @param tabela tabela na qual o registro está sendo inserido
    * @return endereço onde o registro foi inserido, -1 se não conseguiu inserir 
    * pois já existia um registro com a mesma chave e -2 se não conseguir 
    * inserir porque não havia mais espaço para inserir registros (overflow)
    * @throws java.io.FileNotFoundException
    * @throws java.io.IOException
    */
    public int insere(int codReg, List<Valor> valoresAtributos, Tabela tabela) throws FileNotFoundException, IOException {        
        Result resultBusca = busca(codReg,tabela);
        if (resultBusca.getA() != 1) {            
            String nomeArquivoHash = getNomeArquivoHash(tabela);
            RandomAccessFile tabelaHash = new RandomAccessFile(nomeArquivoHash,"rw");
            int tamanhoRegistro = tabela.getTamanhoRegistro();
            int endInserir;
            if (resultBusca.getEnd() != -1) {
                endInserir = resultBusca.getEnd(); // endereço livre onde devemos inserir                
            }
            else { // nao há espaço livre na lista encadeada; vamos buscar um espaço livre a partir do hash da chave que queremos inserir                
                endInserir = calcHash(codReg);
                boolean overflow = true; //valor inicial de overflow. Se não acontecer overflow o valor será trocado para false.
                for (int i = 0 ; i < HASH_FILE_SIZE; i++) {
                    tabelaHash.seek(endInserir*tamanhoRegistro);
                    Registro registroAtual = Registro.le(tabelaHash,tabela);
                    if (registroAtual.getFlag() == Registro.OCUPADO) {
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
                    int endAtual = calcHash(codReg);
                    tabelaHash.seek(endAtual*tamanhoRegistro);
                    Registro registroAtual = Registro.le(tabelaHash,tabela);
                    while (registroAtual.getProx() != endAtual) {
                        endAtual = registroAtual.getProx();
                        tabelaHash.seek(endAtual*tamanhoRegistro);
                        registroAtual = Registro.le(tabelaHash,tabela);
                    }
                    registroAtual.setProx(endInserir); // último registro da lista encadeada passa a apontar para endInserir
                    tabelaHash.seek(endAtual*tamanhoRegistro);
                    registroAtual.salva(tabelaHash);
                }
            }
            tabelaHash.seek(endInserir*tamanhoRegistro);
            Registro antigoRegistro = Registro.le(tabelaHash,tabela);            
            Registro novoRegistro = new Registro(codReg, valoresAtributos, antigoRegistro.getProx(), Registro.OCUPADO);
            tabelaHash.seek(endInserir*tamanhoRegistro);
            novoRegistro.salva(tabelaHash);
            tabelaHash.close();
            return endInserir;
        }
        else {
            return -1; // inserçao invalida: chave ja existe
        }        
    }

    /**
    * Executa exclusão em Arquivos por Encadeamento Exterior (Hash)
    * @param codReg: código do registro a ser excluído
    * @param tabela tabela da qual o registro está sendo excluído
    * @return endereço do registro que foi excluído, -1 se registro não existe
    * @throws java.io.FileNotFoundException
    * @throws java.io.IOException
    */
    public int exclui(int codReg, Tabela tabela) throws FileNotFoundException, IOException {
        Result resultBusca = busca(codReg,tabela);
        if (resultBusca.getA() == 1) {
            int tamanhoRegistro = tabela.getTamanhoRegistro();
            String nomeArquivoHash = getNomeArquivoHash(tabela);
            RandomAccessFile tabelaHash = new RandomAccessFile(nomeArquivoHash,"rw");
            tabelaHash.seek(resultBusca.getEnd()*tamanhoRegistro);
            Registro registro = Registro.le(tabelaHash,tabela);
            registro.setFlag(Registro.LIBERADO);
            tabelaHash.seek(resultBusca.getEnd()*tamanhoRegistro);
            registro.salva(tabelaHash);
            tabelaHash.close();
            return resultBusca.getEnd();
        }
        else { //registro não existe
            return -1;
        }
    }
    
    public void modifica(int codReg, List<Valor> valoresAtributos, Tabela tabela) throws FileNotFoundException, IOException{
        Result resultBusca = busca(codReg,tabela);
        int tamanhoRegistro = tabela.getTamanhoRegistro();
        String nomeArquivoHash = getNomeArquivoHash(tabela);
        RandomAccessFile tabelaHash = new RandomAccessFile(nomeArquivoHash,"rw");
        tabelaHash.seek(resultBusca.getEnd()*tamanhoRegistro);
        Registro registro = Registro.le(tabelaHash, tabela);
        Registro registroModficado = new Registro(codReg, valoresAtributos, registro.getProx(), Registro.OCUPADO);
        tabelaHash.seek(resultBusca.getEnd()*tamanhoRegistro);
        registroModficado.salva(tabelaHash);
        tabelaHash.close();
    }

}
