
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

    //Cria uma arvore de vencedores já toda preenchida
    public No criaArvore(No[] nos) {
        No pai = new No();
        No[] nosTemp;
        //caso o vetor passado esteja vazio
        if (nos.length == 0) {
            return null;
        }
        //caso onde o no será uma folha
        if (nos.length == 1) {
            return nos[0];
        }
        /* caso onde a arvore passada tem base n par, criamos então uma 
         * arvore n/2 para cada filho
         */
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
        } 
        //caso onde a arvore passada tem base n impar, criamos então uma
        //arvore n-1 no filho da esquerda e criamos uma folha no da direita
        else {
            nosTemp = new No[nos.length - 1];
            for (int i = 0; i < nosTemp.length; i++) {
                nosTemp[i] = nos[i];
            }

            pai.esq = criaArvore(nosTemp);
            pai.dir = nos[nos.length - 1];
        }

        //verificamos por fim qual dos filhos é o menor e passamos a referência
        //para seu conteudo para o no pai
        if (pai.esq.info.info.codCliente < pai.dir.info.info.codCliente) {
            pai.info = pai.esq.info;
        } else {
            pai.info = pai.dir.info;
        }
        return pai;
    }

    //salva a arvore, pelo algoritmo de intercalação, no arquivo de saída passado
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

    //auxilia o salvamento da arvore aplicando o algoritmo de intercalação
    private void itera() throws IOException {
        //Se for folha, atualizamos o conteudo dela
        if (this.esq == null && this.dir == null) {
            this.info.atualizar();
        } 
        //Se não for fazemos uma nova chamada para o filho de conteudo igual ao pai
        else {
            if (this.info.info.codCliente == this.esq.info.info.codCliente) {
                this.esq.itera();
            } else {
                this.dir.itera();
            }
            //E então verificamos qual dos filhos é o menor agora e passamos a
            // referência para seu conteudo para o no pai
            if (this.esq.info.info.codCliente < this.dir.info.info.codCliente) {
                this.info = this.esq.info;
            } else {
                this.info = this.dir.info;
            }
        }

    }
}
