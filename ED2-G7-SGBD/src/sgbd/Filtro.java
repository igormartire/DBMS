package sgbd;

import dominio.*;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author igormartire
 */
public class Filtro {
    public static final int OPER_IGUAL = 1;
    public static final int OPER_DIFERENTE = 2;
    public static final int OPER_MAIOR = 3;
    public static final int OPER_MAIOR_OU_IGUAL = 4;
    public static final int OPER_MENOR = 5;    
    public static final int OPER_MENOR_OU_IGUAL = 6;
        
    private final int indexAtributo;
    private final int operador;
    private final Valor operando;
    
    /**
     * @param indexAtributo indice do atributo a ser filtrado (-1 = chave ; 0..n = mesmo indice da lista de atributos)
     * @param operador código do operador do filtro
     * @param operando valor do operando do filtro
     */
    public Filtro(int indexAtributo, int operador, Valor operando){
        this.indexAtributo = indexAtributo;
        this.operador = operador;
        this.operando = operando;
    }
    
    /**
    * Verifica se o Registro é filtrado pelo filtro.    * 
    * Ser filtrado significa que o registro atende à condição estabelecida pelo filtro.
    * @param registro registro a ser verificado
    * @return retorna true se for filtrado e retorna false se nao for filtrado.
    */
    public boolean filtra(Registro registro){        
        Valor valor = registro.getValorByIndex(this.indexAtributo);
        int comp = valor.compareTo(operando);
        boolean filtra;
        switch(this.operador) {
            case OPER_IGUAL:
                filtra = comp == 0;
                break;
            case OPER_DIFERENTE:
                filtra = comp != 0;
                break;
            case OPER_MAIOR:
                filtra = comp > 0;
                break;
            case OPER_MAIOR_OU_IGUAL:
                filtra = comp >= 0;
                break;
            case OPER_MENOR:
                filtra = comp < 0;
                break;
            case OPER_MENOR_OU_IGUAL:
                filtra = comp <= 0;
                break;
            default:
                throw new IllegalArgumentException("[ERRO] Falha de implementação: operador de filtro inválido ("+this.operador+").");
        }
        return filtra;
    }
    
    public static List<Filtro> menuCriacaoFiltros(Tabela tabela) {
        List<Filtro> filtros = new LinkedList<Filtro>();
        List<Atributo> atributos = tabela.getAtributos();
        int index = -1;
        boolean criar = false;
        System.out.print("Deseja criar algum filtro? (s/n): ");
        criar = SGBD.SCAN.next().startsWith("s");
        if (criar){
            criar = false;
            do {
                //Se ainda não houver criado nenhum filtro para este atributo, a mensagem fica:
                //Deseja criar um filtro...?
                //Se já houver criado um filtro para este atributo, a mensagem fica:
                //Deseja criar mais um filtro...?
                System.out.print("Deseja criar "+(criar?"mais ":"")+"um filtro para o atributo-chave "+tabela.getNomeChave()+"? (s/n): ");
                criar = SGBD.SCAN.next().startsWith("s");
                if(criar){
                    Filtro f = menuCriacaoFiltro(index,Atributo.TIPO_INTEIRO);
                    filtros.add(f);
                    System.out.println("Filtro criado com sucesso.");
                }
            } while(criar); //enquanto filtros forem criados, pergunte se o usuário quer mais
            for(Atributo atr : atributos) {
                index++;
                do {
                System.out.print("Deseja criar "+(criar?"mais ":"")+"um filtro para o atributo "+atr.getNome()+"? (s/n): ");
                criar = SGBD.SCAN.next().startsWith("s");            
                    if(criar){
                        Filtro f = menuCriacaoFiltro(index,atr.getTipo());
                        filtros.add(f);
                        System.out.println("Filtro criado com sucesso.");
                    }
                } while(criar);
            }
        }
        return filtros;
    }
    
    private static Filtro menuCriacaoFiltro(int index, int tipoAtributoFiltrado){
        boolean entradaValida;
        //Entrada do codigo de operador
        int operador = 0;
        do {
            System.out.println  ("Entre com o codigo do operador desejado:\n"
                                +OPER_IGUAL+"- Igual (=)\n"
                                +OPER_DIFERENTE+"- Diferente (<>)\n"
                                +OPER_MAIOR+"- Maior (>)\n"
                                +OPER_MAIOR_OU_IGUAL+"- Maior ou igual (>=)\n"
                                +OPER_MENOR+"- Menor (<)\n"
                                +OPER_MENOR_OU_IGUAL+"- Menor ou igual (<=)\n");
            System.out.print("Operador: ");
            try{
                if (!SGBD.SCAN.hasNextInt()){
                    SGBD.SCAN.next();
                    System.out.println("Entrada invalida: a entrada deve ser um numero inteiro.");
                    entradaValida = false;
                }
                else {
                    int op = SGBD.SCAN.nextInt();
                    switch(op){
                        case OPER_IGUAL:
                        case OPER_DIFERENTE:
                        case OPER_MAIOR:
                        case OPER_MAIOR_OU_IGUAL:
                        case OPER_MENOR:
                        case OPER_MENOR_OU_IGUAL:
                            entradaValida=true;
                            operador = op;
                            break;
                        default:
                            System.out.println("Entrada invalida: codigo de operador invalido.");
                            entradaValida = false;
                    }
                }
            }
            catch(InputMismatchException ex){
                System.out.println("Entrada invalida: a entrada deve ser um numero inteiro.");
                entradaValida = false;
            }            
        } while(!entradaValida);        
        //Entrada do operando
        Valor operando = null;
        do {    
            try {
                switch(tipoAtributoFiltrado) {
                    case Atributo.TIPO_INTEIRO:
                        System.out.print("Entre com o valor do operando (o tipo eh inteiro): ");
                        if (!SGBD.SCAN.hasNextInt()){
                            SGBD.SCAN.next();
                            System.out.println("Entrada invalida: a entrada deve ser um numero inteiro.");
                            entradaValida = false;
                        }
                        else {
                            int valorOperandoInteiro = SGBD.SCAN.nextInt();
                            operando = new Valor(valorOperandoInteiro);                            
                            entradaValida = true;
                        }
                        break;
                    case Atributo.TIPO_TEXTO:
                        System.out.print("Entre com o valor do operando (o tipo eh texto): ");
                        String valorOperandoTexto = SGBD.SCAN.next();
                        operando = new Valor(valorOperandoTexto);
                        entradaValida = true;
                        break;
                }                
            }
            catch(InputMismatchException ex){
                System.out.println("Entrada invalida: a entrada deve ser do tipo requisitado.");
                entradaValida = false;
            }            
        } while(!entradaValida); 
        return new Filtro(index,operador,operando);
    }
}
