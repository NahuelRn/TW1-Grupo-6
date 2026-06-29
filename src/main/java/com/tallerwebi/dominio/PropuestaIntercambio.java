package com.tallerwebi.dominio;

import java.time.LocalDateTime;
import javax.persistence.*;

@Entity
public class PropuestaIntercambio {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  private Usuario usuarioEmisor; // El que crea la solicitud (busca la carta)

  @ManyToOne(fetch = FetchType.LAZY)
  private Carta cartaBuscada; // La carta específica que desea obtener

  @ManyToOne(fetch = FetchType.LAZY)
  private Carta cartaOfrecida; // La carta que el receptor le elegirá como pago

  private String estado = "ACTIVA"; // "ACTIVA" o "FINALIZADA"
  private LocalDateTime fechaCreacion = LocalDateTime.now();

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

  public Carta getCartaBuscada() {
    return cartaBuscada;
  }

  public void setCartaBuscada(Carta cartaBuscada) {
    this.cartaBuscada = cartaBuscada;
  }

  public Carta getCartaOfrecida() {
    return cartaOfrecida;
  }

  public void setCartaOfrecida(Carta cartaOfrecida) {
    this.cartaOfrecida = cartaOfrecida;
  }

  public String getEstado() {
    return estado;
  }

  public void setEstado(String estado) {
    this.estado = estado;
  }

  public LocalDateTime getFechaCreacion() {
    return fechaCreacion;
  }

  public void setFechaCreacion(LocalDateTime fechaCreacion) {
    this.fechaCreacion = fechaCreacion;
  }
}
