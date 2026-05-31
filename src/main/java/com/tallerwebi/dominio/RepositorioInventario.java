package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioInventario {
  void guardar(ItemInventario item);
  void actualizar(ItemInventario item);
  ItemInventario buscarItemDeJugador(Long jugadorId, Long cartaId);
  List<ItemInventario> listarInventarioDeJugador(Long jugadorId);
}
