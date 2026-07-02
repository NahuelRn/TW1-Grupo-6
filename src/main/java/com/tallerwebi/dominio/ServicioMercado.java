package com.tallerwebi.dominio;

import java.util.List;

public interface ServicioMercado {
  void publicarSolicitud(Long jugadorId, Long idCartaBuscada) throws Exception;

  List<PropuestaIntercambio> obtenerOfertasCompatibles(Long jugadorId);

  boolean usuarioTieneCartaRepetida(Long jugadorId, Long idCarta);

  List<Carta> obtenerCartasFaltantes(Long jugadorId);

  List<PropuestaIntercambio> obtenerMisTrades(Long jugadorId);

  PropuestaIntercambio buscarPorId(Long id);

  List<Carta> obtenerOpcionesRecompensa(PropuestaIntercambio propuesta);

  void finalizarIntercambio(Long jugadorId, Long idPropuesta, Long idCartaRecompensa)
    throws Exception;

  void eliminarMiTrade(Long jugadorId, Long idPropuesta) throws Exception;
}
