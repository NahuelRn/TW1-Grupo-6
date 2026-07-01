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
  private Integer danoMin;
  private Integer danoMax;
  private String imagen;
  private Integer probabilidad;

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

  public Integer getDanoMin() {
    return danoMin;
  }

  public void setDanoMin(Integer danoMin) {
    this.danoMin = danoMin;
  }

  public Integer getDanoMax() {
    return danoMax;
  }

  public void setDanoMax(Integer danoMax) {
    this.danoMax = danoMax;
  }

  public String getImagen() {
    return imagen;
  }

  public void setImagen(String imagen) {
    this.imagen = imagen;
  }

  public Integer getProbabilidad() {
    return probabilidad;
  }

  public void setProbabilidad(Integer probabilidad) {
    this.probabilidad = probabilidad;
  }
}
