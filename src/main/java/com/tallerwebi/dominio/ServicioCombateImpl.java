package com.tallerwebi.dominio;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ServicioCombateImpl implements ServicioCombate {

  private RepositorioPartida repositorioPartida;
  private RepositorioCarta repositorioCarta; // Lo inyectamos para leer el daño de la carta

  @Autowired
  public ServicioCombateImpl(
    RepositorioPartida repositorioPartida,
    RepositorioCarta repositorioCarta
  ) {
    this.repositorioPartida = repositorioPartida;
    this.repositorioCarta = repositorioCarta;
  }

  // OJO: Cambié Integer por Long en identificadorCarta para que matchee con Carta.getId()
  @Override
  public Integer jugarCarta(Long identificadorCarta, Long identificadorPartida) {
    Partida partida = obtenerPartidaPorIdentificador(identificadorPartida);
    Carta cartaJugada = repositorioCarta.buscarPorId(identificadorCarta);

    // 1. Turno Jugador: Calculamos el daño de la carta (Si es null, pega 0)
    int danoJugador = (cartaJugada.getDano() != null) ? cartaJugada.getDano() : 0;
    partida.setHpEnemigo(partida.getHpEnemigo() - danoJugador);

    // Chequeo de victoria
    if (partida.getHpEnemigo() <= 0) {
      partida.setEnumEstadoPartida(EnumEstadoPartida.GANADOR_JUGADOR);
      return danoJugador; // Retornamos para avisarle al log
    }

    // 2. Turno Zombi (Automático): Pega 5 de daño fijo por turno
    int danoZombi = 5;
    partida.setHpJugador(partida.getHpJugador() - danoZombi);

    // Chequeo de derrota
    if (partida.getHpJugador() <= 0) {
      partida.setEnumEstadoPartida(EnumEstadoPartida.GANADOR_ENEMIGO);
    }

    repositorioPartida.modificar(partida); // Guardamos el estado actual
    return danoJugador;
  }

  @Override
  public Partida obtenerPartidaPorIdentificador(Long identificadorPartida) {
    return this.repositorioPartida.buscarPartidaPorIdentificador(identificadorPartida);
  }
}
