package com.tallerwebi.dominio;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ServicioCombateImpl implements ServicioCombate {

  private static final int TURNO_JUGADOR = 1;
  private static final int TURNO_ENEMIGO = 2;

  private static final int MAX_CARTAS_REPETIDAS = 3;

  private int contadorCartasRepetidas = 0;

  private RepositorioPartida repositorioPartida;

  @Autowired
  public ServicioCombateImpl(RepositorioPartida repositorioPartida) {
    this.repositorioPartida = repositorioPartida;
  }

  @Override
  public Integer jugarCarta(Integer identificadorCarta, Long identificadorPartida) {
    validarTurno(identificadorPartida);
    validarCartaEnMano(identificadorCarta, identificadorPartida);

    Integer danioCartaHaciaEnemigo = calcularEfectoCarta();

    Partida partida = obtenerPartidaPorIdentificador(identificadorPartida);
    partida.setHpEnemigo(partida.getHpEnemigo() - danioCartaHaciaEnemigo);
    actualizarEstadoPartida(partida);

    return danioCartaHaciaEnemigo;
  }

  private void validarTurno(Long identificadorPartida) {
    Partida partida = obtenerPartidaPorIdentificador(identificadorPartida);

    if (partida.getTurno() == null) {
      throw new RuntimeException("Error, turno no definido.");
    }

    if (partida.getTurno() != TURNO_JUGADOR) {
      throw new RuntimeException("Turno del enemigo.");
    }
  }

  private void validarCartaEnMano(Integer identificadorCarta, Long identificadorPartida) {
    Partida partida = obtenerPartidaPorIdentificador(identificadorPartida);

    if (partida.getCartasEnManoJugador() == null || partida.getCartasEnManoJugador().size() == 0) {
      throw new RuntimeException("Error, el jugador no tiene cartas en mano.");
    }

    for (Integer carta : partida.getCartasEnManoJugador()) {
      if (carta.equals(identificadorCarta)) {
        contadorCartasRepetidas++;

        if (contadorCartasRepetidas > MAX_CARTAS_REPETIDAS) {
          throw new RuntimeException("Error, máximo 3 cartas del mismo tipo.");
        }
      }
    }
  }

  private Integer calcularEfectoCarta() {
    int valorBaseCarta = 10;
    int multiplicador = 5;
    int factorSuerte = 5;

    return valorBaseCarta * multiplicador + factorSuerte;
  }

  @Override
  public Partida obtenerPartidaPorIdentificador(Long identificadorPartida) {
    return this.repositorioPartida.buscarPartidaPorIdentificador(identificadorPartida);
  }

  private void actualizarEstadoPartida(Partida partida) {
    cambiarTurno(partida);
    cambiarEstado(partida);
  }

  private void cambiarTurno(Partida partida) {
    if (partida.getTurno() == TURNO_JUGADOR) {
      partida.setTurno(TURNO_ENEMIGO);
    } else {
      partida.setTurno(TURNO_JUGADOR);
    }
  }

  private void cambiarEstado(Partida partida) {
    if (partida.getHpEnemigo() <= 0) {
      partida.setEnumEstadoPartida(EnumEstadoPartida.GANADOR_JUGADOR);
    } else if (partida.getHpJugador() <= 0) {
      partida.setEnumEstadoPartida(EnumEstadoPartida.GANADOR_ENEMIGO);
    }
  }
}
