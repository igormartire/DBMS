/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dominio;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Daniel Prett, Gabriel Saldanha, Igor Martire, Lucas Barros
 */
public class Atributo {
    
    public static final int TIPO_INTEIRO = 1;
    public static final int TIPO_TEXTO = 2;
    public static final int TIPO_MARCADOR = 3;
    
    private String nome;
    private int tipo;

    public Atributo(String nome, int tipo) throws IllegalArgumentException{
        this.setNome(nome);
        this.setTipo(tipo);
    }

    public String getNome() {
        return nome;
    }

    public int getTipo() {
        return tipo;
    }

    private void setNome(String nome) throws IllegalArgumentException{
        if(nome == null || nome.trim().equals("")){
            throw new IllegalArgumentException("[ERRO] Valor de atributo\n"
                    + this.getClass().toString() + ".nome nao pode receber um valor nulo"
                    + " ou vazio (\"\").");
        }
        this.nome = nome;
    }
    
    private void setTipo(int tipo) throws IllegalArgumentException{
        if(tipo!=TIPO_INTEIRO && tipo!=TIPO_TEXTO && tipo!=TIPO_MARCADOR){
            throw new IllegalArgumentException("[ERRO] Valor de atributo:\n"
                    + this.getClass().toString() + ".tipo deve ser um "
                    + Atributo.TIPO_INTEIRO + " ou " + Atributo.TIPO_TEXTO);
        }
        this.tipo = tipo;
    }

    void salva(DataOutputStream out) throws IOException {
        out.writeUTF(nome);
        out.writeInt(tipo);
    }
    
    public static Atributo le(DataInputStream in) throws IOException {
        String nome = in.readUTF();
        int tipo = in.readInt();
        return new Atributo(nome,tipo);
    }
    
    @Override
    public String toString(){
        String str = this.nome + " : ";
        if(this.tipo == TIPO_INTEIRO){
            str+="Inteiro";
        }
        else {
            str+="Texto";
        }
        return str;
    }
}
