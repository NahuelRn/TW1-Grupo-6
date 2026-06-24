package com.tallerwebi.dominio;

import java.util.List;

public interface ServicioMercado {
  void publicarSolicitud(Long idUsuario, Long idCartaBuscada) throws Exception;
  List<PropuestaIntercambio> obtenerOfertasCompatibles(Long idUsuario);
  List<PropuestaIntercambio> obtenerMisTrades(Long idUsuario);
  List<Carta> obtenerCartasFaltantes(Long idUsuario);
  PropuestaIntercambio buscarPorId(Long id);
  List<Carta> obtenerOpcionesRecompensa(PropuestaIntercambio propuesta);
  void finalizarIntercambio(Long idReceptor, Long idOferta, Long idCartaRecompensa)
    throws Exception;
  void eliminarMiTrade(Long idUsuario, Long idOferta) throws Exception;
}
