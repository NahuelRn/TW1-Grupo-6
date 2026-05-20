package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

public class CartaTest {

  @Test
  public void queSePuedanAsignarYObtenerTodosLosAtributosDeUnaCarta() {
    Carta carta = new Carta();

    // Ejecución
    carta.setId(1L);
    carta.setNombre("Espada Larga");
    carta.setTipo("Ataque");
    carta.setSubtipo("Sangrado");
    carta.setValorOroBase(150);
    carta.setNivelDesbloqueo(2);
    carta.setProbabilidad(80.5);
    carta.setDuracion(3);
    carta.setRareza("Poco Común");
    carta.setDescripcion("Inflige daño físico");

    // Validación (Then)
    assertThat(carta.getId(), equalTo(1L));
    assertThat(carta.getNombre(), equalTo("Espada Larga"));
    assertThat(carta.getTipo(), equalTo("Ataque"));
    assertThat(carta.getSubtipo(), equalTo("Sangrado"));
    assertThat(carta.getValorOroBase(), equalTo(150));
    assertThat(carta.getNivelDesbloqueo(), equalTo(2));
    assertThat(carta.getProbabilidad(), equalTo(80.5));
    assertThat(carta.getDuracion(), equalTo(3));
    assertThat(carta.getRareza(), equalTo("Poco Común"));
    assertThat(carta.getDescripcion(), equalTo("Inflige daño físico"));
  }
}
