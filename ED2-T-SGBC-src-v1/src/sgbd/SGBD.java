package sgbd;

import java.util.Scanner;
import dominio.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
* @author Daniel Prett, Gabriel Saldanha, Igor Martire, Lucas Barros
*/
public class SGBD {

    private static final int OP_CRIAR_TABELA = 1;
    private static final int OP_MOSTRAR_TABELAS = 2;
    private static final int OP_SAIR = 3;
    private static final String arquivoCatalogo = "catalogo.dat";   
    private static Scanner scan;    
    
    /**
    * Interface com o usuário
    */
    public static void main(String[] args) {
        System.out.println  ("#####################\n"
                            +"# ED2 - SGBD        #\n"
                            +"# Grupo:            #\n"
                            +"# *Daniel Campagna  #\n"
                            +"# *Gabriel Saldanha #\n"
                            +"# *Igor Martire     #\n"
                            +"# *Lucas Barros     #\n"
                            +"#####################\n");
        scan = new Scanner(System.in);
        boolean sair = false;
        while (!sair) {
            System.out.println  ("Entre com a opcao desejada:\n"
                                +OP_CRIAR_TABELA+"- Criar tabela\n"
                                +OP_MOSTRAR_TABELAS+"- Mostrar tabelas\n"
                                +OP_SAIR+"- Sair\n");
            
            System.out.print("Opcao: ");
            int op = scan.nextInt();  
            switch (op) {
                case OP_CRIAR_TABELA:
                    opCriarTabela();
                    break;
                case OP_MOSTRAR_TABELAS:
                    opMostrarTabelas();
                    break;
                case OP_SAIR:
                    sair = true;
                    break;
                default:
                    System.out.println("\nOpcao invalida.");
                break;
            }
            System.out.println("\n----------------------------\n");
        }
    }

    //TODO: Não pode deixar criar tabela que tenha o mesmo nome de alguma tabela já existente
    //TODO: Não pode deixar criar atributo com mesmo nome da chave 
    //TODO: Não pode deixar criar dois atributos com o mesmo nome na mesma tabela
    private static void opCriarTabela() {
        System.out.println("\n----------------------------\n");        
        
        //Nome e chave da tabela
        Tabela tabela = null;
        try {
            System.out.print("Entre com o nome da tabela: ");
            String nomeTabela = scan.next();
            System.out.print("Entre com o nome do atributo chave (o tipo eh inteiro): ");
            String nomeChave = scan.next();
            tabela = new Tabela(nomeTabela,nomeChave);
        }
        catch (IllegalArgumentException ex){
            System.out.println(ex.getMessage());
            System.out.println("Criacao da tabela cancelada.");
            return;
        }
        if(tabela == null) return;        
        
        //Adição de atributos
        boolean fim;
        System.out.print("Deseja adicionar mais um atributo? (s/n): ");
        String maisAtributo = scan.next();
        if(maisAtributo.startsWith("s")){
            fim = false;
        }
        else {                
            fim = true;
        }
        while(!fim) {            
            Atributo a = null;
            System.out.print("Entre com o nome do atributo: ");
            String nomeAtributo = scan.next();   
            
            try {
                boolean repete;
                do {
                    repete = false;
                    System.out.print("Entre com o tipo do atributo (1 para inteiro e 2 para texto): ");
                    int opTipoAtributo = scan.nextInt();                        
                    switch(opTipoAtributo){
                        case 1:
                            a = new Atributo(nomeAtributo,Atributo.TIPO_INTEIRO);
                            break;
                        case 2:
                            a = new Atributo(nomeAtributo,Atributo.TIPO_TEXTO);
                            break;
                        default:                    
                            System.out.println("Opcao invalida");
                            repete = true;
                            break;
                    }
                } while(repete);
                tabela.addAtributo(a);
            }
            catch (IllegalArgumentException ex){
                System.out.println(ex.getMessage());
            }            
                        
            System.out.print("Deseja adicionar mais um atributo? (s/n): ");
            maisAtributo = scan.next();
            if(maisAtributo.startsWith("s")){
                fim = false;
            }
            else {                
                fim = true;
            }            
        }
        
        //Gravação da tabela no arquivo de catálogo        
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(arquivoCatalogo,true)));
            tabela.salva(out);
            System.out.println("\nTabela criada com sucesso!");
        } catch (IOException ex) {            
            System.out.println(ex.getMessage());            
            System.out.println("\nFalha na criacao da tabela!");
        } finally {
            if(out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        
    }

    private static void opMostrarTabelas() {
        System.out.println("\n----------------------------\n");  
        
        Tabela tabela;
        DataInputStream in = null;        
        try {
            in = new DataInputStream(new BufferedInputStream(new FileInputStream(arquivoCatalogo)));
            while(true){
                tabela = Tabela.le(in);            
                System.out.println(tabela);
                System.out.println("*******");
            }
        } catch (EOFException ex) {
            System.out.println("Todas as tabelas foram listadas.");
        } catch (FileNotFoundException ex) {
            System.out.println("Nao há tabelas no banco de dados.");
        } catch (IOException ex) {            
            System.out.println(ex.getMessage());            
        } finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
    }
}
