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

    void salva() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
