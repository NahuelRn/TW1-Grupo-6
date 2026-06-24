package com.tallerwebi.dominio;

import java.util.List;

public interface ServicioCarta {
  List<Carta> obtenerTodas();
  Carta obtenerCartaPorId(Long id);
  List<ItemInventario> obtenerInventario(Long jugadorId);
  ColeccionDto obtenerColeccionAgrupada(Long jugadorId);

  Carta buscarPorId(Long identificadorCarta);
}
