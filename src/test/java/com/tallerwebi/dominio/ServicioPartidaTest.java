package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioPartidaTest {

  private ServicioPartida servicioPartida;
  private RepositorioEnemigo repositorioEnemigoMock;
  private Usuario usuarioMock;
  private Mazo mazoActivo;

  @BeforeEach
  public void init() {
    repositorioEnemigoMock = mock(RepositorioEnemigo.class);
    servicioPartida = new ServicioPartidaImpl(repositorioEnemigoMock);
    usuarioMock = new Usuario();
    mazoActivo = new Mazo();
    List<Carta> cartasMazo = new ArrayList<>();
    for (int i = 0; i < 15; i++) {
      cartasMazo.add(new Carta());
    }
    mazoActivo.setCartas(cartasMazo);
    usuarioMock.setMazoActivo(mazoActivo);
  }

  @Test
  public void alIniciarPartidaElJugadorDebeTenerExactamente5CartasEnLaMano() {
    // Preparación
    Enemigo enemigoGenerico = new Enemigo();
    enemigoGenerico.setHpBase(100);

    when(repositorioEnemigoMock.obtenerEnemigoAleatorioPorZona(anyString()))
      .thenReturn(enemigoGenerico);

    // Ejecución
    Partida partida = servicioPartida.iniciarPartida(usuarioMock, "Bosque Oscuro");

    // Validación
    assertThat(partida, is(notNullValue()));
    assertThat(partida.getManoJugador(), hasSize(5));
  }
}
