package com.tallerwebi.dominio;

import javax.persistence.*;

@Entity
public class ItemInventario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "jugador_id")
  private Jugador jugador;

  @ManyToOne
  @JoinColumn(name = "carta_id")
  private Carta carta;

  private Integer cantidad = 1;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Jugador getJugador() {
    return jugador;
  }

  public void setJugador(Jugador jugador) {
    this.jugador = jugador;
  }

  public Carta getCarta() {
    return carta;
  }

  public void setCarta(Carta carta) {
    this.carta = carta;
  }

  public Integer getCantidad() {
    return cantidad;
  }

  public void setCantidad(Integer cantidad) {
    this.cantidad = cantidad;
  }

  public void restarCantidad(Integer cuanto) {
    this.cantidad -= cuanto;
  }

  public void sumarCantidad(Integer cuanto) {
    this.cantidad += cuanto;
  }
}
