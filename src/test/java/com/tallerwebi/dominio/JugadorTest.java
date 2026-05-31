package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;

public class JugadorTest {

  @Test
  public void queUnJugadorNazcaConValoresPorDefecto() {
    // Ejecución
    Jugador jugador = new Jugador();

    // Validación: Comprobamos que asigne nivel 1 y 0 de oro al crearse
    assertThat(jugador.getNivel(), is(1));
    assertThat(jugador.getOro(), is(0));
  }

  @Test
  public void queSePuedanModificarLosAtributosDelJugador() {
    // Preparación
    Jugador jugador = new Jugador();

    // Ejecución
    jugador.setId(10L);
    jugador.setNivel(5);
    jugador.setOro(500);

    // Validación (Para dejar a JaCoCo al 100% en esta clase)
    assertThat(jugador.getId(), is(10L));
    assertThat(jugador.getNivel(), is(5));
    assertThat(jugador.getOro(), is(500));
  }

  @Test
  public void queSePuedaVincularUnUsuarioAlJugadorDeFormaBidireccional() {
    // Preparación
    Usuario usuario = new Usuario();
    usuario.setEmail("nahuel@unlam.edu.ar");

    Jugador jugador = new Jugador();

    // Ejecución: Armamos la relación de los dos lados
    jugador.setUsuario(usuario);
    usuario.setJugador(jugador);

    // Validación
    assertThat(jugador.getUsuario(), is(notNullValue()));
    assertThat(jugador.getUsuario().getEmail(), is("nahuel@unlam.edu.ar"));
    assertThat(usuario.getJugador(), is(jugador));
  }
}
