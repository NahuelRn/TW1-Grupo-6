package com.tallerwebi.dominio;

import java.util.List;

public interface ServicioIntercambio {
  List<ItemInventario> obtenerInventario(Long jugadorId);
  List<ItemInventario> obtenerInventarioFiltrado(Long jugadorId, String rareza); // Agregar este
  Carta realizarMejora(Long jugadorId, List<Long> idsCartasEntregadas) throws Exception;
  Double transformarEnOro(Long idCarta);
}
