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
public class ReceberInit implements Runnable {

    private final Processo processo;

    public ReceberInit(Processo processo) {
        this.processo = processo;
    }

    @Override
    @SuppressWarnings("empty-statement")
    public void run() {
        try {
            NetworkService rede = new NetworkService(7000 + processo.getId());
            while (true) {
                Future<Mensagem> future = processo.receber(rede);
                Mensagem mensagem = future.get();
                if (mensagem.getTipo().equals(TipoMensagem.INIT)) {
                    while (processo.containsSuspeito_T(mensagem.getId_origem()));
                    processo.addTrusted(mensagem.getId_origem());
                    Mensagem resposta = new Mensagem(processo.getId(), mensagem.getId_origem(), TipoMensagem.ACK);
                    processo.executar(new Enviar("localhost", 7100 + mensagem.getId_origem(), resposta));
                } else {
                    System.err.println("Processo[" + processo.getId() + "]: Erro, pacote recebido do Processo " + mensagem.getId_origem() + " não era INIT!");
                }
            }
        } catch (IOException | InterruptedException | ExecutionException ex) {
            Logger.getLogger(ReceberInit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            System.err.println("Processo[" + processo.getId() + "]: erro ao enviar ACK!");
        }
    }

}
