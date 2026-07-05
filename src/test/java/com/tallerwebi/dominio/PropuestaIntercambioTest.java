package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class PropuestaIntercambioTest {

  @Test
  public void gettersYSettersFuncionanCorrectamente() {
    Usuario emisor = new Usuario();
    Usuario receptor = new Usuario();

    Carta cartaBuscada = new Carta();
    Carta cartaOfrecida = new Carta();

    LocalDateTime fecha = LocalDateTime.now();

    PropuestaIntercambio propuesta = new PropuestaIntercambio();

    propuesta.setId(10L);
    propuesta.setUsuarioEmisor(emisor);
    propuesta.setUsuarioReceptor(receptor);
    propuesta.setCartaBuscada(cartaBuscada);
    propuesta.setCartaOfrecida(cartaOfrecida);
    propuesta.setEstado("FINALIZADA");
    propuesta.setFechaCreacion(fecha);

    assertThat(propuesta.getId(), is(10L));
    assertThat(propuesta.getUsuarioEmisor(), is(emisor));
    assertThat(propuesta.getUsuarioReceptor(), is(receptor));
    assertThat(propuesta.getCartaBuscada(), is(cartaBuscada));
    assertThat(propuesta.getCartaOfrecida(), is(cartaOfrecida));
    assertThat(propuesta.getEstado(), is("FINALIZADA"));
    assertThat(propuesta.getFechaCreacion(), is(fecha));
  }
}
