/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.uesb.kmutex1.rede;

import br.edu.uesb.kmutex1.mensagens.Mensagem;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 * @author Matheus
 */
public class NetworkService implements Callable<Mensagem> {

    private final ServerSocket serverSocket;
    private final ExecutorService pool;
    private final int porta;

    public NetworkService(int porta) throws IOException {
        this.porta = porta;
        serverSocket = new ServerSocket(porta);
        pool = Executors.newCachedThreadPool();
//        System.out.println("Servidor aberto na porta: " + serverSocket.getLocalPort());
    }

    public void fecharServidor() throws IOException {
        serverSocket.close();
        pool.shutdown();
//        System.out.println("Servidor fechado na porta: " + serverSocket.getLocalPort());
    }

    @Override
    public Mensagem call() throws InterruptedException, ExecutionException {
        try {
            serverSocket.setReuseAddress(true);
            Future<Mensagem> future = pool.submit(new ServicoReceber(serverSocket.accept()));
            Mensagem pacote = future.get();
//            Thread.sleep(1000);
            return pacote;

        } catch (IOException ex) {
            pool.shutdown();
            System.err.println("Erro ao receber o pacote.");
            return null;
        }
    }

}
