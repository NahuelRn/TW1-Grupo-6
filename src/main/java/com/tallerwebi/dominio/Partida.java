package com.tallerwebi.dominio;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Entity
public class Partida {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Usuario usuario;

  @ManyToOne
  private Enemigo enemigo;

  // --- VARIABLES COMPARTIDAS ---
  private Integer hpJugador;
  private Integer hpEnemigo;

  // --- LO DE MIKA (Combate) ---
  private Integer turno;
  private EnumEstadoPartida enumEstadoPartida;
  private String estado;

  @Transient
  private List<Carta> manoJugador = new ArrayList<>();

  @Transient
  private List<Integer> cartasEnManoJugador = new ArrayList<>();

  // --- CONSTRUCTORES ---
  public Partida() {}

  public Partida(Integer hpJugador, Integer hpEnemigo, Integer turno) {
    this.hpJugador = hpJugador;
    this.hpEnemigo = hpEnemigo;
    this.turno = turno;
    this.cartasEnManoJugador = new ArrayList<>();
    this.enumEstadoPartida = EnumEstadoPartida.ACTIVA;
  }

  // GETTERS Y SETTERS

  public Long getId() {
    return id;
  }

  public Partida setId(Long id) {
    this.id = id;
    return this;
  }

  public Integer getHpJugador() {
    return hpJugador;
  }

  public Partida setHpJugador(Integer hpJugador) {
    this.hpJugador = hpJugador;
    return this;
  }

  public Integer getHpEnemigo() {
    return hpEnemigo;
  }

  public Partida setHpEnemigo(Integer hpEnemigo) {
    this.hpEnemigo = hpEnemigo;
    return this;
  }

  public Integer getTurno() {
    return turno;
  }

  public void setTurno(Integer turno) {
    this.turno = turno;
  }

  public String getEstado() {
    return estado;
  }

  public void setEstado(String estado) {
    this.estado = estado;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public Partida setUsuario(Usuario usuario) {
    this.usuario = usuario;
    return this;
  }

  public Enemigo getEnemigo() {
    return enemigo;
  }

  public Partida setEnemigo(Enemigo enemigo) {
    this.enemigo = enemigo;
    return this;
  }

  public List<Carta> getManoJugador() {
    return manoJugador;
  }

  public Partida setManoJugador(List<Carta> manoJugador) {
    this.manoJugador = manoJugador;
    return this;
  }

  public List<Integer> getCartasEnManoJugador() {
    return cartasEnManoJugador;
  }

  public void setCartasEnManoJugador(List<Integer> cartasEnManoJugador) {
    this.cartasEnManoJugador = cartasEnManoJugador;
  }

  public EnumEstadoPartida getEnumEstadoPartida() {
    return enumEstadoPartida;
  }

  public void setEnumEstadoPartida(EnumEstadoPartida enumEstadoPartida) {
    this.enumEstadoPartida = enumEstadoPartida;
  }
}
