/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.uesb.kmutex1;

import br.edu.uesb.kmutex1.enumerado.TipoValor;
import java.util.Random;

/**
 *
 * @author Matheus
 */
public class Recurso {

    private TipoValor valor;

    public Recurso() {
        valor = escolherValor();
    }

    public TipoValor getValor() {
        return valor;
    }

    public void setValor(TipoValor valor) {
        this.valor = valor;
    }

    public TipoValor escolherValor() {
        int num_rand = new Random().nextInt(3);
        switch (num_rand) {
            case 0:
                return TipoValor.STAR_WARS;
            case 1:
                return TipoValor.STAR_TREK;
            case 2:
                return TipoValor.NENHUMA;
        }
        return null;
    }

    @Override
    public String toString() {
        return valor.toString();
    }
    
    

}
