/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.uesb.kmutex1;

import java.util.Random;

/**
 *
 * @author Matheus
 */
public class Recurso {

    private int valor;

    public Recurso() {
        valor = new Random().nextInt(100);
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return String.valueOf(valor);
    }
    
    

}
