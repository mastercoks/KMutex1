/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.uesb.kmutex1;

import br.edu.uesb.kmutex1.enumerado.TipoMensagem;
import br.edu.uesb.kmutex1.mensagens.Mensagem;
import br.edu.uesb.kmutex1.rede.Enviar;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    public DetectorFalhas(Processo processo) {
        this.processo = processo;
        this.suspeitos_S = new ArrayList<>();
        this.suspeitos_T = new ArrayList<>();

        suspeitos_S.addAll(processo.getProcessos());
    }

    @Override
    public void run() {
        while (true) {
            for (Integer processoj : processo.getProcessos()) {
                if (processo.containsTrusted(processoj) && suspeitos_T.contains(processoj)) {
                    processo.removeTrusted(processoj);
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
                processo.executar(new Enviar("localhost", 7000 + processok, mensagem));
            }
        }
    }

}
