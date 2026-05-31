package com.tallerwebi.dominio;

import javax.persistence.*;

@Entity
public class MazoCarta {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Muchas instancias de esta relación pertenecen a un solo Mazo
  @ManyToOne
  @JoinColumn(name = "mazo_id")
  private Mazo mazo;

  // Muchas instancias de esta relación apuntan a una sola Carta
  @ManyToOne
  @JoinColumn(name = "carta_id")
  private Carta carta;

  // Getters y Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Mazo getMazo() {
    return mazo;
  }

  public void setMazo(Mazo mazo) {
    this.mazo = mazo;
  }

  public Carta getCarta() {
    return carta;
  }

  public void setCarta(Carta carta) {
    this.carta = carta;
  }
}
