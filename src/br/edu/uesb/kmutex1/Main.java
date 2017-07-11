/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.uesb.kmutex1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Matheus
 */
public class Main {

    public static Recurso[] RECURSOS;
    public static int recursos_uso = 0;

    private final Processo processo;

    public Main(int id, int quant_recursos, int quant_processos) {
        RECURSOS = new Recurso[quant_recursos];
        processo = new Processo(gerarProcessos(quant_processos), id, quant_recursos);
    }

    private List<Integer> gerarProcessos(int quant_processos) {
        List<Integer> processos = new ArrayList<>();
        for (int i = 0; i < quant_processos; i++) {
            processos.add(i);
        }
        return processos;
    }

    private static void gerarRecursos() {
        for (int i = 0; i < RECURSOS.length; i++) {
            RECURSOS[i] = new Recurso();
        }
    }

    public static void imprimirRecursos() {
        for (Recurso recurso_aux : Main.RECURSOS) {
            System.out.print(recurso_aux + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Main[] main = new Main[6];
        for (int i = 0; i < main.length; i++) {
            main[i] = new Main(i, 2, 6);
        }
        gerarRecursos();

        for (Main main1 : main) {
            main1.processo.iniciar();
        }
        try {
            imprimirRecursos();
            Thread.sleep(2000);
            main[5].processo.getRaymond().request_resource();
            main[2].processo.getRaymond().request_resource();
            main[5].processo.getRaymond().release_resource();
            main[2].processo.getRaymond().release_resource();
            imprimirRecursos();

        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
