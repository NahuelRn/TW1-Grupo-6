package com.tallerwebi.dominio;

import java.util.List;

public interface ServicioHistorial {
  void guardarHistorialPartidaServicio(HistorialPartida historialPartida);
  List<HistorialPartida> listarHistorialPorUsuario(Long identificadorUsuario);
}
