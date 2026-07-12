package com.tallerwebi.dominio;

import java.util.List;

public interface ServicioTienda {
  void comprarCarta(Usuario usuario, Long identificadorCarta);

  List<Carta> listarCartas(); // no entendi esta parte
}
