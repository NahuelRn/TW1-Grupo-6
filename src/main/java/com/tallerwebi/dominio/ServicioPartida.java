package com.tallerwebi.dominio;

import java.util.List;

public interface ServicioPartida {
  Partida iniciarPartida(Usuario usuario, String zona);

  void gestionarRoboDeCarta(Long idCartaJugada, List<Long> idsMano, List<Long> idsMazoRobo);
}
