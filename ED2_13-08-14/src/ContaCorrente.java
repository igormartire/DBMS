
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Lucas
 */
public class ContaCorrente {
    private int cod;
    private int codAgencia;
    private double saldo;

    public ContaCorrente(int cod, int codAgencia, double saldo) {
        this.cod = cod;
        this.codAgencia = codAgencia;
        this.saldo = saldo;
    }

    public int getCod() {
        return cod;
    }

    public int getCodAgencia() {
        return codAgencia;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(float saldo) {
        this.saldo = saldo;
    }

    void salva() throws IOException {
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("contascorrentes.dat")));
            
            out.writeInt(this.cod);
            out.writeInt(this.codAgencia);
            out.writeDouble(this.saldo);
            
        } catch (FileNotFoundException ex) {
            //NOTHING
        } finally {
            if (out != null){
                out.close();
            }
        }
    }
}
