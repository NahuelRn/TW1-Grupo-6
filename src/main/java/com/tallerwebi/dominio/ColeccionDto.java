package com.tallerwebi.dominio;

import java.util.List;
import java.util.Map;

public class ColeccionDto {

  private List<Carta> cartasUnicas;
  private Map<Long, Integer> cantidades;

  public ColeccionDto(List<Carta> cartasUnicas, Map<Long, Integer> cantidades) {
    this.cartasUnicas = cartasUnicas;
    this.cantidades = cantidades;
  }

  public List<Carta> getCartasUnicas() {
    return cartasUnicas;
  }

  public Map<Long, Integer> getCantidades() {
    return cantidades;
  }
}
