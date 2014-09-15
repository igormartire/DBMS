/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dojointercalacao;

import arvore.No;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Executa o algoritmo Intercalação Ótima
 *
 * @param nomeParticoes array com os nomes dos arquivos que contêm as partições
 * de entrada
 * @param nomeArquivoSaida nome do arquivo de saída resultante da execução do
 * algoritmo
 */
public class IntercalacaoOtima {

    private static final int F = 4;

    public void executa(List<String> nomeParticoes, String nomeArquivoSaida) throws Exception {
        //TODO: Inserir aqui o código do algoritmo de Intercalação Ótima
        DataInputStream[] leitores = new DataInputStream[F - 1];
        DataOutputStream escritor = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(nomeArquivoSaida)));

        // caso sem particoes ou uma particao
        
        int i = 0;
        while(i < nomeParticoes.size()) {
            List<String> args = new ArrayList<String>();
            for (int j = 0; j < leitores.length && i < nomeParticoes.size(); j++) {
                leitores[j] = new DataInputStream(new BufferedInputStream(new FileInputStream(nomeParticoes.get(i))));
                args.add(nomeParticoes.get(i));
                i++;

            }
            if(i == nomeParticoes.size()){
                args.add(nomeArquivoSaida);
                String[] param = toArray(args);
                
                
                arvoreDeVencedores(param);
                return;
            }else{
                String nomeSaida = "particao" + (nomeParticoes.size() + 1) + ".dat";
                args.add(nomeSaida);
                String[] param = toArray(args);
                arvoreDeVencedores(param);
                nomeParticoes.add(nomeSaida);
            }
//            manipulaArvore(nomeParticoes.subList(i, i));
            /*se fila vazia, entao
                chamar arvore de vencedores com arquivo de saida chamado nomeArquivoSaida
                fim funcao
              senao
                chamar arvore de vencedores com arquivo de saida chamado "particao" + (nomeParticoes.size() + 1) + ".dat"
                adicionar o nome desta particao na lista nomeParticoes
              fimse
            */
            

        }

//        for (int i = 0; i < nomeParticoes.size(); i++) {
//            for (int j = 0 ; i < nomeParticoes.size() && j < leitores.length; j++) {
//                leitores[j] = new DataInputStream(new BufferedInputStream(new FileInputStream(nomeParticoes.get(i))));
//                i++;
//            }
//
//            Cliente cliente = Cliente.le(leitores[i]);
//
//            while (cliente.codCliente != Integer.MAX_VALUE) {
//                cliente.salva(escritor);
//                cliente = Cliente.le(leitores[i]);
//            }
//            cliente.salva(escritor);
//
//            leitores.close();
//            escritor.close();
//        }
    }

    private void arvoreDeVencedores(String args[]) {
        No[] nos = new No[args.length - 1];
        No arvore = new No();

        try {
            //Le todos os arquivos de entrada de registros e cria um no que aponta pra cada um deles
            for (int i = 0; i < nos.length ; i++) {
                nos[i] = new No();
                nos[i].info.in = new DataInputStream(new BufferedInputStream(new FileInputStream(args[i])));
                nos[i].info.atualizar();
                System.out.println(nos[i].info.info);
            }
            //Esses nos criados então serviram como base para a arvore
            arvore = arvore.criaArvore(nos);
            System.out.println(arvore.toString());
            //Lemos então o nome do arquivo de saida e instaciamos seu OutputStream
            DataOutputStream saida = new DataOutputStream(new BufferedOutputStream(
                    new FileOutputStream(args[args.length-1])));
            arvore.salvaArvore(saida);
            
            //Agora fechamos todos os arquivos abertos
            saida.close();
            
            for (int i = 0; i < nos.length; i++) {
                nos[i].info.in.close();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private String[] toArray(List<String> args) {
        String[] result = null;
        result = new String[args.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = args.get(i);
            
        }
        
        return result;
    }
}
