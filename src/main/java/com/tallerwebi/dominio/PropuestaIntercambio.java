package com.tallerwebi.dominio;

import java.time.LocalDateTime;
import javax.persistence.*;

@Entity
public class PropuestaIntercambio {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Quien publica la solicitud (quiere obtener cartaBuscada)
  @ManyToOne
  @JoinColumn(name = "usuario_emisor_id")
  private Usuario usuarioEmisor;

  // Quien acepta y completa el trade
  @ManyToOne
  @JoinColumn(name = "usuario_receptor_id")
  private Usuario usuarioReceptor;

  // La carta específica que el emisor DESEA OBTENER
  @ManyToOne
  @JoinColumn(name = "carta_buscada_id")
  private Carta cartaBuscada;

  // La carta que el emisor entrega como recompensa (se asigna al finalizar)
  @ManyToOne
  @JoinColumn(name = "carta_ofrecida_id")
  private Carta cartaOfrecida;

  // "ACTIVA" | "FINALIZADA"
  private String estado = "ACTIVA";

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

  public Usuario getUsuarioReceptor() {
    return usuarioReceptor;
  }

  public void setUsuarioReceptor(Usuario usuarioReceptor) {
    this.usuarioReceptor = usuarioReceptor;
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
