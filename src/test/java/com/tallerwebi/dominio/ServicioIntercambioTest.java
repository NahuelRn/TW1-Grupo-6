package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

public class ServicioIntercambioTest {

  private static final String RAREZA_COMUN = "Comun";
  private static final String RAREZA_POCO_COMUN = "Poco Comun";
  private static final String RAREZA_RARA = "Rara";
  private static final String RAREZA_EXOTICA = "Exotica";
  private static final String RAREZA_LEGENDARIA = "Legendaria";

  private RepositorioCarta repoCartaMock;
  private RepositorioInventario repoItemMock;
  private ServicioIntercambio servicio;

  @BeforeEach
  public void setUp() {
    repoCartaMock = mock(RepositorioCarta.class);
    repoItemMock = mock(RepositorioInventario.class);
    servicio = new ServicioIntercambioImpl(repoCartaMock, repoItemMock);

    // Mock por defecto para descontarCartasEntregadas — item con cantidad suficiente
    ItemInventario itemMock = new ItemInventario();
    itemMock.setCantidad(5);
    when(repoItemMock.buscarItemDeJugador(anyLong(), anyLong())).thenReturn(itemMock);
  }

  //  Validación de cantidad
  @Test
  public void siSeEntreganMenosDe4CartasDebeLanzarError() {
    List<Long> ids = List.of(1L, 2L, 3L);

    Exception ex = assertThrows(Exception.class, () -> servicio.realizarMejora(1L, ids));
    assertThat(ex.getMessage(), containsString("exactamente 4 cartas"));
  }

  @Test
  public void siSeEntreganMasDe4CartasDebeLanzarError() {
    List<Long> ids = List.of(1L, 2L, 3L, 4L, 5L);

    Exception ex = assertThrows(Exception.class, () -> servicio.realizarMejora(1L, ids));
    assertThat(ex.getMessage(), containsString("exactamente 4 cartas"));
  }

  @Test
  public void siUnaCartaNoExisteEnElRepositorioDebeLanzarError() {
    Carta comun = cartaConRareza(RAREZA_COMUN);
    when(repoCartaMock.buscarPorId(1L)).thenReturn(comun);
    when(repoCartaMock.buscarPorId(2L)).thenReturn(comun);
    when(repoCartaMock.buscarPorId(3L)).thenReturn(comun);
    when(repoCartaMock.buscarPorId(4L)).thenReturn(null);

    assertThrows(Exception.class, () -> servicio.realizarMejora(1L, List.of(1L, 2L, 3L, 4L)));
  }

  //  Validación de rareza uniforme
  @Test
  public void siLasCartasSonDeDistintaRarezaDebeLanzarError() {
    Carta comun = cartaConRareza(RAREZA_COMUN);
    Carta pocoCom = cartaConRareza(RAREZA_POCO_COMUN);

    when(repoCartaMock.buscarPorId(1L)).thenReturn(comun);
    when(repoCartaMock.buscarPorId(2L)).thenReturn(comun);
    when(repoCartaMock.buscarPorId(3L)).thenReturn(comun);
    when(repoCartaMock.buscarPorId(4L)).thenReturn(pocoCom);

    Exception ex = assertThrows(
      Exception.class,
      () -> servicio.realizarMejora(1L, List.of(1L, 2L, 3L, 4L))
    );
    assertThat(ex.getMessage(), containsString("misma rareza"));
  }

  @Test
  public void siLasCartasSonLegendariasDebeLanzarError() {
    Carta legendaria = cartaConRareza(RAREZA_LEGENDARIA);
    when(repoCartaMock.buscarPorId(anyLong())).thenReturn(legendaria);

    Exception ex = assertThrows(
      Exception.class,
      () -> servicio.realizarMejora(1L, List.of(1L, 2L, 3L, 4L))
    );
    assertThat(ex.getMessage(), containsString("No se puede mejorar una carta Legendaria"));
  }

