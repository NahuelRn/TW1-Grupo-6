package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioCartaTest {

  private ServicioCarta servicioCarta;
  private RepositorioCarta repositorioCarta;
  private RepositorioInventario repositorioInventario;

  @BeforeEach
  public void init() {
    repositorioCarta = mock(RepositorioCarta.class);
    // 2. Inicializá el mock
    repositorioInventario = mock(RepositorioInventario.class);

    // 3. Pasale los DOS argumentos al constructor
    servicioCarta = new ServicioCartaImpl(repositorioCarta, repositorioInventario);
  }

  @Test
  public void queAlPedirTodasLasCartasSeLlameAlRepositorio() {
    List<Carta> cartasSimuladas = new ArrayList<>();
    cartasSimuladas.add(new Carta());
    when(repositorioCarta.listarTodas()).thenReturn(cartasSimuladas);

    List<Carta> resultado = servicioCarta.obtenerTodas();

    assertThat(resultado, hasSize(1));
    verify(repositorioCarta, times(1)).listarTodas();
  }

  @Test
  public void queAlPedirUnaCartaPorIdSeLlameAlRepositorio() {
    Carta cartaSimulada = new Carta();
    cartaSimulada.setId(1L);
    when(repositorioCarta.buscarPorId(1L)).thenReturn(cartaSimulada);

    Carta resultado = servicioCarta.obtenerCartaPorId(1L);

    assertThat(resultado, notNullValue());
    assertThat(resultado.getId(), is(1L));
    verify(repositorioCarta, times(1)).buscarPorId(1L);
  }

  @Test
  public void queAlPedirUnaCartaInexistentePorIdRetorneNull() {
    when(repositorioCarta.buscarPorId(99L)).thenReturn(null);

    Carta resultado = servicioCarta.obtenerCartaPorId(99L);

    assertThat(resultado, nullValue());
  }
}
