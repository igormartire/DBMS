package sgbd;

/**
* @author Daniel Prett, Gabriel Saldanha, Igor Martire, Lucas Barros
*/
public class Main {

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
                                +OP_CRIAR_TABELA"- Criar tabela\n"
                                +OP_MOSTRAR_TABELAS"- Mostrar tabelas\n"
                                +OP_SAIR+"- Sair\n");
            int op;
            op = getOpcao();
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
                    System.out.println("Opcao invalida.\n");
                break;
            }
        }
    }
    
    /*
    * Pede opção ao usuário, a retorna e adiciona quebra de linha
    */
    private static int getOpcao() {
        System.out.print("Opcao: ");
        int op = scan.nextInt();
        System.out.println("\n----------------------------\n");
        return op;
    }
}
