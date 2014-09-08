/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dominio;

/**
 *
 * @author Daniel, Gabriel, Igor, Lucas
 */
public class Atributo {
    
    public static final String TIPO_INTEIRO = "inteiro";
    public static final String TIPO_TEXTO = "texto";
    
    private String nome;
    private String tipo;

    public Atributo(String nome, String tipo) throws IllegalArgumentException{
        this.setNome(nome);
        this.setTipo(tipo);
    }

    public String getNome() {
        return nome;
    }

    public String getTipo() {
        return tipo;
    }

    private void setNome(String nome) throws IllegalArgumentException{
        if(nome == null || nome.trim().equals("")){
            throw new IllegalArgumentException("[ERRO] valor de atributo\n"
                    + this.getClass().toString() + ".nome nao pode receber um valor nulo"
                    + " ou vazio (\"\").");
        }
        this.nome = nome;
    }
    
    private void setTipo(String tipo) throws IllegalArgumentException{
        if(tipo == null || tipo.equals("")){
            throw new IllegalArgumentException("[ERRO] Valor de atributo:\n"
                    + this.getClass().toString() + ".tipo nao pode receber um valor nulo"
                    + " ou vazio (\"\").");
        }
        if(!tipo.equals(Atributo.TIPO_INTEIRO) && !tipo.equals(Atributo.TIPO_TEXTO)){
            throw new IllegalArgumentException("[ERRO] Valor de atributo:\n"
                    + this.getClass().toString() + ".tipo deve ser um "
                    + Atributo.TIPO_INTEIRO + " ou " + Atributo.TIPO_TEXTO);
        }
        this.tipo = tipo;
    }
    
}
