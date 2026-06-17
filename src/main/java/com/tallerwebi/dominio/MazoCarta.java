package com.tallerwebi.dominio;

import javax.persistence.*;

@Entity
public class MazoCarta {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mazo_id")
  private Mazo mazo;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "carta_id")
  private Carta carta;

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
