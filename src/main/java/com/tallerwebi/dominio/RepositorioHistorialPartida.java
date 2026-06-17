package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioHistorialPartida {
  void guardarHistorialPartidaRepositorio(HistorialPartida historialPartida);
  List<HistorialPartida> listarPorUsuario(Long identificadorUsuario);
}
