package com.tallerwebi.dominio;

public class RecompensaDTO {

  private Integer oro = 0;
  private Integer experiencia = 0;
  private Long idCarta;

  public Integer getExperiencia() {
    return experiencia;
  }

  public void setExperiencia(int experiencia) {
    this.experiencia = experiencia;
  }

  public Integer getOro() {
    return oro;
  }

  public void setOro(int oro) {
    this.oro = oro;
  }

  public Long getIdCarta() {
    return idCarta;
  }

  public void setIdCarta(Long idCarta) {
    this.idCarta = idCarta;
  }
}
