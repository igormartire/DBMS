package dojoparticoes;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class GeracaoParticoes {

    /**
     * Executa o algoritmo de geração de partições por Classificação Interna
     *
     * @param nomeArquivoEntrada arquivo de entrada
     * @param nomeArquivoSaida array list contendo os nomes dos arquivos de
     * saída a serem gerados
     * @param M tamanho do array em memória para manipulação dos registros
     */
    public void executaClassificacaoInterna(String nomeArquivoEntrada, List<String> nomeArquivoSaida, int M) throws Exception {

        //TODO: Inserir aqui o código do algoritmo de geração de partições
    }

    /**
     * Executa o algoritmo de geração de partições por Seleção com Substituição
     *
     * @param nomeArquivoEntrada arquivo de entrada
     * @param nomeArquivoSaida array list contendo os nomes dos arquivos de
     * saída a serem gerados
     * @param M tamanho do array em memória para manipulação dos registros
     */
    public void executaSelecaoComSubstituicao(String nomeArquivoEntrada, List<String> nomeArquivoSaida, int M) throws Exception {

        /*
        bandeira 1: usado para o caso de arquivos que nao tem o HV no fim
        bandeira 2: usado para o caso de arquivos que tem o HV no fim
        */
        final Cliente cHV = new Cliente(Integer.MAX_VALUE, "");

        Cliente[] vet = new Cliente[M];
        boolean[] congelados = new boolean[vet.length];

        int menor, indiceArqSaida = 0;
        Cliente ultimoInserido, ultimoLido;

        File arqSaida = new File(nomeArquivoSaida.get(indiceArqSaida));
        if (!arqSaida.exists()) {
            arqSaida.createNewFile();
        }

        DataInputStream entrada = new DataInputStream(new BufferedInputStream(new FileInputStream(nomeArquivoEntrada)));
        DataOutputStream saida = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(arqSaida)));

//--inicio-bandeira 1-----------------------------------------------------------
        try {
//--fim----bandeira 1-----------------------------------------------------------

            ultimoLido = Cliente.le(entrada);

//--comeco-bandeira 1-----------------------------------------------------------
        } catch (IOException ex) {
            entrada.close();
            saida.close();
            return;
        }
//--fim----bandeira 1-----------------------------------------------------------

        congelarTodos(congelados);

//--inicio-bandeira 1-----------------------------------------------------------
        for (int i = 0; i < vet.length && ultimoLido != null; i++) {
            try {
//--fim----bandeira 1-----------------------------------------------------------

//--inicio-bandeira 2-----------------------------------------------------------
//        for (int i = 0; i < vet.length; i++) {
//            if (ultimoLido.codCliente != Integer.MAX_VALUE) {
//--fim----bandeira 2-----------------------------------------------------------
                vet[i] = ultimoLido;
                congelados[i] = false;
                ultimoLido = Cliente.le(entrada);

//--inicio-bandeira 2-----------------------------------------------------------
//            } else {
//                vet[i] = ultimoLido;
//            }
//--fim----bandeira 2-----------------------------------------------------------
//--inicio-bandeira 1-----------------------------------------------------------
            } catch (IOException ex) {
                ultimoLido = null;
            }
//--fim----bandeira 1-----------------------------------------------------------

        }

//--inicio-bandeira 2-----------------------------------------------------------
//        while (ultimoLido.codCliente != Integer.MAX_VALUE) {
//--fim----bandeira 2-----------------------------------------------------------

//--comeco-bandeira 1-----------------------------------------------------------
        while (ultimoLido != null) {
//--fim----bandeira 1-----------------------------------------------------------

            menor = buscaIndiceMenorNaoCongelado(vet, congelados);

            if (menor != -1) {
                vet[menor].salva(saida);

                ultimoInserido = vet[menor];

                vet[menor] = ultimoLido;

                if (vet[menor].codCliente < ultimoInserido.codCliente) {
                    congelados[menor] = true;
                }

//--inicio-bandeira 1-----------------------------------------------------------
                try {
//--fim----bandeira 1-----------------------------------------------------------

                    ultimoLido = Cliente.le(entrada);

//--inicio-bandeira 1-----------------------------------------------------------
                } catch (IOException ex) {
                    ultimoLido = null;
                }
//--fim----bandeira 1-----------------------------------------------------------

            } else {

//--comeco-bandeira 2-----------------------------------------------------------
//            cHV.salva(saida);
//--fim----bandeira 2-----------------------------------------------------------

                saida.close();

                indiceArqSaida++;

                if (indiceArqSaida == nomeArquivoSaida.size()) {
                    entrada.close();
                    return;
                }

                arqSaida = new File(nomeArquivoSaida.get(indiceArqSaida));

                if (!arqSaida.exists()) {
                    arqSaida.createNewFile();
                }

                saida = new DataOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream(arqSaida)));

                descongelarTodos(congelados);
            }
        }

        menor = buscaIndiceMenorNaoCongelado(vet, congelados);

        while (menor != -1) {

            vet[menor].salva(saida);

//--comeco-bandeira 1-----------------------------------------------------------
            vet[menor] = null;
//--fim----bandeira 1-----------------------------------------------------------
            
//--comeco-bandeira 2-----------------------------------------------------------
//            vet[menor] = cHV;
//--fim----bandeira 2-----------------------------------------------------------
            
            congelados[menor] = true;

            menor = buscaIndiceMenorNaoCongelado(vet, congelados);

        }
