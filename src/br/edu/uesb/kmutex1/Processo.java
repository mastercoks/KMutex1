/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.uesb.kmutex1;

import br.edu.uesb.kmutex1.mensagens.Mensagem;
import br.edu.uesb.kmutex1.rede.NetworkService;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 * @author Matheus
 */
public class Processo {


    private final ExecutorService executor_service;
    private List<Integer> processos;
    private int id;
    private final Raymond raymond;
    private int quant_recursos;
    private boolean crashed;

    public Processo(List<Integer> processos, int id, int quant_recursos) {
        this.executor_service = Executors.newCachedThreadPool();
        this.processos = processos;
        this.id = id;
        this.raymond = new Raymond(processos.size(), this);
        this.quant_recursos = quant_recursos;
        this.crashed = false;
    }

    public void iniciar() {
        executar(raymond);
    }

    public List<Integer> getProcessos() {
        return processos;
    }

    public void setProcessos(List<Integer> processos) {
        this.processos = processos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void executar(Runnable r) {
        executor_service.execute(r);
    }

    public Future<Mensagem> receber(NetworkService rede) {
        return executor_service.submit(rede);
    }

    public boolean containsTrusted(Integer processoj) {
        return raymond.getTrusted().contains(processoj);
    }

    public boolean removeTrusted(Integer processoj) {
        return raymond.getTrusted().remove(processoj);
    }

    boolean containsCrashed(Integer processok) {
        return raymond.getCrashed().contains(processok);
    }

    public boolean containsSuspeito_T(Integer processoj) {
        return raymond.containsSuspeito_T(processoj);
    }

    public boolean addTrusted(Integer processoj) {
        return raymond.addTrusted(processoj);
    }

    public Raymond getRaymond() {
        return raymond;
    }

    public int getQuant_recursos() {
        return quant_recursos;
    }

    public void setQuant_recursos(int quant_recursos) {
        this.quant_recursos = quant_recursos;
    }

    public boolean isCrashed() {
        return crashed;
    }

    public void setCrashed(boolean crashed) {
        this.crashed = crashed;
    }

}
