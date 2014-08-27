
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Lucas
 */
public class Main {

    /*
     * 1- Criar a árvore (n=1 -> retorna no com filhos nulos, n=par -> retorna no com filhos de cria(n/2), n=ímpar -> retorna no com filho direito cria(1) e filho esqurerdo cria(n-1))
     * 2- Preencher a árvore (quase a mesma iteração de baixo, só q faz pros dois filhos, sem o if de filho de mesmo valor)
     * 3- Iterar salvando 1º (iteração: se folha, le. Se não, itera pro filho de igual valor e depois compara os filhos)
     * 4- Salvar HV no final do arquivo 
     */
    public static void main(String[] args) {
        No[] nos = new No[args.length - 1];
        No arvore = new No();

        try {
            for (int i = 0; i < nos.length; i++) {
                nos[i] = new No();
                nos[i].info.in = new DataInputStream(new BufferedInputStream(new FileInputStream(args[i])));
                nos[i].info.atualizar();
            }
            arvore = arvore.criaArvore(nos);
            
            DataOutputStream saida = new DataOutputStream(new BufferedOutputStream(
                    new FileOutputStream(args[args.length-1])));
            arvore.salvaArvore(saida);
            
            saida.close();
            
            for (int i = 0; i < nos.length; i++) {
                nos[i].info.in.close();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}