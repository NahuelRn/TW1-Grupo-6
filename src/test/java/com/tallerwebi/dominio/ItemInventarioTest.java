package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;

public class ItemInventarioTest {

  @Test
  public void queUnItemNuevoNazcaConCantidadUno() {
    // Ejecución
    ItemInventario item = new ItemInventario();

    // Validación
    assertThat(item.getCantidad(), is(1));
  }

  @Test
  public void queSePuedaSumarYRestarCantidadDeCartas() {
    // Preparación
    ItemInventario item = new ItemInventario(); // Arranca en 1

    // Ejecución
    item.sumarCantidad(3); // Debería pasar a 4
    item.restarCantidad(2); // Debería pasar a 2

    // Validación
    assertThat(item.getCantidad(), is(2));
  }

  @Test
  public void queSePuedanVincularJugadorYCarta() {
    // Preparación
    ItemInventario item = new ItemInventario();
    Jugador jugador = new Jugador();
    jugador.setId(10L);
    Carta carta = new Carta();
    carta.setId(5L);

    // Ejecución
    item.setJugador(jugador);
    item.setCarta(carta);
    item.setId(1L);

    // Validación
    assertThat(item.getId(), is(1L));
    assertThat(item.getJugador().getId(), is(10L));
    assertThat(item.getCarta().getId(), is(5L));
  }
}
