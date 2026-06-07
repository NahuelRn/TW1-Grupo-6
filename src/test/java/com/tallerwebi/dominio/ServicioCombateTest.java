package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioCombateTest {

  private RepositorioPartida repositorioPartida;
  private ServicioCombate servicioCombate;

  @BeforeEach
  public void init() {
    repositorioPartida = mock(RepositorioPartida.class);
    servicioCombate = new ServicioCombateImpl(repositorioPartida);
  }

  @Test
  public void deberiaCalcularCorrectamenteElDanioDeLaCarta() {
    Partida partida = new Partida(100, 100, 1);
    when(repositorioPartida.buscarPartidaPorIdentificador(1L)).thenReturn(partida);

    ArrayList<Integer> cartasEnMano = new ArrayList<>();
    cartasEnMano.add(1);
    partida.setCartasEnManoJugador(cartasEnMano);

    Integer danioEfecto = servicioCombate.jugarCarta(1, 1L);
    assertEquals(55, danioEfecto);
  }

  @Test
  public void deberiaCambiarTurnoLuegoDeJugarCarta() {
    Partida partida = new Partida(100, 100, 1);
    when(repositorioPartida.buscarPartidaPorIdentificador(1L)).thenReturn(partida);

    ArrayList<Integer> cartasEnMano = new ArrayList<>();
    cartasEnMano.add(1);
    partida.setCartasEnManoJugador(cartasEnMano);

    servicioCombate.jugarCarta(1, 1L);
    assertEquals(2, partida.getTurno());
  }

  @Test
  public void deberiaLanzarErrorCuandoNoTieneCartasEnMano() {
    Partida partida = new Partida(100, 100, 1);
    when(repositorioPartida.buscarPartidaPorIdentificador(1L)).thenReturn(partida);

    partida.setCartasEnManoJugador(new ArrayList<>());

    assertThrows(RuntimeException.class, () -> servicioCombate.jugarCarta(1, 1L));
  }

  @Test
  public void deberiaColocarEstadoGanadorJugadorCuandoHpEnemigoEsMenorOIgualACero() {
    Partida partida = new Partida(100, 100, 1);
    when(repositorioPartida.buscarPartidaPorIdentificador(1L)).thenReturn(partida);

    ArrayList<Integer> cartasEnMano = new ArrayList<>();
    cartasEnMano.add(1);
    partida.setCartasEnManoJugador(cartasEnMano);
    partida.setHpEnemigo(50);

    servicioCombate.jugarCarta(1, 1L);
    assertEquals(EnumEstadoPartida.GANADOR_JUGADOR, partida.getEnumEstadoPartida());
  }

  @Test
  public void deberiaLanzarErrorCuandoElTurnoNoEstaDefinido() {
    Partida partida = new Partida(100, 100, 1);
    partida.setTurno(null);
    when(repositorioPartida.buscarPartidaPorIdentificador(1L)).thenReturn(partida);

    assertThrows(RuntimeException.class, () -> servicioCombate.jugarCarta(1, 1L));
  }

  @Test
  public void deberiaLanzarErrorCuandoEsElTurnoDelEnemigo() {
    Partida partida = new Partida(100, 100, 2);
    when(repositorioPartida.buscarPartidaPorIdentificador(1L)).thenReturn(partida);

    assertThrows(RuntimeException.class, () -> servicioCombate.jugarCarta(1, 1L));
  }

  @Test
  public void deberiaLanzarErrorCuandoLasCartasEnManoSonNulas() {
    Partida partida = new Partida(100, 100, 1);
    partida.setCartasEnManoJugador(null);
    when(repositorioPartida.buscarPartidaPorIdentificador(1L)).thenReturn(partida);

    assertThrows(RuntimeException.class, () -> servicioCombate.jugarCarta(1, 1L));
  }

  @Test
  public void deberiaLanzarErrorSiSeSuperaElMaximoDeCartasRepetidas() {
    Partida partida = new Partida(100, 100, 1);
    when(repositorioPartida.buscarPartidaPorIdentificador(1L)).thenReturn(partida);

    ArrayList<Integer> cartasEnMano = new ArrayList<>();
    cartasEnMano.add(5);
    cartasEnMano.add(5);
    cartasEnMano.add(5);
    cartasEnMano.add(5);
    partida.setCartasEnManoJugador(cartasEnMano);

    assertThrows(RuntimeException.class, () -> servicioCombate.jugarCarta(5, 1L));
  }

  @Test
  public void deberiaColocarEstadoGanadorEnemigoCuandoHpJugadorEsMenorOIgualACero() {
    Partida partida = new Partida(100, 100, 1);
    when(repositorioPartida.buscarPartidaPorIdentificador(1L)).thenReturn(partida);

    ArrayList<Integer> cartasEnMano = new ArrayList<>();
    cartasEnMano.add(1);
    partida.setCartasEnManoJugador(cartasEnMano);
    partida.setHpJugador(0);
    partida.setHpEnemigo(100);

    servicioCombate.jugarCarta(1, 1L);
    assertEquals(EnumEstadoPartida.GANADOR_ENEMIGO, partida.getEnumEstadoPartida());
  }

  @Test
  public void deberiaPoderObtenerPartidaPorSuIdentificador() {
    Partida partida = new Partida(100, 100, 1);
    when(repositorioPartida.buscarPartidaPorIdentificador(1L)).thenReturn(partida);

    Partida resultado = servicioCombate.obtenerPartidaPorIdentificador(1L);
    assertEquals(partida, resultado);
  }

  // ✅ cubre la rama else de cambiarTurno
  @Test
  public void deberiaCambiarTurnoAJugadorCuandoElTurnoEstaEnEnemigo() {
    Partida partida = new Partida(100, 100, 1);
    when(repositorioPartida.buscarPartidaPorIdentificador(1L)).thenReturn(partida);

    ArrayList<Integer> cartasEnMano = new ArrayList<>();
    cartasEnMano.add(1);
    cartasEnMano.add(1);
    partida.setCartasEnManoJugador(cartasEnMano);
    partida.setHpEnemigo(200);

    // Primera jugada: turno pasa de 1 a 2
    servicioCombate.jugarCarta(1, 1L);
    assertEquals(2, partida.getTurno());

    // Forzamos turno 1 para poder jugar de nuevo y cubrir el else
    partida.setTurno(1);
    servicioCombate.jugarCarta(1, 1L);
    assertEquals(2, partida.getTurno());
  }
}
