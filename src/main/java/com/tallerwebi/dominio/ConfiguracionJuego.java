package com.tallerwebi.dominio;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ConfiguracionJuego {

  public static final String ORO_BASE = "ORO_BASE";
  public static final String EXPERIENCIA_BASE = "EXPERIENCIA_BASE";

  @Id
  private String clave;

  private Integer valor;

  public String getClave() {
    return clave;
  }

  public void setClave(String clave) {
    this.clave = clave;
  }

  public Integer getValor() {
    return valor;
  }

  public void setValor(Integer valor) {
    this.valor = valor;
  }
}
