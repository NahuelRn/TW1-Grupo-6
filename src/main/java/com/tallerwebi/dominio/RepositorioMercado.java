package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioMercado {
  void guardar(PropuestaIntercambio propuesta);
  PropuestaIntercambio buscarPorId(Long id);
  void eliminar(PropuestaIntercambio propuesta);

  // Busca por ID de Usuario (tabla usuario)
  Usuario buscarUsuarioPorId(Long id);

  // *** NUEVO: Busca el Usuario a partir del ID de Jugador (lo que guardamos en sesión) ***
  Usuario buscarUsuarioPorJugadorId(Long jugadorId);

  List<PropuestaIntercambio> listarMisTrades(Usuario usuario);
  List<PropuestaIntercambio> listarTodasLasActivas();
}
