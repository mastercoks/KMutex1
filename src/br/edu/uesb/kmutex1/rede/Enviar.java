/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.uesb.kmutex1.rede;

import br.edu.uesb.kmutex1.mensagens.Mensagem;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Matheus
 */
public final class Enviar implements Runnable {

    private final int id;
    private final Socket cliente;
    private final Mensagem mensagem;

    public Enviar(String host, int porta, Mensagem mensagem) throws UnknownHostException, IOException, ClassNotFoundException {
        this.id = mensagem.getId_origem();
        this.mensagem = mensagem;
        this.cliente = new Socket(host, porta);
    }

    @Override
    public void run() {
        try {

//            System.out.println("Processo[" + id + "]: " + "Cliente " + cliente.getInetAddress() + ":" + cliente.getPort() + " conectado.");
            ObjectOutputStream saida = new ObjectOutputStream(cliente.getOutputStream());
            saida.writeObject(mensagem);
            cliente.close();
//            System.out.println("Processo[" + id + "]: " + "Cliente " + cliente.getInetAddress() + ":" + cliente.getPort() + " desconectado.");
        } catch (IOException ex) {
            System.err.println("Processo[" + id + "]: Falha no envio: " + ex);
        } 
    }

    public long getId() {
        return id;
    }

}
