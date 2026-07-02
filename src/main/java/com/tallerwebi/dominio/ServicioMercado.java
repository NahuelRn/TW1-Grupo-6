package com.tallerwebi.dominio;

import java.util.List;

public interface ServicioMercado {
  void publicarSolicitud(Long jugadorId, Long idCartaBuscada) throws Exception;

  List<PropuestaIntercambio> obtenerOfertasCompatibles(Long jugadorId);

  boolean usuarioTieneCartaRepetida(Long jugadorId, Long idCarta);

  List<Carta> obtenerCartasFaltantes(Long jugadorId);

  // Propuestas donde soy EMISOR (las que yo publiqué)
  List<PropuestaIntercambio> obtenerMisTrades(Long jugadorId);

  // Propuestas donde soy RECEPTOR (las que yo acepté)
  List<PropuestaIntercambio> obtenerTradesAceptados(Long jugadorId);

  PropuestaIntercambio buscarPorId(Long id);

  List<Carta> obtenerOpcionesRecompensa(PropuestaIntercambio propuesta);

  void finalizarIntercambio(Long jugadorId, Long idPropuesta, Long idCartaRecompensa)
    throws Exception;

  void eliminarMiTrade(Long jugadorId, Long idPropuesta) throws Exception;

  // Devuelve true si el jugadorId corresponde al emisor de la propuesta
  boolean esEmisor(Long jugadorId, PropuestaIntercambio propuesta);
}
