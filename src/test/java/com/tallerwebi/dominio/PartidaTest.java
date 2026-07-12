package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;

public class PartidaTest {

  @Test
  public void queUnaPartidaNuevaNazcaSinBuffsActivos() {
    Partida partida = new Partida();

    assertThat(partida.getBuffDanioProximoAtaque(), is(0));
    assertThat(partida.getMultiplicadorDanioProximoAtaque(), is(1));
  }

  @Test
  public void quePuedaAsignarseUnBuffDeDanioAlProximoAtaque() {
    Partida partida = new Partida();

    partida.setBuffDanioProximoAtaque(10);

    assertThat(partida.getBuffDanioProximoAtaque(), is(10));
  }

  @Test
  public void quePuedaAsignarseUnMultiplicadorDeDanioAlProximoAtaque() {
    Partida partida = new Partida();

    partida.setMultiplicadorDanioProximoAtaque(2);

    assertThat(partida.getMultiplicadorDanioProximoAtaque(), is(2));
  }
}
