package com.tallerwebi.dominio;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

@Entity
public class Jugador {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Integer nivel = 1;
  private Integer oro = 0;

  @OneToOne
  @JoinColumn(name = "usuario_id")
  private Usuario usuario;

  @OneToMany(mappedBy = "jugador", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private Set<ItemInventario> inventario = new HashSet<>();

  public Set<ItemInventario> getInventario() {
    return inventario;
  }

  public void setInventario(Set<ItemInventario> inventario) {
    this.inventario = inventario;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getNivel() {
    return nivel;
  }

  public void setNivel(Integer nivel) {
    this.nivel = nivel;
  }

  public Integer getOro() {
    return oro;
  }

  public void setOro(Integer oro) {
    this.oro = oro;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }
}
