package com.tallerwebi.dominio;

import javax.persistence.*;

@Entity
public class Enemigo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nombre;
  private Integer hpBase;
  private String zona;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public Integer getHpBase() {
    return hpBase;
  }

  public void setHpBase(Integer hpBase) {
    this.hpBase = hpBase;
  }

  public String getZona() {
    return zona;
  }

  public void setZona(String zona) {
    this.zona = zona;
  }
}
