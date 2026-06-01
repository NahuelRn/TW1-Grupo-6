package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioIntercambioTest {

  private RepositorioCarta repoCartaMock;
  private RepositorioInventario repoItemMock;
  private ServicioIntercambio servicio;

  @BeforeEach
  public void setUp() {
    repoCartaMock = mock(RepositorioCarta.class);
    repoItemMock = mock(RepositorioInventario.class);
    servicio = new ServicioIntercambioImpl(repoCartaMock, repoItemMock);
  }

  // ─── Validación de cantidad ────────────────────────────────────────────────

  @Test
  public void siSeEntreganMenosDe4CartasDebeLanzarError() {
    List<Long> ids = List.of(1L, 2L, 3L);

    Exception ex = assertThrows(Exception.class, () -> servicio.realizarMejora(ids));
    assertThat(ex.getMessage(), containsString("exactamente 4 cartas"));
  }

  @Test
  public void siSeEntreganMasDe4CartasDebeLanzarError() {
    List<Long> ids = List.of(1L, 2L, 3L, 4L, 5L);

    Exception ex = assertThrows(Exception.class, () -> servicio.realizarMejora(ids));
    assertThat(ex.getMessage(), containsString("exactamente 4 cartas"));
  }

  // ─── Validación de rareza uniforme ────────────────────────────────────────

  @Test
  public void siLasCartasSonDeDistintaRarezaDebeLanzarError() {
    Carta comun = cartaConRareza("COMUN");
    Carta especial = cartaConRareza("ESPECIAL");

    when(repoCartaMock.buscarPorId(1L)).thenReturn(comun);
    when(repoCartaMock.buscarPorId(2L)).thenReturn(comun);
    when(repoCartaMock.buscarPorId(3L)).thenReturn(comun);
    when(repoCartaMock.buscarPorId(4L)).thenReturn(especial); // la distinta

    Exception ex = assertThrows(
      Exception.class,
      () -> servicio.realizarMejora(List.of(1L, 2L, 3L, 4L))
    );
    assertThat(ex.getMessage(), containsString("misma rareza"));
  }

  // ─── Validación de carta legendaria ──────────────────────────────────────

  @Test
  public void siLasCartasSonLegendariasDebeLanzarError() {
    Carta legendaria = cartaConRareza("LEGENDARIA");

    when(repoCartaMock.buscarPorId(anyLong())).thenReturn(legendaria);

    Exception ex = assertThrows(
      Exception.class,
      () -> servicio.realizarMejora(List.of(1L, 2L, 3L, 4L))
    );
    assertThat(ex.getMessage(), containsString("No se puede mejorar una carta Legendaria"));
  }

  // ─── Caso feliz: mejora correcta ─────────────────────────────────────────

  @Test
  public void siSeEntreganCuatroCartasComunesDebeRetornarUnaEspecial() throws Exception {
    Carta comun = cartaConRareza("COMUN");
    Carta especial = cartaConRareza("ESPECIAL");
    especial.setNombre("Carta Especial Premio");

    when(repoCartaMock.buscarPorId(anyLong())).thenReturn(comun);
    when(repoCartaMock.buscarPorRareza("ESPECIAL")).thenReturn(List.of(especial));

    Carta resultado = servicio.realizarMejora(List.of(1L, 2L, 3L, 4L));

    assertThat(resultado, notNullValue());
    assertThat(resultado.getRareza(), is("ESPECIAL"));
  }

  @Test
  public void siSeEntreganCuatroCartasEspecialesDebeRetornarUnaEpica() throws Exception {
    Carta especial = cartaConRareza("ESPECIAL");
    Carta epica = cartaConRareza("EPICA");
    epica.setNombre("Carta Épica Premio");

    when(repoCartaMock.buscarPorId(anyLong())).thenReturn(especial);
    when(repoCartaMock.buscarPorRareza("EPICA")).thenReturn(List.of(epica));

    Carta resultado = servicio.realizarMejora(List.of(1L, 2L, 3L, 4L));

    assertThat(resultado, notNullValue());
    assertThat(resultado.getRareza(), is("EPICA"));
  }

  @Test
  public void siSeEntreganCuatroCartasEpicasDebeRetornarUnaLegendaria() throws Exception {
    Carta epica = cartaConRareza("EPICA");
    Carta legendaria = cartaConRareza("LEGENDARIA");
    legendaria.setNombre("Carta Legendaria Premio");

    when(repoCartaMock.buscarPorId(anyLong())).thenReturn(epica);
    when(repoCartaMock.buscarPorRareza("LEGENDARIA")).thenReturn(List.of(legendaria));

    Carta resultado = servicio.realizarMejora(List.of(1L, 2L, 3L, 4L));

    assertThat(resultado, notNullValue());
    assertThat(resultado.getRareza(), is("LEGENDARIA"));
  }

  // ─── Sin premios disponibles ──────────────────────────────────────────────

  @Test
  public void siNoHayCartasDelSiguienteNivelDebeLanzarError() {
    Carta comun = cartaConRareza("COMUN");

    when(repoCartaMock.buscarPorId(anyLong())).thenReturn(comun);
    when(repoCartaMock.buscarPorRareza("ESPECIAL")).thenReturn(Collections.emptyList());

    Exception ex = assertThrows(
      Exception.class,
      () -> servicio.realizarMejora(List.of(1L, 2L, 3L, 4L))
    );
    assertThat(ex.getMessage(), containsString("No hay cartas disponibles"));
  }

  // ─── obtenerInventario ────────────────────────────────────────────────────

  @Test
  public void obtenerInventarioDebeRetornarItemsDelJugador() {
    ItemInventario item = new ItemInventario();
    when(repoItemMock.listarInventarioDeJugador(1L)).thenReturn(List.of(item));

    List<ItemInventario> resultado = servicio.obtenerInventario(1L);

    assertThat(resultado, hasSize(1));
    verify(repoItemMock, times(1)).listarInventarioDeJugador(1L);
  }

  // ─── Helper ───────────────────────────────────────────────────────────────

  private Carta cartaConRareza(String rareza) {
    Carta carta = new Carta();
    carta.setRareza(rareza);
    return carta;
  }
}
