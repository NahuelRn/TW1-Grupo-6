package com.tallerwebi.dominio;

import javax.persistence.*;

@Entity
public class PropuestaIntercambio {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Usuario usuarioEmisor; // Quien ofrece la carta

  @ManyToOne
  private Carta cartaOfrecida; // La carta repetida

  private String rarezaBuscada; // Lo que pide a cambio

  // --- GETTERS Y SETTERS COMPLETOS ---
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Usuario getUsuarioEmisor() {
    return usuarioEmisor;
  }

  public void setUsuarioEmisor(Usuario usuarioEmisor) {
    this.usuarioEmisor = usuarioEmisor;
  }

  public Carta getCartaOfrecida() {
    return cartaOfrecida;
  }

  public void setCartaOfrecida(Carta cartaOfrecida) {
    this.cartaOfrecida = cartaOfrecida;
  }

  public String getRarezaBuscada() {
    return rarezaBuscada;
  }

  public void setRarezaBuscada(String rarezaBuscada) {
    this.rarezaBuscada = rarezaBuscada;
  }
}
