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
public class ReceberCrash implements Runnable {

    private final Processo processo;

    public ReceberCrash(Processo processo) {
        this.processo = processo;
    }

    @Override
    public void run() {
        try {
            NetworkService rede = new NetworkService(7400 + processo.getId());
            while (true) {
                Future<Mensagem> future = processo.receber(rede);
                Mensagem mensagem = future.get();
                if (mensagem.getTipo().equals(TipoMensagem.CRASH)) {
                    if (!processo.getRaymond().containsCrashed(mensagem.getValor())) {
                        processo.getRaymond().crashedAdd(mensagem.getValor());
                        if (processo.getRaymond().isState(Estado.SOLICITANDO) && processo.getRaymond().isReply_count(mensagem.getValor(), 0)) {
                            processo.getRaymond().decrementarPerm_count();
                            processo.getRaymond().decrementarN();
                        }
                    }
                } else {
                    System.err.println("Processo[" + processo.getId() + "]: Erro, pacote recebido do Processo "
                            + mensagem.getId_origem() + " não era CRASH!");
                }
            }
        } catch (IOException | InterruptedException | ExecutionException ex) {
            Logger.getLogger(ReceberInit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
