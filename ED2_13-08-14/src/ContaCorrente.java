
import java.io.DataOutputStream;
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

    void salva(DataOutputStream out) throws IOException {
        out.writeInt(this.cod);
        out.writeInt(this.codAgencia);
        out.writeDouble(this.saldo);
        System.out.println("Conta Corrente salva com sucesso!");
    }
}
