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
    // CORREGIDO: Mokeamos el repositorio, no el servicio real
    when(repositorioPartida.buscarPartidaPorIdentificador(1L)).thenReturn(partida);

    ArrayList<Integer> cartasEnMano = new ArrayList<>();
    cartasEnMano.add(1);
    partida.setCartasEnManoJugador(cartasEnMano);

    partida.setHpEnemigo(50); // 50 - 55 de daño de la carta <= 0

    servicioCombate.jugarCarta(1, 1L);
    assertEquals(EnumEstadoPartida.GANADOR_JUGADOR, partida.getEnumEstadoPartida());
  }

  // ─── NUEVOS TESTS PARA COMPLETAR EL 100% DE COBERTURA EN COMBATE ───

  @Test
  public void deberiaLanzarErrorCuandoElTurnoNoEstaDefinido() {
    Partida partida = new Partida(100, 100, 1);
    partida.setTurno(null); // Forzamos turno nulo
    when(repositorioPartida.buscarPartidaPorIdentificador(1L)).thenReturn(partida);

    assertThrows(RuntimeException.class, () -> servicioCombate.jugarCarta(1, 1L));
  }

  @Test
  public void deberiaLanzarErrorCuandoEsElTurnoDelEnemigo() {
    Partida partida = new Partida(100, 100, 2); // Turno 2 = Turno Enemigo
    when(repositorioPartida.buscarPartidaPorIdentificador(1L)).thenReturn(partida);

    assertThrows(RuntimeException.class, () -> servicioCombate.jugarCarta(1, 1L));
  }

  @Test
  public void deberiaLanzarErrorCuandoLasCartasEnManoSonNulas() {
    Partida partida = new Partida(100, 100, 1);
    partida.setCartasEnManoJugador(null); // Forzamos lista nula
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
    cartasEnMano.add(5); // Agregamos 4 veces la misma carta para romper el límite de 3
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

    partida.setHpJugador(0); // El jugador ya no tiene HP
    partida.setHpEnemigo(100); // El enemigo tiene suficiente vida para sobrevivir al golpe (100 - 55 = 45)

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
}