  //  Casos felices
  @Test
  public void siSeEntreganCuatroCartasComunesDebeRetornarUnaPocoComon() throws Exception {
    Carta comun = cartaConRareza(RAREZA_COMUN);
    Carta pocoCom = cartaConRareza(RAREZA_POCO_COMUN);
    pocoCom.setNombre("Carta Poco Comun Premio");

    when(repoCartaMock.buscarPorId(anyLong())).thenReturn(comun);
    when(repoCartaMock.buscarPorRareza(RAREZA_POCO_COMUN)).thenReturn(List.of(pocoCom));

    Carta resultado = servicio.realizarMejora(1L, List.of(1L, 2L, 3L, 4L));

    assertThat(resultado, notNullValue());
    assertThat(resultado.getRareza(), is(RAREZA_POCO_COMUN));
  }

  @Test
  public void siSeEntreganCuatroCartasPocoComunesDebeRetornarUnaRara() throws Exception {
    Carta pocoCom = cartaConRareza(RAREZA_POCO_COMUN);
    Carta rara = cartaConRareza(RAREZA_RARA);
    rara.setNombre("Carta Rara Premio");

    when(repoCartaMock.buscarPorId(anyLong())).thenReturn(pocoCom);
    when(repoCartaMock.buscarPorRareza(RAREZA_RARA)).thenReturn(List.of(rara));

    Carta resultado = servicio.realizarMejora(1L, List.of(1L, 2L, 3L, 4L));

    assertThat(resultado, notNullValue());
    assertThat(resultado.getRareza(), is(RAREZA_RARA));
  }

  @Test
  public void siSeEntreganCuatroCartasRarasDebeRetornarUnaExotica() throws Exception {
    Carta rara = cartaConRareza(RAREZA_RARA);
    Carta exotica = cartaConRareza(RAREZA_EXOTICA);
    exotica.setNombre("Carta Exotica Premio");

    when(repoCartaMock.buscarPorId(anyLong())).thenReturn(rara);
    when(repoCartaMock.buscarPorRareza(RAREZA_EXOTICA)).thenReturn(List.of(exotica));

    Carta resultado = servicio.realizarMejora(1L, List.of(1L, 2L, 3L, 4L));

    assertThat(resultado, notNullValue());
    assertThat(resultado.getRareza(), is(RAREZA_EXOTICA));
  }

  @Test
  public void siSeEntreganCuatroCartasExoticasDebeRetornarUnaLegendaria() throws Exception {
    Carta rara = cartaConRareza(RAREZA_EXOTICA);
    Carta legendaria = cartaConRareza(RAREZA_LEGENDARIA);
    legendaria.setNombre("Carta Legendaria Premio");

    when(repoCartaMock.buscarPorId(anyLong())).thenReturn(rara);
    when(repoCartaMock.buscarPorRareza(RAREZA_LEGENDARIA)).thenReturn(List.of(legendaria));

    Carta resultado = servicio.realizarMejora(1L, List.of(1L, 2L, 3L, 4L));

    assertThat(resultado, notNullValue());
    assertThat(resultado.getRareza(), is(RAREZA_LEGENDARIA));
  }

  //  Sin premios disponibles
  @Test
  public void siNoHayCartasDelSiguienteNivelDebeLanzarError() {
    Carta comun = cartaConRareza(RAREZA_COMUN);

    when(repoCartaMock.buscarPorId(anyLong())).thenReturn(comun);
    when(repoCartaMock.buscarPorRareza(RAREZA_POCO_COMUN)).thenReturn(Collections.emptyList());

    Exception ex = assertThrows(
      Exception.class,
      () -> servicio.realizarMejora(1L, List.of(1L, 2L, 3L, 4L))
    );
    assertThat(ex.getMessage(), containsString("No hay cartas disponibles"));
  }

  //  obtenerInventario
  @Test
  public void obtenerInventarioDebeRetornarItemsDelJugador() {
    ItemInventario item = new ItemInventario();
    item.setCantidad(2);
    when(repoItemMock.listarInventarioDeJugador(1L)).thenReturn(List.of(item));

    List<ItemInventario> resultado = servicio.obtenerInventario(1L);

    assertThat(resultado, hasSize(1));
    verify(repoItemMock, times(1)).listarInventarioDeJugador(1L);
  }

