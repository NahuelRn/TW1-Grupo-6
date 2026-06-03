package com.tallerwebi.dominio;

public interface ServicioCombate {
  Integer jugarCarta(Integer identificadorCarta, Long identificadorPartida);

  Partida obtenerPartidaPorIdentificador(Long identificadorPartida);
}
