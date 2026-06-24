package com.tallerwebi.dominio;

public interface ServicioCombate {
  Integer jugarCarta(Long identificadorCarta, Long identificadorPartida);

  Partida obtenerPartidaPorIdentificador(Long identificadorPartida);
}
