package com.tallerwebi.dominio;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.*;

@Entity
public class Mazo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToMany(mappedBy = "mazo", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private List<MazoCarta> mazoCartas = new ArrayList<>();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public List<MazoCarta> getMazoCartas() {
    return mazoCartas;
  }

  public List<Carta> getCartas() {
    return mazoCartas.stream().map(MazoCarta::getCarta).collect(Collectors.toList());
  }

  public void setCartas(List<Carta> cartas) {
    this.mazoCartas = new ArrayList<>();
    for (Carta carta : cartas) {
      MazoCarta nexo = new MazoCarta();
      nexo.setMazo(this);
      nexo.setCarta(carta);
      this.mazoCartas.add(nexo);
    }
  }
}
