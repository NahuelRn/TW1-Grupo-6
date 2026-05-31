package com.tallerwebi.dominio;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Entity
public class Mazo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Relación Muchos a Muchos: Un mazo tiene muchas cartas
  @ManyToMany(fetch = FetchType.EAGER)
  private List<Carta> cartas = new ArrayList<>();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public List<Carta> getCartas() {
    return cartas;
  }

  @Transient
  private List<MazoCarta> mazoCartas = new ArrayList<>();

  public void setCartas(List<Carta> cartas) {
    this.cartas = cartas;
  }

  public List<MazoCarta> getMazoCartas() {
    return this.mazoCartas;
  }
}
