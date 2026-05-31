package com.tallerwebi.dominio;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Entity
public class Mazo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Un mazo tiene muchas entradas en la tabla intermedia
  // mappedBy indica el nombre del atributo en la clase MazoCarta
  @OneToMany(mappedBy = "mazo", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private List<MazoCarta> mazoCartas = new ArrayList<>();

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
