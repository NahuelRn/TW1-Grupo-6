package com.tallerwebi.dominio;

public interface RepositorioPartida {
  Partida buscarPartidaPorIdentificador(Long identificador);
//  void modificar(Partida partida);
  void guardar(Partida partida);
  void actualizar(Partida partida);
  Partida buscarPartidaActivaPorUsuario(Long usuarioId);
}