/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dominio;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Daniel, Gabriel, Igor, Lucas
 */
public class Tabela {
    
    private String nome;
    private String chave;
    private List<Atributo> atributos;

    public Tabela(String nome, String chave) {
        this(nome, chave, new ArrayList<Atributo>());
    }

    public Tabela(String nome, String chave, List<Atributo> atributos) {
        this.setNome(nome);
        this.setChave(chave);
        this.setAtributos(atributos);
    }

    public String getNome() {
        return nome;
    }

    public String getChave() {
        return chave;
    }

    public List<Atributo> getAtributos() {
        return atributos;
    }

    private void setNome(String nome) throws IllegalArgumentException{
        if(nome == null || nome.equals("")){
            throw new IllegalArgumentException("[ERRO] Valor de atributo:\n"
                    + this.getClass().toString() + ".nome nao pode"
                    + " receber um valor nulo ou vazio (\"\").");
        }
        this.nome = nome;
    }

    private void setChave(String chave) throws IllegalArgumentException{
        if(chave == null || chave.equals("")){
            throw new IllegalArgumentException("[ERRO] Valor de atributo:\n"
                    + this.getClass().toString() + ".chave nao pode"
                    + " receber um valor nulo ou vazio (\"\").");
        }
        this.chave = chave;
    }

    private void setAtributos(List<Atributo> atributos) throws IllegalArgumentException{
        if(atributos == null){
            throw new IllegalArgumentException("[ERRO] Valor de atributo:\n"
                    + this.getClass().toString() + ".atributos nao pode"
                    + " receber um valor nulo");
        }
        this.atributos = atributos;
    }
    
}
