/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.uesb.kmutex1.mensagens;

import br.edu.uesb.kmutex1.enumerado.TipoMensagem;
import java.io.Serializable;

/**
 *
 * @author Matheus
 */
public class Mensagem implements Serializable {

    private int id_origem;
    private int id_destino;
    private TipoMensagem tipo;
    private int valor;

    public Mensagem(int id_origem, int id_destino, TipoMensagem tipo) {
        this.id_origem = id_origem;
        this.id_destino = id_destino;
        this.tipo = tipo;
    }

    public Mensagem(int id_origem, int id_destino, TipoMensagem tipo, int valor) {
        this.id_origem = id_origem;
        this.id_destino = id_destino;
        this.tipo = tipo;
        this.valor = valor;
    }

    public int getId_origem() {
        return id_origem;
    }

    public void setId_origem(int id_origem) {
        this.id_origem = id_origem;
    }

    public int getId_destino() {
        return id_destino;
    }

    public void setId_destino(int id_destino) {
        this.id_destino = id_destino;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public TipoMensagem getTipo() {
        return tipo;
    }

    public void setTipo(TipoMensagem tipo) {
        this.tipo = tipo;
    }

}
