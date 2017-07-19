/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.uesb.kmutex1;

import br.edu.uesb.kmutex1.enumerado.TipoMensagem;
import br.edu.uesb.kmutex1.mensagens.Mensagem;
import br.edu.uesb.kmutex1.rede.Enviar;
import br.edu.uesb.kmutex1.rede.NetworkService;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Matheus
 */
public class DetectorFalhas implements Runnable {

    private final Processo processo;
    private List<Integer> suspeitos_S;
    private List<Integer> suspeitos_T;
    private final ExecutorService es;
            
    public DetectorFalhas(Processo processo) {
        this.processo = processo;
        this.suspeitos_S = new ArrayList<>();
        this.suspeitos_T = new ArrayList<>();
        es = Executors.newCachedThreadPool();

        suspeitos_S.addAll(processo.getProcessos());
        processo.executar(new Suspeitar());
    }

    @Override
    public void run() {
        while (true) {
            for (Integer processoj : processo.getProcessos()) {
                if (processo.containsTrusted(processoj) && suspeitos_T.contains(processoj)) {
                    processo.removeTrusted(processoj);
                    System.err.println("Processo[" + processo.getId() + "]: Processo " + processoj + " não é confiavel!");
                    try {
                        enviaCrash(processoj);
                    } catch (IOException | ClassNotFoundException ex) {
                        Logger.getLogger(DetectorFalhas.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    public boolean containsSuspeito_S(Integer processoj) {
        return suspeitos_S.contains(processoj);
    }

    public boolean containsSuspeito_T(Integer processoj) {
        return suspeitos_T.contains(processoj);
    }

    public List<Integer> getSuspeitos_S() {
        return suspeitos_S;
    }

    public void setSuspeitos_S(List<Integer> suspeitos_S) {
        this.suspeitos_S = suspeitos_S;
    }

    public List<Integer> getSuspeitos_T() {
        return suspeitos_T;
    }

    public void setSuspeitos_T(List<Integer> suspeitos_T) {
        this.suspeitos_T = suspeitos_T;
    }

    private void enviaCrash(Integer processoj) throws IOException, UnknownHostException, ClassNotFoundException {
        for (Integer processok : processo.getProcessos()) {
            if (!Objects.equals(processoj, processok) && !processo.containsCrashed(processok)) {
                Mensagem mensagem = new Mensagem(processo.getId(), processok, TipoMensagem.CRASH, processoj);
                processo.executar(new Enviar("localhost", 7400 + processok, mensagem));
            }
        }
    }

    public class Suspeitar implements Runnable {

        @Override
        public void run() {
            try {
                NetworkService rede = new NetworkService(7600 + processo.getId());
                while (true) {
                    for (Integer processoj : processo.getProcessos()) {
                        if (processoj != processo.getId()) {
                            Mensagem mensagem_enviar = new Mensagem(processo.getId(), processoj, TipoMensagem.YOU_ALIVE);
                            es.execute(new Enviar("localhost", 7500 + processoj, mensagem_enviar));
                            Future<Mensagem> future = processo.receber(rede);
                            try {
                                Mensagem mensagem = future.get(1000, TimeUnit.MILLISECONDS);
                                if (mensagem.getTipo().equals(TipoMensagem.I_AM_ALIVE)) {
                                } else {
                                    System.err.println("Processo[" + processo.getId() + "]: Erro, pacote recebido do Processo " + mensagem.getId_origem() + " não era I_AM_ALIVE!");
                                }
                            } catch (TimeoutException ex) {
                                if (!suspeitos_T.contains(processoj) && !suspeitos_T.contains(processo.getId())) {
//                                    System.err.println("Processo[" + processo.getId() + "]: Suspeitando do Processo " + processoj + "!");
                                    suspeitos_T.add(processoj);
                                }
                            }
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException | InterruptedException | ExecutionException ex) {
                System.err.println("Processo[" + processo.getId() + "]: DetectorFalhas " + ex);
            }
        }

    }

}
