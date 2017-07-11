/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.uesb.kmutex1;

import br.edu.uesb.kmutex1.enumerado.Estado;
import br.edu.uesb.kmutex1.enumerado.TipoMensagem;
import br.edu.uesb.kmutex1.mensagens.Mensagem;
import br.edu.uesb.kmutex1.rede.Enviar;
import br.edu.uesb.kmutex1.rede.NetworkService;
import br.edu.uesb.kmutex1.rede.receber.ReceberCrash;
import br.edu.uesb.kmutex1.rede.receber.ReceberInit;
import br.edu.uesb.kmutex1.rede.receber.ReceberReply;
import br.edu.uesb.kmutex1.rede.receber.ReceberRequest;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Matheus
 */
public class Raymond implements Runnable {

    private final Processo processo;

    private int n;
    private Estado state;
    private int H;
    private int last;
    private int perm_count;
    private int[] reply_count;
    private int[] defer_count;
    private List<Integer> trusted;
    private List<Integer> crashed;
    private final DetectorFalhas FD;

    public Raymond(int process_length, Processo processo) {
        this.processo = processo;
        n = process_length;
        state = Estado.NAO_SOLICITANDO;
        H = 0;
        last = 0;
        perm_count = 0;
        reply_count = new int[process_length];
        defer_count = new int[process_length];
        iniciarArrays(process_length);
        trusted = new ArrayList<>();
        crashed = new ArrayList<>();
        FD = new DetectorFalhas(processo);
    }

    private void iniciarArrays(int process_length) {
        for (int i = 0; i < process_length; i++) {
            reply_count[i] = 0;
            defer_count[i] = 0;
        }
    }

    @SuppressWarnings("empty-statement")
    public void request_resource() throws IOException, UnknownHostException, ClassNotFoundException {
        System.out.println("Processo[" + processo.getId() + "]: Solicitando entrada na CS.");
        state = Estado.SOLICITANDO;
        last = H + 1;
        perm_count = 0;
        for (int processoj : processo.getProcessos()) {
            if (processoj != processo.getId() && !crashed.contains(processoj)) {
                Mensagem mensagem = new Mensagem(processo.getId(), processoj, TipoMensagem.REQUEST, last, H);
                processo.executar(new Enviar("localhost", (7200 + processoj), mensagem));
                reply_count[processoj]++;
            }
        }
        while (perm_count >= (n - processo.getQuant_recursos()));
        state = Estado.CS;
        Main.recursos_uso++;
        mudarRecurso();
        System.out.println("Processo[" + processo.getId() + "]: Entrou na CS.");
    }

    public void release_resource() throws IOException, UnknownHostException, ClassNotFoundException {
        System.out.println("Processo[" + processo.getId() + "]: Saindo da CS.");
        state = Estado.NAO_SOLICITANDO;
        for (int processoj : processo.getProcessos()) {
            if (processoj != processo.getId() && defer_count[processoj] != 0 && !crashed.contains(processoj)) {
                Mensagem mensagem = new Mensagem(processo.getId(), processoj, TipoMensagem.REPLY, defer_count[processoj]);
                processo.executar(new Enviar("localhost", (7300 + processoj), mensagem));
                defer_count[processoj] = 0;
            }
        }
        Main.recursos_uso--;
        System.out.println("Processo[" + processo.getId() + "]: Saiu da CS.");
    }