  @Test
  public void obtenerInventarioDebeRetornarListaVaciaSiElJugadorNoTieneCartas() {
    when(repoItemMock.listarInventarioDeJugador(2L)).thenReturn(Collections.emptyList());

    List<ItemInventario> resultado = servicio.obtenerInventario(2L);

    assertThat(resultado, is(empty()));
  }

  @Test
  public void siElJugadorNoTieneLaCartaEnSuInventarioDebeLanzarError() {
    Carta comun = cartaConRareza(RAREZA_COMUN);
    Carta pocoCom = cartaConRareza(RAREZA_POCO_COMUN);

    when(repoCartaMock.buscarPorId(anyLong())).thenReturn(comun);
    when(repoCartaMock.buscarPorRareza(RAREZA_POCO_COMUN)).thenReturn(List.of(pocoCom));
    when(repoItemMock.buscarItemDeJugador(1L, 1L)).thenReturn(null);

    Exception ex = assertThrows(
      Exception.class,
      () -> servicio.realizarMejora(1L, List.of(1L, 1L, 1L, 1L))
    );
    assertThat(ex.getMessage(), containsString("No tienes suficientes copias"));
  }

  @Test
  public void siLaCantidadDeLaCartaEnElInventarioEsCeroDebeLanzarError() {
    Carta comun = cartaConRareza(RAREZA_COMUN);
    Carta pocoCom = cartaConRareza(RAREZA_POCO_COMUN);

    when(repoCartaMock.buscarPorId(anyLong())).thenReturn(comun);
    when(repoCartaMock.buscarPorRareza(RAREZA_POCO_COMUN)).thenReturn(List.of(pocoCom));

    ItemInventario itemCero = new ItemInventario();
    itemCero.setCantidad(0);
    when(repoItemMock.buscarItemDeJugador(1L, 1L)).thenReturn(itemCero);

    Exception ex = assertThrows(
      Exception.class,
      () -> servicio.realizarMejora(1L, List.of(1L, 1L, 1L, 1L))
    );
    assertThat(ex.getMessage(), containsString("No tienes suficientes copias"));
  }

  @Test
  public void siElPremioEsUnaCartaNuevaDebeGuardarUnNuevoItemEnElInventario() throws Exception {
    Carta comun = cartaConRareza(RAREZA_COMUN);
    Carta pocoCom = cartaConRareza(RAREZA_POCO_COMUN);
    pocoCom.setId(99L);

    when(repoCartaMock.buscarPorId(anyLong())).thenReturn(comun);
    when(repoCartaMock.buscarPorRareza(RAREZA_POCO_COMUN)).thenReturn(List.of(pocoCom));

    ItemInventario itemMateriales = new ItemInventario();
    itemMateriales.setCantidad(4);

    when(repoItemMock.buscarItemDeJugador(1L, 1L)).thenReturn(itemMateriales);
    when(repoItemMock.buscarItemDeJugador(1L, 99L)).thenReturn(null);

    Carta resultado = servicio.realizarMejora(1L, List.of(1L, 1L, 1L, 1L));

    assertThat(resultado, notNullValue());
    verify(repoItemMock, times(1)).guardar(ArgumentMatchers.any(ItemInventario.class));
  }

  @Test
  public void transformarEnOroDebeRetornarLaMitadDelValorBase() {
    Carta carta = new Carta();
    carta.setValorOroBase(100);
    when(repoCartaMock.buscarPorId(1L)).thenReturn(carta);

    Double resultado = servicio.transformarEnOro(1L);

    assertThat(resultado, is(50.0));
  }

  // Helper

  private Carta cartaConRareza(String rareza) {
    Carta carta = new Carta();
    carta.setRareza(rareza);
    return carta;
  }
}
