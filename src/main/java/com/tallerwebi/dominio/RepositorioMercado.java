package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioMercado {
  void guardar(PropuestaIntercambio propuesta);
  PropuestaIntercambio buscarPorId(Long id);
  void eliminar(PropuestaIntercambio propuesta);
  Usuario buscarUsuarioPorId(Long id);

  // Métodos nuevos para el modelo INTERCAMBIO 1.1
  List<PropuestaIntercambio> listarTodasLasActivas();
  List<PropuestaIntercambio> listarMisTrades(Usuario usuarioActual);
  List<Carta> listarTodasLasCartas();
}
