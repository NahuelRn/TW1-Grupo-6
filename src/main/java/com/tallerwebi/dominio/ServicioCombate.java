package com.tallerwebi.dominio;

import java.util.Map;

public interface ServicioCombate {
  Map<String, String> obtenerConfiguracionZona(String zona);

  String jugarTurno(Long idPartida, Long idCarta);

  //  String jugarTurno(Partida partidaActual, Long idCarta);

  Partida obtenerPartidaPorIdentificador(Long identificadorPartida);
}
