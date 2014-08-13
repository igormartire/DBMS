
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Lucas
 */
public class Agencia {
    private int cod;
    private String nome;
    private String gerente;

    public Agencia(int cod, String nome, String gerente) {
        this.cod = cod;
        this.nome = nome;
        this.gerente = gerente;
    }

    public int getCod() {
        return cod;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getGerente() {
        return gerente;
    }

    public void setGerente(String gerente) {
        this.gerente = gerente;
    }

    void salva() throws IOException{
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("agencias.dat")));
        } catch (FileNotFoundException ex) {
            //NOTHING
        } finally {
            if (out != null){
                out.close();
            }
        }
    }
    
    
    
}
