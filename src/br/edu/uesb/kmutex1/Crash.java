/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.uesb.kmutex1;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Matheus
 */
public class Crash implements Runnable {

    private final Processo processo;

    public Crash(Processo processo) {
        this.processo = processo;
    }

    @Override
    public void run() {
        for (int processoj : Main.PROCESSOS_CORRETOS) {
            if (processoj == processo.getId()) {
                return;
            }
        }

        if (0 == new Random().nextInt(3)) {
            try {
                Thread.sleep(1000 * (new Random().nextInt(5) + 1));
            } catch (InterruptedException ex) {
                Logger.getLogger(DetectorFalhas.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.err.println("Processo[" + processo.getId() + "] falhou!");
            processo.setCrashed(true);
        }
    }

}
