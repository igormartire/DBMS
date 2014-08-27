package dojobalanceline;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class BalanceLine {

    /**
     * Executa o algoritmo Balance Line
     *
     * @param nomeArquivoMestre arquivo mestre
     * @param nomeArquivoTransacao arquivo de transação
     * @param nomeArquivoErros arquivo de erros
     * @param nomeArquivoSaida arquivo de saída
     */
    public void executa(String nomeArquivoMestre, String nomeArquivoTransacao,
            String nomeArquivoSaida, String nomeArquivoErros) throws Exception {

        DataInputStream inTransacao = new DataInputStream(new BufferedInputStream(new FileInputStream(nomeArquivoTransacao)));
        DataInputStream inMestre = new DataInputStream(new BufferedInputStream(new FileInputStream(nomeArquivoMestre)));

        DataOutputStream outSaida = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(nomeArquivoSaida)));
        DataOutputStream outErro = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(nomeArquivoErros)));


        Transacao itemTransacao;
        Cliente itemMestre;

        itemTransacao = Transacao.le(inTransacao);
        itemMestre = Cliente.le(inMestre);

        // Enquanto nenhum dos dois atingiu o fim do arquivo
        while (itemTransacao.chave != Integer.MAX_VALUE
                && itemMestre.codCliente != Integer.MAX_VALUE) {

            // Se a chave do itemMestre for menor, vai salvando direto no arquivo de saida seus valores
            if (itemMestre.codCliente < itemTransacao.chave) {
                itemMestre.salva(outSaida);
                itemMestre = Cliente.le(inMestre);
            // Se for igual..
            } else if (itemMestre.codCliente == itemTransacao.chave) {

                switch (itemTransacao.tipoTransacao) {
                    // Caso seja insercao, vai salvar o ERRO E o mestre
                    case Transacao.INSERCAO:
                        itemMestre.salva(outSaida);
                        itemTransacao.salva(outErro);
                        break;
                    // Caso exclusao, apenas nao salva no arquivo de saida (mestre)
                    case Transacao.EXCLUSAO:
                        break;
                    // Caso seja modificacao, solicita os valores
                    case Transacao.MODIFICACAO:

                        TransacaoModificacao mod = (TransacaoModificacao) itemTransacao;
                        // So tem 2 atributos a serem mudados e estamos considerando que 
                        // o arquivo transacao esta correto (nao tem atributos que nao existem)
                        if (mod.nomeAtributo.equals("nome")) {
                            itemMestre.nome = mod.valorAtributo;
                        } else {
                            itemMestre.dataNascimento = mod.valorAtributo;
                        }
                        // Depois de alterar o atributo solicitado, salva.
                        itemMestre.salva(outSaida);

                        break;
                }
                // Le o proximo itemMestre E itemTransacao
                itemMestre = Cliente.le(inMestre);
                itemTransacao = Transacao.le(inTransacao);
            } else {    // Caso codCliente seja maior que itemTransacao.chave
                        // ou seja, tem que executar a transacao antes
                switch (itemTransacao.tipoTransacao) {
                    case Transacao.INSERCAO:
                        TransacaoInsercao ins = (TransacaoInsercao) itemTransacao;
                        // Instancia um cliente com os dados vindos do arquivo de transacao
                        Cliente novoCliente = new Cliente(ins.chave, ins.nomeCliente, ins.dataNascimento);
                        novoCliente.salva(outSaida);
                        break;
                    // Erro, pois a chave e diferente
                    case Transacao.EXCLUSAO:
                        itemTransacao.salva(outErro);
                        break;
                    case Transacao.MODIFICACAO:
                        itemTransacao.salva(outErro);
                        break;
                }
                // Le APENAS a proxima TRANSACAO
                itemTransacao = Transacao.le(inTransacao);
            }

        }
        
        // So chega nesse ponto quando um dos dois (ou os dois) arquivos acabarem
        // Se foi o de transacao que acabou, apenas salva todo o resto do itemMestre no out
        if (itemTransacao.chave == Integer.MAX_VALUE) {
            while (itemMestre.codCliente != Integer.MAX_VALUE) {
                itemMestre.salva(outSaida);
                itemMestre = Cliente.le(inMestre);

            }
        } else {    // Caso ainda existam transacoes a serem realizadas
            while (itemTransacao.chave != Integer.MAX_VALUE) {
                switch (itemTransacao.tipoTransacao) {
                    case Transacao.INSERCAO:
                        TransacaoInsercao ins = (TransacaoInsercao) itemTransacao;
                        Cliente novoCliente = new Cliente(ins.chave, ins.nomeCliente, ins.dataNascimento);
                        novoCliente.salva(outSaida);
                        break;
                    case Transacao.EXCLUSAO:
                        itemTransacao.salva(outErro);
                        break;
                    case Transacao.MODIFICACAO:
                        itemTransacao.salva(outErro);
                        break;
                }
                
                itemTransacao = Transacao.le(inTransacao);
            }
        }
        
        // Salva o valor convencionado para determinar fim de arquivo
        Cliente maxCliente = new Cliente(Integer.MAX_VALUE, "    ", "          ");
        maxCliente.salva(outSaida);
        Transacao maxTrans = new TransacaoExclusao(Integer.MAX_VALUE);
        maxTrans.salva(outErro);
        
        inMestre.close();
        inTransacao.close();
        outSaida.close();
        outErro.close();

    }
}