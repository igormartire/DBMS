
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
public class No {

    NoConteudo info;
    No esq, dir;

    public No() {
        esq = dir = null;
        info = new NoConteudo();
    }

    public No criaArvore(No[] nos) {
        No pai = new No();
        No[] nosTemp;
        if (nos.length == 0) {
            return null;
        }
        if (nos.length == 1) {
            return nos[0];
        }
        if (nos.length % 2 == 0) {
            nosTemp = new No[nos.length / 2];
            for (int i = 0; i < (nosTemp.length); i++) {
                nosTemp[i] = nos[i];
            }
            pai.esq = criaArvore(nosTemp);

            for (int i = nosTemp.length; i < nos.length; i++) {
                nosTemp[i] = nos[i];
            }
            pai.dir = criaArvore(nosTemp);
        } else {
            nosTemp = new No[nos.length - 1];
            for (int i = 0; i < nosTemp.length; i++) {
                nosTemp[i] = nos[i];
            }

            pai.esq = criaArvore(nosTemp);
            pai.dir = nos[nos.length - 1];
        }

        if (pai.esq.info.info.codCliente < pai.dir.info.info.codCliente) {
            pai.info = pai.esq.info;
        } else {
            pai.info = pai.dir.info;
        }
        return pai;
    }

    public void salvaArvore(DataOutputStream out) throws IOException {
        /* 1º salva o vencedor, então passa a salvar apos a iteração, permitindo 
         * que ao chegar ao HV, este também seja salvo no arquivo
         */
        this.info.salva(out);

        while (this.info.info.codCliente != Integer.MAX_VALUE) {
            this.itera();
            this.info.salva(out);
        }
    }

    private void itera() throws IOException {
        if (this.esq == null && this.dir == null) {
            this.info.atualizar();
        } else {
            if (this.info.info.codCliente == this.esq.info.info.codCliente) {
                this.esq.itera();
            } else {
                this.dir.itera();
            }
            if (this.esq.info.info.codCliente < this.dir.info.info.codCliente) {
                this.info = this.esq.info;
            } else {
                this.info = this.dir.info;
            }
        }

    }
}
