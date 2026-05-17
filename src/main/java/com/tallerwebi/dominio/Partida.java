package com.tallerwebi.dominio;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Entity
public class Partida {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  public Long getId() {
    return id;
  }

  public Partida setId(Long id) {
    this.id = id;
    return this;
  }

  private Integer hpJugador;

  public Integer getHpJugador() {
    return hpJugador;
  }

  public Partida setHpJugador(Integer hpJugador) {
    this.hpJugador = hpJugador;
    return this;
  }

  private Integer hpEnemigo;

  public Integer getHpEnemigo() {
    return hpEnemigo;
  }

  public Partida setHpEnemigo(Integer hpEnemigo) {
    this.hpEnemigo = hpEnemigo;
    return this;
  }

  @ManyToOne
  private Usuario usuario;

  public Usuario getUsuario() {
    return usuario;
  }

  public Partida setUsuario(Usuario usuario) {
    this.usuario = usuario;
    return this;
  }

  @ManyToOne
  private Enemigo enemigo;

  public Enemigo getEnemigo() {
    return enemigo;
  }

  public Partida setEnemigo(Enemigo enemigo) {
    this.enemigo = enemigo;
    return this;
  }

  // @Transient significa que esta lista no se va a guardar en una tabla de la BD.
  // Es temporal, solo dura mientras la partida está en memoria.
  @Transient
  private List<Carta> manoJugador = new ArrayList<>();

  public List<Carta> getManoJugador() {
    return manoJugador;
  }

  public Partida setManoJugador(List<Carta> manoJugador) {
    this.manoJugador = manoJugador;
    return this;
  }
}
