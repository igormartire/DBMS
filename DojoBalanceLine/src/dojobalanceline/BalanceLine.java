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

        while (itemTransacao.chave != Integer.MAX_VALUE
                && itemMestre.codCliente != Integer.MAX_VALUE) {

            if (itemMestre.codCliente < itemTransacao.chave) {
                itemMestre.salva(outSaida);
                itemMestre = Cliente.le(inMestre);

            } else if (itemMestre.codCliente == itemTransacao.chave) {

                switch (itemTransacao.tipoTransacao) {
                    case Transacao.INSERCAO:
                        itemMestre.salva(outSaida);
                        itemTransacao.salva(outErro);
                        break;
                    case Transacao.EXCLUSAO:
                        break;
                    case Transacao.MODIFICACAO:

                        TransacaoModificacao mod = (TransacaoModificacao) itemTransacao;

                        if (mod.nomeAtributo.equals("nome")) {
                            itemMestre.nome = mod.valorAtributo;
                        } else {
                            itemMestre.dataNascimento = mod.valorAtributo;
                        }
                        itemMestre.salva(outSaida);

                        break;
                }
                itemMestre = Cliente.le(inMestre);
                itemTransacao = Transacao.le(inTransacao);
            } else {
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

        if (itemTransacao.chave == Integer.MAX_VALUE) {
            while (itemMestre.codCliente != Integer.MAX_VALUE) {
                itemMestre.salva(outSaida);
                itemMestre = Cliente.le(inMestre);

            }
        } else {
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