    @Override
    @SuppressWarnings("empty-statement")
    public void run() {
        try {
//            System.out.println("Processo[" + processo.getId() + "]: Iniciando.");
            processo.executar(FD);
            processo.executar(new ReceberInit(processo));
            processo.executar(new ReceberRequest(processo));
            processo.executar(new ReceberReply(processo));
            processo.executar(new ReceberCrash(processo));
            NetworkService rede = new NetworkService(7100 + processo.getId());
            broadcast(TipoMensagem.INIT);
            for (Integer processoj : processo.getProcessos()) {
                if (processoj != processo.getId() && FD.containsSuspeito_S(processoj)) {
                    Future<Mensagem> future = processo.receber(rede);
                    Mensagem mensagem = future.get();
                    if (mensagem.getTipo().equals(TipoMensagem.ACK)) {
//                        System.out.println("Processo[" + processo.getId() + "]: ACK do Processo " + mensagem.getId_origem() + " recebido com sucesso!");
                    } else {
                        System.err.println("Processo[" + processo.getId() + "]: Erro, pacote recebido do Processo " + mensagem.getId_origem() + " não era ACK!");
                    }
                }
            }
//            System.out.println("Processo[" + processo.getId() + "]: Iniciado com sucesso!");
            Thread.sleep(1000);
//            System.out.println(this);
//            while (true) {
//                Future<Mensagem> future = processo.receber(rede);
//                Mensagem mensagem = future.get();
//                switch (mensagem.getTipo()) {
//                    case ACK:
//
//                        break;
//                    case INIT:
//                        while (FD.containsSuspeito_T(mensagem.getId_origem()));
//                        trusted.add(mensagem.getId_origem());
//                        Mensagem resposta = new Mensagem(processo.getId(), mensagem.getId_origem(), TipoMensagem.ACK);
//                        processo.executar(new Enviar("localhost", 7000 + mensagem.getId_origem(), resposta));
//                        break;
//                    case CRASH:
//                        if (!crashed.contains(mensagem.getValor())) {
//                            crashed.add(mensagem.getValor());
//                            if (state.equals(Estado.SOLICITANDO) && reply_count[mensagem.getValor()] == 0) {
//                                perm_count--;
//                                n--;
//                            }
//                        }
//                        break;
//                    case REPLY:
//                        if (!crashed.contains(mensagem.getId_origem())) {
//                            reply_count[mensagem.getId_origem()] -= mensagem.getValor();
//                            if (state == Estado.SOLICITANDO && reply_count[mensagem.getId_origem()] == 0) {
//                                perm_count++;
//                            }
//                        }
//                        break;
//                    case REQUEST:
//                        H = maior(H, mensagem.getH());
//                        if (!crashed.contains(mensagem.getId_origem())) {
//                            if (state == Estado.CS
//                                    || equals(state == Estado.SOLICITANDO
//                                            && (last < mensagem.getValor()))) {
//                                defer_count[mensagem.getId_origem()]++;
//                            } else {
//                                Mensagem m_resposta = new Mensagem(processo.getId(), mensagem.getId_origem(), TipoMensagem.REPLY, 1);
//                                processo.executar(new Enviar("localhost", 7000 + mensagem.getId_origem(), m_resposta));
//                            }
//                        }
//                        break;
//                }
//            }
        } catch (IOException | InterruptedException | ExecutionException | ClassNotFoundException ex) {
            Logger.getLogger(Raymond.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int maior(int rodada, int ultima_rodada) {
        if (rodada > ultima_rodada) {
            return rodada;
        } else {
            return ultima_rodada;
        }
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public Estado getState() {
        return state;
    }

    public void setState(Estado state) {
        this.state = state;
    }

    public int getH() {
        return H;
    }

    public void setH(int H) {
        this.H = H;
    }

    public int getLast() {
        return last;
    }

    public void setLast(int last) {
        this.last = last;
    }

    public int getPerm_count() {
        return perm_count;
    }

    public void setPerm_count(int perm_count) {
        this.perm_count = perm_count;
    }

    public int[] getReply_count() {
        return reply_count;
    }

    public void setReply_count(int[] reply_count) {
        this.reply_count = reply_count;
    }

    public int[] getDefer_count() {
        return defer_count;
    }

    public void incrementaDefer_count(int pos) {
        defer_count[pos]++;
    }

    public void setDefer_count(int[] defer_count) {
        this.defer_count = defer_count;
    }

    public List<Integer> getTrusted() {
        return trusted;
    }

    public void setTrusted(List<Integer> trusted) {
        this.trusted = trusted;
    }

    public List<Integer> getCrashed() {
        return crashed;
    }

    public void setCrashed(List<Integer> crashed) {
        this.crashed = crashed;
    }

    private void broadcast(TipoMensagem tipoMensagem) throws IOException, UnknownHostException, ClassNotFoundException {
        for (Integer processoj : processo.getProcessos()) {
            if (processoj != processo.getId()) {
                Mensagem mensagem = new Mensagem(processo.getId(), processoj, tipoMensagem);
                processo.executar(new Enviar("localhost", 7000 + processoj, mensagem));
            }
        }
    }

    public boolean containsSuspeito_T(Integer processoj) {
        return FD.containsSuspeito_T(processoj);
    }

    public boolean addTrusted(Integer processoj) {
        return trusted.add(processoj);
    }

    @Override
    public String toString() {
        return "\n-------------Processo " + processo.getId() + "-------------"
                + "\nN: " + n
                + "\nEstado: " + state
                + "\nH: " + H
                + "\nUltimo: " + last
                + "\nQuantidade de permissões: " + perm_count
                + "\nRespostas pendentes: " + Arrays.toString(reply_count)
                + "\nRespostas deferidas: " + Arrays.toString(defer_count)
                + "\nConfiaveis: " + trusted
                + "\nDefeituosos: " + crashed + "\n";
    }

    public boolean containsCrashed(Integer processoj) {
        return crashed.contains(processoj);
    }

    public void decrementarReply_count(int pos, int valor) {
        reply_count[pos] -= valor;
    }

    public void incrementaPerm_count() {
        perm_count++;
    }

    public boolean isReply_count(int pos, int valor) {
        return reply_count[pos] == valor;
    }

    public boolean crashedAdd(Integer valor) {
        return crashed.add(valor);
    }

    public boolean isState(Estado estado) {
        return state.equals(estado);
    }

    public void decrementarN() {
        n--;
    }

    public void decrementarPerm_count() {
        perm_count--;
    }

    private void mudarRecurso() {
        Main.RECURSOS[Main.recursos_uso-1].escolherValor();
    }

}
