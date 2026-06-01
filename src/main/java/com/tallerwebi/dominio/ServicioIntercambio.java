package com.tallerwebi.dominio;

import java.util.List;

public interface ServicioIntercambio {
  /**
   * Procesa la mejora de 4 cartas a una de rareza superior.
   * @param idsCartasEntregadas Lista de IDs de las 4 cartas a sacrificar.
   * @return La nueva carta obtenida aleatoriamente.
   * @throws Exception Si no son 4 cartas, no son de la misma rareza, o son legendarias.
   */
  Carta realizarMejora(List<Long> idsCartasEntregadas) throws Exception;

  /**
   * Calcula y procesa la conversión de una carta en oro.
   * @param idCarta ID de la carta a vender.
   * @return Cantidad de oro obtenida (50% del valor base).
   */
  Double transformarEnOro(Long idCarta);

  /**
   * Devuelve el inventario de cartas del jugador autenticado.
   * @param jugadorId ID del jugador.
   * @return Lista de ItemInventario con sus cartas.
   */
  List<ItemInventario> obtenerInventario(Long jugadorId);
}
