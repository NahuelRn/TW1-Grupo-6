package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioMercado {
  void guardar(PropuestaIntercambio propuesta);
  PropuestaIntercambio buscarPorId(Long id);
  void eliminar(PropuestaIntercambio propuesta);

  Usuario buscarUsuarioPorId(Long id);
  Usuario buscarUsuarioPorJugadorId(Long jugadorId);

  // Propuestas donde el usuario es EMISOR
  List<PropuestaIntercambio> listarMisTrades(Usuario usuario);

  // Propuestas donde el usuario es RECEPTOR (trades que aceptó)
  List<PropuestaIntercambio> listarTradesAceptados(Usuario usuario);

  List<PropuestaIntercambio> listarTodasLasActivas();
}
