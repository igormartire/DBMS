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

    private static void opCriarTabela() {
        System.out.println("\n----------------------------\n");        
        
        //Nome e chave da tabela
        Tabela tabela = null;
        try {
            System.out.print("Entre com o nome da tabela: ");
            String nomeTabela = scan.next();
            
            //Se já existe tabela no banco de dados com o nome desejado, cancela a criação da nova tabela
            if(getTabelaByName(nomeTabela) != null) 
                throw new IllegalArgumentException("Tabela com esse nome ja existe!");
                
            System.out.print("Entre com o nome do atributo chave (o tipo eh inteiro): ");
            String nomeChave = scan.next();
            
            tabela = new Tabela(nomeTabela,nomeChave);
        }
        catch (IllegalArgumentException ex){
            System.out.println(ex.getMessage());
        }
        
        if(tabela == null) {
            System.out.println("Criacao da tabela cancelada.");
            return;        
        }
        
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
            System.out.print("Entre com o nome do atributo: ");
            String nomeAtributo = scan.next();   
            
            //Se já foi adicionado um atributo com o mesmo nome, pede outro nome para o atributo.
            if((nomeAtributo.equalsIgnoreCase(tabela.getChave())) || (tabela.getAtributoByName(nomeAtributo) != null)) {
                System.out.println("Já há um atributo com esse nome. Escolha outro nome.");
                continue;
            }
            
            try {
                Atributo a = null;
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
    
    // Retorna uma tabela pelo nome dela
    private static Tabela getTabelaByName(String nomeTabela) {
        boolean found = false;
        Tabela tabela = null;
        DataInputStream in = null;
        try {
            in = new DataInputStream(new BufferedInputStream(new FileInputStream(arquivoCatalogo)));
            while(!found) {
                tabela = Tabela.le(in);
                if(tabela.getNome().equalsIgnoreCase(nomeTabela))
                    found = true;
            }
        } catch (EOFException ex) {
        } catch(FileNotFoundException ex) {
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
        if(found)
            return tabela;
        else
            return null;
    }
}