//--comeco-bandeira 2-----------------------------------------------------------
//        cHV.salva(saida);
//--fim----bandeira 2-----------------------------------------------------------

        saida.close();

        indiceArqSaida++;

        if (indiceArqSaida == nomeArquivoSaida.size()) {
            entrada.close();
            return;
        }

        arqSaida = new File(nomeArquivoSaida.get(indiceArqSaida));

        if (!arqSaida.exists()) {
            arqSaida.createNewFile();
        }

        saida = new DataOutputStream(
                new BufferedOutputStream(
                        new FileOutputStream(arqSaida)));

        descongelarTodos(congelados);

        menor = buscaIndiceMenorNaoCongelado(vet, congelados);

        while (menor != -1) {
//--inicio-bandeira 1-----------------------------------------------------------
            if(vet[menor] == null){
//--fim----bandeira 1-----------------------------------------------------------

//--inicio-bandeira 2-----------------------------------------------------------
//            if (vet[menor].codCliente != Integer.MAX_VALUE) {
//--fim----bandeira 2-----------------------------------------------------------
                
                vet[menor].salva(saida);
                
//--inicio-bandeira 2-----------------------------------------------------------
                vet[menor] = null;
//--fim----bandeira 2-----------------------------------------------------------

//--inicio-bandeira 2-----------------------------------------------------------
//                vet[menor].codCliente = Integer.MAX_VALUE;
//--fim----bandeira 2-----------------------------------------------------------

            }

            congelados[menor] = true;
            menor = buscaIndiceMenorNaoCongelado(vet, congelados);

        }
//--inicio-bandeira 2-----------------------------------------------------------
//        cHV.salva(saida);
//--fim----bandeira 2-----------------------------------------------------------

        saida.close();
        
        entrada.close();

    }

    /**
     * Executa o algoritmo de geração de partições por Seleção Natural
     *
     * @param nomeArquivoEntrada arquivo de entrada
     * @param nomeArquivoSaida array list contendo os nomes dos arquivos de
     * saída a serem gerados
     * @param M tamanho do array em memória para manipulação dos registros
     * @param n tamanho do reservatório
     */
    public void executaSelecaoNatural(String nomeArquivoEntrada, List<String> nomeArquivoSaida, int M, int n) throws Exception {

        //TODO: Inserir aqui o código do algoritmo de geração de partições
    }

    /**
     *
     * @param vet
     * @param congelados
     * @return -1 caso todos estejam cogelados
     * @throws IllegalArgumentException
     */
    private int buscaIndiceMenorNaoCongelado(Cliente[] vet, boolean[] congelados) throws IllegalArgumentException {
        if (vet.length != congelados.length) {
            throw new IllegalArgumentException();
        }

        int indiceMenor = -1;

        // Buscar o primeiro nao congelado
        for (int i = 0; i < vet.length; i++) {
            if (!congelados[i]) {
                indiceMenor = i;
                break;
            }

        }

        // Caso todos estejam congelados, retorna -1
        if (indiceMenor == -1) {
            return indiceMenor;
        }

        // Buscar menor nao congelado depois do indiceMenor
        for (int i = indiceMenor + 1; i < vet.length; i++) {

            if (!congelados[i] && vet[i].codCliente < vet[indiceMenor].codCliente) {
                indiceMenor = i;
            }

        }

        return indiceMenor;

    }

    private void descongelarTodos(boolean[] congelados) {
        for (int i = 0; i < congelados.length; i++) {
            congelados[i] = false;

        }
    }

    private void congelarTodos(boolean[] congelados) {
        for (int i = 0; i < congelados.length; i++) {
            congelados[i] = true;

        }
    }

}
