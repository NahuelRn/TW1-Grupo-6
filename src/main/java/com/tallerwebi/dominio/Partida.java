package com.tallerwebi.dominio;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

public class Partida {

//  private Integer identificadorPartida;
    private Integer hpJugador;
    private Integer hpEnemigo;

    private Integer turno;

//  private Usuario usuario;
//  private Enemigo enemigo;

    private String estado;

    private List<Integer> cartasEnManoJugador;

    public Partida(Integer hpJugador, Integer hpEnemigo, Integer turno) {
        this.hpJugador = hpJugador;
        this.hpEnemigo = hpEnemigo;
        this.turno = turno;
        this.cartasEnManoJugador = new ArrayList<>();
    }

    public Integer getHpJugador() {
        return this.hpJugador;
    }

    public void setHpJugador(Integer hpJugador) {
        this.hpJugador = hpJugador;
    }

    public Integer getHpEnemigo() {
        return this.hpEnemigo;
    }

    public void setHpEnemigo(Integer hpEnemigo) {
        this.hpEnemigo = hpEnemigo;
    }

    public Integer getTurno() {
        return this.turno;
    }

    public void setTurno(Integer turno) {
        this.turno = turno;
    }

    public void setCartasEnManoJugador(List<Integer> cartasEnManoJugador) {
        this.cartasEnManoJugador = cartasEnManoJugador;
    }

    public List<Integer> getCartasEnManoJugador() {
        return cartasEnManoJugador;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
