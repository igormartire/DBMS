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
 * @author igormartire
 */
public class Valor {    
    public static final int TAMANHO_LIMITE_TEXTO = 10;
    
    private int tipo;    
    private int valorInteiro;
    private String valorTexto;
    
    public Valor(int v) {
        this.setTipo(Atributo.TIPO_INTEIRO);
        this.setValorInteiro(v);
    }
    
    public Valor(String v) {
        this.setTipo(Atributo.TIPO_TEXTO);
        while (v.length() < TAMANHO_LIMITE_TEXTO) {
            v += " ";
        }
        this.setValorTexto(v);
    }
    
    /**
     * Salva um Valor no arquivo out, na posição atual do cursor
     * @param out aquivo onde os dados serão gravados
     */
    public void salva(RandomAccessFile out) throws IOException {
        if (this.tipo == Atributo.TIPO_INTEIRO)
            out.writeInt(valorInteiro);
        else
            out.writeUTF(valorTexto);
    }
    
    /**
     * Lê um Valor do arquivo in na posição atual do cursor
     * e retorna uma instância de Valor populada com os dados lidos do arquivo
     * @param in Arquivo de onde os dados serão lidos
     * @param tipoValor Tipo do valor a ser lido
     * @return instância de Valor populada com os dados lidos
     */
    public static Valor le(RandomAccessFile in, int tipoValor) throws IOException {                   
        Valor valor;
        switch(tipoValor) {
            case Atributo.TIPO_INTEIRO:
                valor = new Valor(in.readInt());
                break;
            default: // TIPO_TEXTO
                valor = new Valor(in.readUTF());
                break;
        }  
        return valor;
    }
    
    /**
     * Gera uma String com uma representação de um Valor
     */
    @Override
    public String toString() {
        String text = this.valorTexto;
        if (this.tipo == Atributo.TIPO_INTEIRO) {
            text = String.valueOf(this.valorInteiro);
        }
        return text;
    }
   
    public int getTipo() {
        return tipo;
    }
    
    private void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getValorTexto() {
        return valorTexto;
    }

    private void setValorTexto(String valorTexto) {
        if(valorTexto.length() > TAMANHO_LIMITE_TEXTO) {
            throw new IllegalArgumentException("[Erro] Um atributo do tipo texto nao pode ter valor com mais de "+TAMANHO_LIMITE_TEXTO+" caracteres.");
        }
        else {
            this.valorTexto = valorTexto;
        }
    }

    public int getValorInteiro() {
        return valorInteiro;
    }

    private void setValorInteiro(int valorInteiro) {
        this.valorInteiro = valorInteiro;
    }
}
