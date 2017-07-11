/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.uesb.kmutex1.rede;

import br.edu.uesb.kmutex1.mensagens.Mensagem;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

/**
 *
 * @author Matheus
 */
public class ServicoReceber implements Callable<Mensagem> {

    private final Socket socket;

    public ServicoReceber(Socket socket) {
        this.socket = socket;
    }

    @Override
    public Mensagem call() throws Exception {
        ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
        Mensagem pacote = (Mensagem) entrada.readObject();
        socket.close();
        return pacote;
    }

}
