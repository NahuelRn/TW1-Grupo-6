package com.tallerwebi.dominio;

import javax.persistence.*;

@Entity
public class Usuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String email;
  private String password;
  private String rol;
  private Boolean activo = false;

  private Integer oro = 150;
  private Integer experiencia = 50;

  private Integer nivel = 1;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "mazo_activo_id")
  private Mazo mazoActivo;

  public Mazo getMazoActivo() {
    return mazoActivo;
  }

  public void setMazoActivo(Mazo mazoActivo) {
    this.mazoActivo = mazoActivo;
  }

  @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private Jugador jugador;

  public Jugador getJugador() {
    return jugador;
  }

  public void setJugador(Jugador jugador) {
    this.jugador = jugador;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getRol() {
    return rol;
  }

  public void setRol(String rol) {
    this.rol = rol;
  }

  public Boolean getActivo() {
    return activo;
  }

  public void setActivo(Boolean activo) {
    this.activo = activo;
  }

  public void activar() {
    activo = true;
  }

  public Integer getOro() {
    return this.oro;
  }

  public void setOro(Integer oro) {
    this.oro = oro;
  }

  public void sumarOro(Integer oro) {
    this.oro += oro;
  }

  public Integer getExperiencia() {
    return this.experiencia;
  }

  public void setExperiencia(Integer experiencia) {
    this.experiencia = experiencia;
  }

  public Integer getNivel() {
    //      Cada 100 puntos de experiencia sube 1 nivel.
    return (this.experiencia / 100) + 1;
  }

  public void setNivel(Integer nivel) {
    this.nivel = nivel;
  }

  public void sumarExperiencia(Integer experiencia) {
    this.experiencia += experiencia;
  }

  // Método helper para conectar con el inventario de su Jugador sin romper la estructura
  public java.util.Set<ItemInventario> getInventario() {
    if (this.jugador != null) {
      return this.jugador.getInventario();
    }
    return new java.util.HashSet<>();
  }
}
