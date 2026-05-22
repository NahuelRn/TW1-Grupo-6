package com.tallerwebi.dominio;

import java.util.ArrayList;
import org.springframework.stereotype.Service;

@Service
public class ServicioCombate {

  private static final int TURNO_JUGADOR = 1;
  private static final int TURNO_ENEMIGO = 2;

  private static final int PARTIDA_DE_PRUEBA = 1;

  private static final int MAX_CARTAS_REPETIDAS = 3;

  private Partida partida = new Partida(100, 100, 1);

  private int contadorCartasRepetidas = 0;

  public Integer jugarCarta(Integer identificadorCarta, Integer identificadorPartida) {
    validarTurno(identificadorPartida);
    validarCartaEnMano(identificadorCarta, identificadorPartida);

    Integer danioCartaHaciaEnemigo = calcularEfectoCarta();

    Partida partida = obtenerPartidaPorIdentificador(identificadorPartida);
    partida.setHpEnemigo(partida.getHpEnemigo() - danioCartaHaciaEnemigo);
    actualizarEstadoPartida(partida);

    return danioCartaHaciaEnemigo;
  }

  private void validarTurno(Integer identificadorPartida) {
    Partida partida = obtenerPartidaPorIdentificador(identificadorPartida);

    if (partida.getTurno() == null) {
      throw new RuntimeException("Error, turno no definido.");
    }

    if (partida.getTurno() != TURNO_JUGADOR) {
      throw new RuntimeException("Turno del enemigo.");
    }
  }

  private void validarCartaEnMano(Integer identificadorCarta, Integer identificadorPartida) {
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
    //      Formula -> danio = valor_base_carta * multiplicador + factor_suerte

    //      Ejemplo:
    int valorBaseCarta = 10;
    int multiplicador = 5;
    int factorSuerte = 5;

    return valorBaseCarta * multiplicador + factorSuerte;
  }

  public Partida obtenerPartidaPorIdentificador(Integer identificadorPartida) {
    //      Aca va la logica del repositorio...

    if (identificadorPartida == PARTIDA_DE_PRUEBA) {
      ArrayList<Integer> cartas = new ArrayList<>();

      cartas.add(1);
      cartas.add(2);
      cartas.add(3);

      this.partida.setCartasEnManoJugador(cartas);

      return this.partida;
    }

    return new Partida(100, 100, TURNO_JUGADOR);
  }

  private void actualizarEstadoPartida(Partida partida) {
    if (partida.getTurno() == TURNO_JUGADOR) {
      partida.setTurno(TURNO_ENEMIGO);
    }

    if (partida.getHpEnemigo() <= 0) {
      partida.setEstado("Ganador jugador.");
    } else if (partida.getHpJugador() <= 0) {
      partida.setEstado("Ganador enemigo.");
    }
  }
}
