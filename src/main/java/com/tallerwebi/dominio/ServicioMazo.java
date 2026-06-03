package com.tallerwebi.dominio;

import java.util.List;

public interface ServicioMazo {
  void validarYGuardarMazo(Mazo mazo) throws Exception;
  List<Carta> buscarCartasPorIds(List<Long> ids);
  List<Carta> obtenerInventarioPorJugador(Long jugadorId); // <-- NUEVO MÉTODO
}
