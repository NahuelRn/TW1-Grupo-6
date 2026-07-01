package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioCarta {
  List<Carta> listarTodas();
  Carta buscarPorId(Long id);

  List<Carta> buscarPorRareza(String rareza);

  List<Carta> listarCartasFaltantes(List<Long> idsCartasPoseidas);
  Carta buscarPorNombre(String nombre);

  void guardar(Carta carta);
}
