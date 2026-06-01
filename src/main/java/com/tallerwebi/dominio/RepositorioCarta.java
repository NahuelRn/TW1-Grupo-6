package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioCarta {
  List<Carta> listarTodas();
  Carta buscarPorId(Long id);

  List<Carta> buscarPorRareza(String rareza);
}
