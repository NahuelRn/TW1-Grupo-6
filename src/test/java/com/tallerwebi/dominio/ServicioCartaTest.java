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
    repositorioInventario = mock(RepositorioInventario.class);

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

  @Test
  public void obtenerColeccionAgrupadaDebeRetornarTodasLasCartasYLasCantidadesCorrectas() {
    Long jugadorId = 1L;

    Carta carta1 = new Carta();
    carta1.setId(10L);
    Carta carta2 = new Carta();
    carta2.setId(20L);
    when(repositorioCarta.listarTodas()).thenReturn(List.of(carta1, carta2));

    ItemInventario item1 = new ItemInventario();
    item1.setCarta(carta1);
    item1.setCantidad(2);

    ItemInventario item2 = new ItemInventario();
    item2.setCarta(carta1);
    item2.setCantidad(3);

    when(repositorioInventario.listarInventarioDeJugador(jugadorId))
      .thenReturn(List.of(item1, item2));

    // Ejecución
    ColeccionDto resultado = servicioCarta.obtenerColeccionAgrupada(jugadorId);

    // Validaciones
    assertThat(resultado, notNullValue());
    assertThat(resultado.getCartasUnicas(), hasSize(2));
    assertThat(resultado.getCantidades().get(10L), equalTo(5)); // 2 + 3 = 5
  }

  @Test
  public void obtenerColeccionAgrupadaDebeFuncionarCorrectamenteSiElInventarioEsNulo() {
    Long jugadorId = 1L;

    Carta carta1 = new Carta();
    carta1.setId(10L);
    when(repositorioCarta.listarTodas()).thenReturn(List.of(carta1));
    when(repositorioInventario.listarInventarioDeJugador(jugadorId)).thenReturn(null);

    // Ejecución
    ColeccionDto resultado = servicioCarta.obtenerColeccionAgrupada(jugadorId);

    // Validaciones
    assertThat(resultado, notNullValue());
    assertThat(resultado.getCartasUnicas(), hasSize(1));
    assertThat(resultado.getCantidades().isEmpty(), is(true));
  }
}
