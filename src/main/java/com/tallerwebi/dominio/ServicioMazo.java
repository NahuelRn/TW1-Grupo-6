package com.tallerwebi.dominio;

import java.util.List;

public interface ServicioMazo {
  void validarYGuardarMazo(Mazo mazo, Long jugadorId) throws Exception;
  List<Carta> buscarCartasPorIds(List<Long> ids);
  List<ItemInventario> obtenerInventarioPorJugador(Long jugadorId);
}
