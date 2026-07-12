package com.tallerwebi.dominio;

public interface ServicioUsuario {
  void aplicarRecompensa(Usuario usuario, Partida partida);
  Usuario buscarPorId(Long id);
}
