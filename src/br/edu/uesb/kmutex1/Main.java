/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.uesb.kmutex1;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matheus
 */
public class Main {

    public static Recurso[] RECURSOS;
    public static int RECURSOS_USO = -1;
    public static int[] PROCESSOS_CS = {1, 2, 5};
    public static int[] PROCESSOS_CORRETOS = {1, 2};

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

    public static void main(String[] args) {
        Main[] main = new Main[6];
        for (int i = 0; i < main.length; i++) {
            main[i] = new Main(i, 2, 6);
        }
        gerarRecursos();

//        System.out.println("Recursos: " + Arrays.toString(RECURSOS));
        for (Main main1 : main) {
            main1.processo.iniciar();
        }
//        try {
//            Thread.sleep(2000);
//            main[5].processo.getRaymond().request_resource();
//            main[2].processo.getRaymond().request_resource();
//            main[1].processo.getRaymond().request_resource();
//            Thread.sleep(10000);
//            System.out.println("Recursos: " + Arrays.toString(RECURSOS));
//        } catch (InterruptedException | IOException | ClassNotFoundException ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//        }

    }
}
