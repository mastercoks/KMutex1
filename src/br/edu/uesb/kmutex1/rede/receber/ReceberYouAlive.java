/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.uesb.kmutex1.rede.receber;

import br.edu.uesb.kmutex1.Processo;
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
public class ReceberYouAlive implements Runnable {

    private final Processo processo;

    public ReceberYouAlive(Processo processo) {
        this.processo = processo;
    }

    @Override
    public void run() {
        try {
            NetworkService rede = new NetworkService(7500 + processo.getId());
            while (true) {
                Future<Mensagem> future = processo.receber(rede);
                Mensagem mensagem = future.get();
                if (!processo.isCrashed() && mensagem.getTipo().equals(TipoMensagem.YOU_ALIVE)) {
                    Mensagem resposta = new Mensagem(processo.getId(), mensagem.getId_origem(), TipoMensagem.I_AM_ALIVE);
                    processo.executar(new Enviar("localhost", 7600 + mensagem.getId_origem(), resposta));
                } else {
//                    System.err.println("Processo[" + processo.getId() + "]: Erro, pacote recebido do Processo " + mensagem.getId_origem() + " não era YOU_ALIVE!" + mensagem.getTipo());
                }
            }
        } catch (IOException | InterruptedException | ExecutionException ex) {
            System.err.println("Processo[" + processo.getId() + "]: erro ao enviar!" + ex);
        } catch (ClassNotFoundException ex) {
            System.err.println("Processo[" + processo.getId() + "]: erro ao enviar I_AM_ALIVE!");
        }
    }

}
