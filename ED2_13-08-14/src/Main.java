
import java.io.IOException;
import java.util.Scanner;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Daniel, Gabriel, Igor, Lucas
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        boolean sair = false;

        while (!sair) {
            int op;
            op = getOpcao();

            switch (op) {
                case 1:
                    menuCadastro();
                    break;
                case 2:
                    //ler
                    break;
                case 3:
                    sair = true;
                    break;
                default:
                    System.out.println("Opcao invalida.\n");
                    break;
            }
        }
    }

    private static int getOpcao() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Entre com a opcao desejada:\n"
                + "1- Cadastrar\n"
                + "2- Ler\n"
                + "3- Sair\n");
        System.out.print("Opcao: ");
        int op = scan.nextInt();
        return op;
    }

    private static void menuCadastro() throws IOException{
        Scanner scan = new Scanner(System.in);
        System.out.println("O que voce deseja cadastrar:\n"
                + "1- Agencia\n"
                + "2- Conta Corrente\n"
                + "3- Voltar\n");
        System.out.print("Opcao: ");
        int op = scan.nextInt();
        switch (op) {
            case 1:
                cadastraAgencia();
                break;
            case 2:
                cadastraContaCorrente();
                break;
            case 3:
                return;
            default:
                System.out.println("Opcao invalida.\n");
                break;
        }
    }
    
    private static void cadastraAgencia() throws IOException{
        Scanner scan = new Scanner(System.in);
        System.out.print("Entre com o codigo da agencia: ");
        int cod = scan.nextInt();
        System.out.print("Entre com o nome da agencia: ");
        String nome = scan.next();
        System.out.print("Entre com o nome do gerente: ");
        String gerente = scan.next();
        Agencia ag = new Agencia(cod,nome,gerente);
        ag.salva();
    }
    
    private static void cadastraContaCorrente() throws IOException{
        Scanner scan = new Scanner(System.in);
        System.out.print("Entre com o codigo da conta corrente: ");
        int cod = scan.nextInt();
        System.out.print("Entre com o codigo da agencia: ");
        int codAgencia = scan.nextInt();
        System.out.print("Entre com o saldo: ");
        double saldo = scan.nextDouble();
        ContaCorrente cc = new ContaCorrente(cod,codAgencia,saldo);
        cc.salva();
    }
}
