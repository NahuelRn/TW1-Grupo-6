package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class ServicioCombateTest {

  RepositorioPartida repositorioPartida = mock(RepositorioPartida.class);
  ServicioCombate servicioCombate = new ServicioCombateImpl(repositorioPartida);

  @Test
  public void deberiaCalcularCorrectamenteElDanioDeLaCarta() {
    Partida partida = new Partida(100, 100, 1);
    when(this.repositorioPartida.buscarPartidaPorIdentificador(1L)).thenReturn(partida);

    ArrayList<Integer> cartasEnMano = new ArrayList<>();
    cartasEnMano.add(1);
    partida.setCartasEnManoJugador(cartasEnMano);

    Integer danioEfecto = this.servicioCombate.jugarCarta(1, 1L);
    assertEquals(55, danioEfecto);
  }

  @Test
  public void deberiaCambiarTurnoLuegoDeJugarCarta() {
    Partida partida = new Partida(100, 100, 1);
    when(this.repositorioPartida.buscarPartidaPorIdentificador(1L)).thenReturn(partida);

    ArrayList<Integer> cartasEnMano = new ArrayList<>();
    cartasEnMano.add(1);
    partida.setCartasEnManoJugador(cartasEnMano);

    this.servicioCombate.jugarCarta(1, 1L);
    assertEquals(2, partida.getTurno());
  }

  @Test
  public void deberiaLanzarErrorCuandoNoTieneCartasEnMano() {
    Partida partida = new Partida(100, 100, 1);
    when(this.repositorioPartida.buscarPartidaPorIdentificador(1L)).thenReturn(partida);

    partida.setCartasEnManoJugador(new ArrayList<>());

    assertThrows(RuntimeException.class, () -> this.servicioCombate.jugarCarta(1, 1L));
  }

  @Test
  public void deberiaColocarEstadoGanadorJugadorCuandoHpEnemigoEsMenorOIgualACero() {
    Partida partida = new Partida(100, 100, 1);
    when(this.servicioCombate.obtenerPartidaPorIdentificador(1L)).thenReturn(partida);

    ArrayList<Integer> cartasEnMano = new ArrayList<>();
    cartasEnMano.add(1);
    partida.setCartasEnManoJugador(cartasEnMano);

    partida.setHpEnemigo(50);

    this.servicioCombate.jugarCarta(1, 1L);
    assertEquals(EnumEstadoPartida.GANADOR_JUGADOR, partida.getEnumEstadoPartida());
  }
}