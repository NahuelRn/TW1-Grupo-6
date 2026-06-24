package com.tallerwebi.dominio;

import java.util.List;

public interface ServicioMercado {
  void publicarSolicitud(Usuario usuario, Long idCartaBuscada) throws Exception;
  List<PropuestaIntercambio> obtenerOfertasCompatibles(Usuario usuarioActual);
  List<PropuestaIntercambio> obtenerMisTrades(Usuario usuarioActual);
  List<Carta> obtenerCartasFaltantes(Usuario usuarioActual);
  PropuestaIntercambio buscarPorId(Long id) throws Exception;
  List<Carta> obtenerOpcionesRecompensa(PropuestaIntercambio propuesta);
  void finalizarIntercambio(Usuario usuarioReceptor, Long idOferta, Long idCartaRecompensa)
    throws Exception;
  void eliminarMiTrade(Usuario usuarioActual, Long idOferta) throws Exception;
}
