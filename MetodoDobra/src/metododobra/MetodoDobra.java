package metododobra;

import java.util.Scanner;

/**
 *
 * @author igormartire
 */
public class MetodoDobra {

    /**
     * M : O número máximo de dígitos da chave
     * D : O tamanho de cada dobra
     * F : O tamanho máximo do endereco retornado pelo método da dobra
     * Restrição.: M e F devem ser múltiplos de D
     * Restrição.: M, D e F devem ser números naturais não-nulos
     */
    private static final int M = 6;
    private static final int D = 2;
    private static final int F = 2;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Metodo da Dobra");        
        System.out.print("Entre com a chave (max. "+M+" digitos): ");
        Scanner input = new Scanner(System.in);
        int chave = input.nextInt();
        MetodoDobra metodo = new MetodoDobra();
        System.out.println("O endereco eh: "+metodo.dobra(chave));
    }
    
    /**
     * @param chave a chave na qual será aplicada o método da dobra
     */
    int dobra(int chave){
        //Cria o vetor com a chave (já preenchendo com zeros quando necessário)
        int[] vet = new int[M];
        for(int i = (M-1); i >= 0; i--) {
            vet[i] = chave % 10;
            chave /= 10;
        }
        
        //Dobra o número correto de vezes
        int numDobras = (M-F)/D;
        for(int i = 0; i < numDobras ; i++) {
            vet = uma_dobra(vet);
        }
        
        //Transforma de vetor para inteiro
        int resultado = vet[0];
        for(int i = 1; i < vet.length; i++) {
            resultado *= 10;
            resultado += vet[i];
        }
        return resultado;
    }

    /**
     * @param vet array de inteiros que sofrerá uma dobra
     */
    int[] uma_dobra(int[] vet) {        
        //Copia o array sem a parte que será eliminada pela dobra
        int novo_tam = vet.length-D;
        int novo[] = new int[novo_tam];
        System.arraycopy(vet, D, novo, 0, novo_tam);
        
        //Soma a parte eliminada com a parte restante, de acordo com o método da dobra
        for(int i = 0; i < D; i++){
            novo[D-1-i] = (novo[D-1-i] + vet[i])%10;
        }
        
        return novo;
    }
}
