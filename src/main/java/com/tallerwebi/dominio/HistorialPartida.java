package com.tallerwebi.dominio;

import java.time.LocalDate;
import javax.persistence.*;

@Entity
public class HistorialPartida {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long identificador;

  private LocalDate localDate;
  private String resultado;
  private Integer oroGanado;
  private Integer experienciaGanada;

  @ManyToOne
  private Usuario usuario;

  public Long getIdentificador() {
    return this.identificador;
  }

  public LocalDate getFecha() {
    return this.localDate;
  }

  public String getResultado() {
    return this.resultado;
  }

  public Integer getOroGanado() {
    return this.oroGanado;
  }

  public Integer getExperienciaGanada() {
    return this.experienciaGanada;
  }

  public Usuario getUsuario() {
    return this.usuario;
  }

  public void setIdentificador(Long identificador) {
    this.identificador = identificador;
  }

  public void setLocalDate(LocalDate localDate) {
    this.localDate = localDate;
  }

  public void setResultado(String resultado) {
    this.resultado = resultado;
  }

  public void setOroGanado(Integer oroGanado) {
    this.oroGanado = oroGanado;
  }

  public void setExperienciaGanada(Integer experienciaGanada) {
    this.experienciaGanada = experienciaGanada;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }
}
