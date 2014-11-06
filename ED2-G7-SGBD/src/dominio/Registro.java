/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dominio;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Daniel Prett, Gabriel Saldanha, Igor Martire, Lucas Barros
 */
public class Registro {
    public static boolean LIBERADO = true;
    public static boolean OCUPADO = false;
    
    private int valorChave;
    private List<Valor> valoresAtributos;    
    private int prox;
    private boolean flag;
        
    public Registro(int valorChave, List<Valor> valoresAtributos, int prox, boolean flag) {
        this.setValorChave(valorChave);
        this.setValoresAtributos(valoresAtributos);
        this.setProx(prox);
        this.setFlag(flag);
    }
    
    /**
     * Salva um Registro no arquivo out, na posição atual do cursor
     * @param out aquivo onde os dados serão gravados
     */
    public void salva(RandomAccessFile out) throws IOException {
        out.writeInt(valorChave);
        for (Valor v : valoresAtributos) {
            v.salva(out);
        }        
        out.writeInt(prox);
        out.writeBoolean(flag);
    }

    /**
     * Lê um Registro do arquivo in na posição atual do cursor
     * e retorna uma instância de Registro populada com os dados lidos do arquivo
     * @param in Arquivo de onde os dados serão lidos
     * @param tabela Tabela a qual o registro se refere
     * @return instância de Registro populada com os dados lidos
     */
    public static Registro le(RandomAccessFile in, Tabela tabela) throws IOException {
        int valorChave = in.readInt();
        List<Valor> valoresAtributos = new ArrayList<Valor>();
        for(Atributo atr : tabela.getAtributos()) {
            valoresAtributos.add(Valor.le(in,atr.getTipo()));
        }
        int prox = in.readInt();
        boolean flag = in.readBoolean();
        return new Registro(valorChave, valoresAtributos, prox, flag);
    }
    
    /**
     * Gera uma String com uma representação de um Registro
     */
    @Override
    public String toString() {
        String s;
        if (this.flag == Registro.LIBERADO) {
            s = "LIBERADO";
        } else {
            s = "OCUPADO";
        }
        String text = String.valueOf(this.valorChave);
        for (Valor v : this.valoresAtributos) {
            text += ", " + v;
        }
        text += ", " + this.prox + ", " + s;
        return text;
    }
    
    /**
     * Retorna o valor desejado atraves do indice passado como parametro
     * @param index index -1 se refere ao valor da chave; index >=0 se refere ao index correspondente na lista valoresAtributos
     * @return Valor selecionado
     */
    public Valor getValorByIndex(int index) {
        if (index == -1) {
            return new Valor(this.getValorChave());
        }
        else {
            return this.getValoresAtributos().get(index);
        }
    }

    public int getValorChave() {
        return valorChave;
    }

    private void setValorChave(int valorChave) {
        this.valorChave = valorChave;
    }

    public List<Valor> getValoresAtributos() {
        return valoresAtributos;
    }

    private void setValoresAtributos(List<Valor> valoresAtributos) {
        this.valoresAtributos = valoresAtributos;
    }

    public int getProx() {
        return prox;
    }

    public void setProx(int prox) {
        this.prox = prox;
    }
    
    public boolean getFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String toString(List<Integer> indexAtributosSelecionados) {        
        String str = "|";
        for (Integer index : indexAtributosSelecionados) {
            str += " ";
            Valor v = this.getValorByIndex(index);
            if (v.getTipo() == Atributo.TIPO_INTEIRO) {
                String strI = String.valueOf(v.getValorInteiro());
                while (strI.length() < Valor.TAMANHO_LIMITE_TEXTO) {
                    strI += " ";
                }
                str += strI;
            }
            else { // TIPO_TEXTO
                str += v.getValorTexto();
            }
            str += " |";
        }
        return str;
    }
}
