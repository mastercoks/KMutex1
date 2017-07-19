/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.uesb.kmutex1.rede.receber;

import br.edu.uesb.kmutex1.Processo;
import br.edu.uesb.kmutex1.enumerado.Estado;
import br.edu.uesb.kmutex1.enumerado.TipoMensagem;
import br.edu.uesb.kmutex1.mensagens.Mensagem;
import br.edu.uesb.kmutex1.rede.Enviar;
import br.edu.uesb.kmutex1.rede.NetworkService;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Matheus
 */
public class ReceberRequest implements Runnable {

    private final Processo processo;

    public ReceberRequest(Processo processo) {
        this.processo = processo;
    }

    @Override
    public void run() {
        try {
            NetworkService rede = new NetworkService(7200 + processo.getId());
            while (true) {
                Future<Mensagem> future = processo.receber(rede);
                Mensagem mensagem = future.get();
                if (processo.isCrashed()) {

                } else if (mensagem.getTipo().equals(TipoMensagem.REQUEST)) {

                    processo.getRaymond().setH(processo.getRaymond().maior(processo.getRaymond().getH(), mensagem.getValor()));
                    if (!processo.getRaymond().containsCrashed(mensagem.getId_origem())) {
                        if (processo.getRaymond().getState() == Estado.CS
                                || (processo.getRaymond().isState(Estado.SOLICITANDO)
                                && (checa(processo.getRaymond().getLast(), processo.getId(), mensagem.getValor(), mensagem.getId_origem())))) {
                            processo.getRaymond().incrementaDefer_count(mensagem.getId_origem());
                            System.out.println("Processo[" + processo.getId() + "]: Recebeu REQUEST do processo "
                                    + mensagem.getId_origem() + ", mas atrasou a resposta!");
                        } else {
                            Mensagem m_resposta = new Mensagem(processo.getId(), mensagem.getId_origem(), TipoMensagem.REPLY, 1);
                            processo.executar(new Enviar("localhost", 7300 + mensagem.getId_origem(), m_resposta));
//                            System.out.println("Processo[" + processo.getId() + "]: Recebeu REQUEST do processo "
//                                    + mensagem.getId_origem() + ", e respondeu com REPLY! " + processo.getRaymond().getState() + " last: " + processo.getRaymond().getLast() + " lastj: " + mensagem.getValor());
                        }
                    } else {
                        System.err.println("Processo[" + processo.getId() + "]: Erro, pacote recebido do Processo " + mensagem.getId_origem() + " não era REQUEST!" + mensagem.getTipo());
                    }
                }
            }
        } catch (IOException | InterruptedException | ExecutionException ex) {
            Logger.getLogger(ReceberInit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            System.err.println("Processo[" + processo.getId() + "]: erro ao enviar REQUEST!");
        }
    }

    private boolean checa(int lasti, int i, int lastj, int j) {
//        System.out.println("check: " + lasti + " < " + lastj + " && " + i + " < " + j);
        return lasti <= lastj && i < j;
    }

}
