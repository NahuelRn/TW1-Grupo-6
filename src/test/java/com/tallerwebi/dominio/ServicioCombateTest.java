package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class ServicioCombateTest {

  ServicioCombate servicioCombate = new ServicioCombate();

  @Test
  public void deberiaJugarCartaCorrectamente() {
    Integer danio = this.servicioCombate.jugarCarta(1, 1);

    assertEquals(55, danio);
  }

  @Test
  public void deberiaCambiarTurnoLuegoDeJugarCarta() {
    servicioCombate.jugarCarta(1, 1);

    Partida partida = this.servicioCombate.obtenerPartidaPorIdentificador(1);

    assertEquals(2, partida.getTurno());
  }

  @Test
  public void deberiaLanzarErrorCuandoNoTieneCartasEnMano() {
    Partida partida = this.servicioCombate.obtenerPartidaPorIdentificador(1);
    partida.setCartasEnManoJugador(new ArrayList<>());

    assertThrows(RuntimeException.class, () -> servicioCombate.jugarCarta(1, 2));
  }

  @Test
  public void deberiaColocarEstadoGanadorJugadorCuandoHpEnemigoEsMenorOIgualACero() {
    Partida partida = this.servicioCombate.obtenerPartidaPorIdentificador(1);
    partida.setHpEnemigo(50);

    servicioCombate.jugarCarta(1, 1);

    assertEquals("Ganador jugador.", partida.getEstado());
  }
}
