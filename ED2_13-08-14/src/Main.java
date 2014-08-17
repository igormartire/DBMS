import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.EOFException;
import java.io.FileNotFoundException; 
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;


/**
 * @author Daniel Prett, Gabriel Saldanha, Igor Martire, Lucas Barros
 */
public class Main {
    
    private static final String arquivoAgencias = "agencias.dat";
    private static final String arquivoContas = "contas.dat";
    private static Scanner scan;

    public static void main(String[] args) {
        
        System.out.println("#####################\n"
                         + "# ED2 - Exercicio 1 #\n"
                         + "# Grupo:            #\n"
                         + "# *Daniel Campagna  #\n"
                         + "# *Gabriel Saldanha #\n"
                         + "# *Igor Martire     #\n"
                         + "# *Lucas Barros     #\n"
                         + "#####################\n");

        scan = new Scanner(System.in);
        boolean sair = false;

        while (!sair) {
            int op;
            op = getOpcao();

            switch (op) {
                case 1:
                    menuCadastro();
                    break;
                case 2:
                    menuLeitura();
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
        System.out.println("Entre com a opcao desejada:\n"
                + "1- Cadastrar\n"
                + "2- Ler\n"
                + "3- Sair\n");
        System.out.print("Opcao: ");
        int op = scan.nextInt();
        System.out.println("\n----------------------------\n");
        return op;
    }

    private static void menuCadastro() {
        boolean sair = false;
        while(!sair) { 
            System.out.println("O que voce deseja cadastrar?\n"
                    + "1- Agencia\n"
                    + "2- Conta-corrente\n"
                    + "3- Voltar\n");
            System.out.print("Opcao: ");
            int op = scan.nextInt();
            System.out.println("\n----------------------------\n");
            switch (op) {
                case 1:
                    cadastraAgencia();
                    break;
                case 2:
                    cadastraContaCorrente();
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
    
    private static void cadastraAgencia() {
        System.out.print("Entre com o codigo da agencia: ");
        int cod = scan.nextInt();
        System.out.print("Entre com o nome da agencia: ");
        String nome = scan.next();
        System.out.print("Entre com o nome do gerente: ");
        String gerente = scan.next();
        Agencia ag = new Agencia(cod,nome,gerente);
        
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(arquivoAgencias, true)));
            ag.salva(out);
            System.out.println("\nAgencia salva com sucesso!");
        }
        catch (IOException ex) { 
            System.err.println("Erro na escrita do arquivo "+arquivoAgencias+": "+ex.getMessage());
        }
        finally {
            if (out != null) {
                try {
                    out.close();
                } 
                catch (IOException ex) {
                    System.err.println("Erro no fechamento do arquivo "+arquivoAgencias+": "+ex.getMessage());
                }
            }
        }
        System.out.println("\n----------------------------\n");
    }
    
    private static void cadastraContaCorrente() {
        System.out.print("Entre com o codigo da conta-corrente: ");
        int cod = scan.nextInt();
        System.out.print("Entre com o codigo da agencia: ");
        int codAgencia = scan.nextInt();
        System.out.print("Entre com o saldo: ");
        double saldo = scan.nextDouble();
        ContaCorrente cc = new ContaCorrente(cod,codAgencia,saldo);
         
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(arquivoContas, true)));   
            cc.salva(out);
            System.out.println("Conta-corrente salva com sucesso!");
        } catch (IOException ex) { 
            System.err.println("Erro na escrita do arquivo "+arquivoContas+": "+ex.getMessage());
        } finally {
            if (out != null){
                try {
                    out.close();
                } 
                catch (IOException ex) {
                    System.err.println("Erro no fechamento do arquivo "+arquivoContas+": "+ex.getMessage());
                }            
            }
        }
        System.out.println("\n----------------------------\n");
    }

    private static void menuLeitura() {
        boolean sair = false;
        while(!sair) { 
            System.out.println("O que voce deseja ler?\n"
                    + "1- Agencias\n"
                    + "2- Contas-correntes\n"
                    + "3- Voltar\n");
            System.out.print("Opcao: ");
            int op = scan.nextInt();
            System.out.println("\n----------------------------\n");
            switch (op) {
                case 1:
                    leAgencias();
                    break;
                case 2:
                    leContaCorrentes();
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
    
    private static void leAgencias() {
        Agencia ag = new Agencia();
        DataInputStream in = null;
        try {
            in = new DataInputStream(new BufferedInputStream(new FileInputStream(arquivoAgencias)));
            while(true) {
                ag.le(in);
                System.out.println("Codigo da agencia: "+ag.getCod());
                System.out.println("Nome da agencia: "+ag.getNome());
                System.out.println("Gerente da agencia: "+ag.getGerente());
                System.out.println();
            }
        }
        catch (EOFException ex) {
            System.out.println("Leitura finalizada com sucesso!");
        }
        catch (FileNotFoundException ex) {
            System.out.println("Nao existe nenhum registro de agencia.");
        }
        catch (IOException ex) { 
            System.err.println("Erro na escrita do arquivo "+arquivoAgencias+": "+ex.getMessage());
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                } 
                catch (IOException ex) {
                    System.err.println("Erro no fechamento do arquivo "+arquivoAgencias+": "+ex.getMessage());
                }            
            }
        }
        System.out.println("\n----------------------------\n");
    }
    
    private static void leContaCorrentes() {
        ContaCorrente cc = new ContaCorrente();
        DataInputStream in = null;
        try {
            in = new DataInputStream(new BufferedInputStream(new FileInputStream(arquivoContas)));
            while(true) {
                cc.le(in);
                System.out.println("Codigo da conta-corrente: "+cc.getCod());
                System.out.println("Codigo da agencia da conta-corrente: "+cc.getCodAgencia());
                System.out.println("Saldo da conta-corrente: "+cc.getSaldo());
                System.out.println();
            }
        }
        catch (EOFException ex) {
            System.out.println("Leitura finalizada com sucesso!");
        }
        catch (FileNotFoundException ex) {
            System.out.println("Nao existe nenhum registro de conta-corrente.");
        }
        catch (IOException ex) { 
            System.err.println("Erro na escrita do arquivo "+arquivoContas+": "+ex.getMessage());
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                } 
                catch (IOException ex) {
                    System.err.println("Erro no fechamento do arquivo "+arquivoContas+": "+ex.getMessage());
                }
            }
        }
        System.out.println("\n----------------------------\n");
    }
}
