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
  private RepositorioMercado repoMercadoMock;
  private ServicioIntercambio servicio;

  @BeforeEach
  public void setUp() {
    repoCartaMock = mock(RepositorioCarta.class);
    repoItemMock = mock(RepositorioInventario.class);
    repoMercadoMock = mock(RepositorioMercado.class);

    // Constructor ahora recibe 3 parámetros
    servicio = new ServicioIntercambioImpl(repoCartaMock, repoItemMock, repoMercadoMock);

    // Mock genérico: por defecto el jugador tiene 5 copias de cualquier carta
    ItemInventario itemMock = new ItemInventario();
    itemMock.setCantidad(5);
    when(repoItemMock.buscarItemDeJugador(anyLong(), anyLong())).thenReturn(itemMock);

    // Mock genérico: buscarUsuarioPorJugadorId devuelve un usuario con jugador
    Jugador jugadorMock = new Jugador();
    jugadorMock.setId(1001L);
    Usuario usuarioMock = new Usuario();
    usuarioMock.setJugador(jugadorMock);
    when(repoMercadoMock.buscarUsuarioPorJugadorId(anyLong())).thenReturn(usuarioMock);
  }

  // -----------------------------------------------------------------------
  // Validaciones de cantidad
  // -----------------------------------------------------------------------

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
  public void siLaListaDeIdsEnviadaAIntercambiarEsNulaDebeLanzarExcepcion() {
    Exception ex = assertThrows(Exception.class, () -> servicio.realizarMejora(1L, null));
    assertThat(ex.getMessage(), containsString("Debes entregar exactamente 4 cartas"));
  }

  // -----------------------------------------------------------------------
  // Validaciones de cartas
  // -----------------------------------------------------------------------

  @Test
  public void siUnaCartaNoExisteEnElRepositorioDebeLanzarError() {
    Carta comun = cartaConRareza(RAREZA_COMUN);
    when(repoCartaMock.buscarPorId(1L)).thenReturn(comun);
    when(repoCartaMock.buscarPorId(2L)).thenReturn(comun);
    when(repoCartaMock.buscarPorId(3L)).thenReturn(comun);
    when(repoCartaMock.buscarPorId(4L)).thenReturn(null);

    assertThrows(Exception.class, () -> servicio.realizarMejora(1L, List.of(1L, 2L, 3L, 4L)));
  }

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
  public void siUnaCartaSecundariaTieneRarezaNulaDebeLanzarExcepcionEnLaValidacionUniforme() {
    Carta primera = cartaConRareza(RAREZA_COMUN);
    Carta segundaSinRareza = new Carta();
    when(repoCartaMock.buscarPorId(1L)).thenReturn(primera);
    when(repoCartaMock.buscarPorId(2L)).thenReturn(segundaSinRareza);

    Exception ex = assertThrows(
      Exception.class,
      () -> servicio.realizarMejora(1L, List.of(1L, 2L, 1L, 1L))
    );
    assertThat(ex.getMessage(), containsString("Todas las cartas deben ser de la misma rareza"));
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

  @Test
  public void siSeIntentaObtenerSiguienteRarezaDeUnParametroNuloDebeLanzarExcepcion() {
    Carta cartaSinRareza = new Carta();
    cartaSinRareza.setRareza(null);
    when(repoCartaMock.buscarPorId(anyLong())).thenReturn(cartaSinRareza);

    Exception ex = assertThrows(
      Exception.class,
      () -> servicio.realizarMejora(1L, List.of(1L, 2L, 3L, 4L))
    );
    assertThat(ex.getMessage(), containsString("La rareza de las cartas no puede ser nula"));
  }

  // -----------------------------------------------------------------------
  // Flujos exitosos de mejora
  // -----------------------------------------------------------------------

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
    Carta exotica = cartaConRareza(RAREZA_EXOTICA);
    Carta legendaria = cartaConRareza(RAREZA_LEGENDARIA);
    legendaria.setNombre("Carta Legendaria Premio");

    when(repoCartaMock.buscarPorId(anyLong())).thenReturn(exotica);
    when(repoCartaMock.buscarPorRareza(RAREZA_LEGENDARIA)).thenReturn(List.of(legendaria));

    Carta resultado = servicio.realizarMejora(1L, List.of(1L, 2L, 3L, 4L));
    assertThat(resultado, notNullValue());
    assertThat(resultado.getRareza(), is(RAREZA_LEGENDARIA));
  }

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

  // -----------------------------------------------------------------------
  // Validaciones de inventario
  // -----------------------------------------------------------------------

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

  /**
   * Cuando el premio es una carta nueva (el jugador no la tiene),
   * debe llamarse guardar() con el nuevo ItemInventario.
   * Se mockea buscarUsuarioPorJugadorId para que el servicio pueda
   * asignar el Jugador al nuevo item.
   */
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

    // El jugador NO tiene aún la carta premio (id=99) → debe crearse un item nuevo
    when(repoItemMock.buscarItemDeJugador(1L, 99L)).thenReturn(null);

    // El mock de buscarUsuarioPorJugadorId ya está configurado en setUp()

    Carta resultado = servicio.realizarMejora(1L, List.of(1L, 1L, 1L, 1L));

    assertThat(resultado, notNullValue());
    verify(repoItemMock, times(1)).guardar(ArgumentMatchers.any(ItemInventario.class));
  }

  // -----------------------------------------------------------------------
  // obtenerInventario y obtenerInventarioFiltrado
  // -----------------------------------------------------------------------

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
  public void siElRepositorioDeInventarioRetornaNuloAlListarDebeRetornarListaVacia() {
    when(repoItemMock.listarInventarioDeJugador(99L)).thenReturn(null);

    List<ItemInventario> resultado = servicio.obtenerInventario(99L);
    assertThat(resultado, is(empty()));
  }

  @Test
  public void obtenerInventarioFiltradoConFiltroAllRetornaTodoElInventario() {
    ItemInventario item = new ItemInventario();
    item.setCantidad(2);
    item.setCarta(cartaConRareza(RAREZA_COMUN));
    when(repoItemMock.listarInventarioDeJugador(1L)).thenReturn(List.of(item));

    List<ItemInventario> resultado = servicio.obtenerInventarioFiltrado(1L, "all");
    assertThat(resultado, hasSize(1));
  }

  @Test
  public void obtenerInventarioFiltradoConRarezaEspecificaFiltraCorrectamente() {
    ItemInventario item1 = new ItemInventario();
    item1.setCantidad(1);
    item1.setCarta(cartaConRareza(RAREZA_COMUN));

    ItemInventario item2 = new ItemInventario();
    item2.setCantidad(3);
    item2.setCarta(cartaConRareza(RAREZA_RARA));

    when(repoItemMock.listarInventarioDeJugador(1L)).thenReturn(List.of(item1, item2));

    List<ItemInventario> resultado = servicio.obtenerInventarioFiltrado(1L, "Rara");
    assertThat(resultado, hasSize(1));
    assertThat(resultado.get(0).getCarta().getRareza(), is(RAREZA_RARA));
  }

  @Test
  public void obtenerInventarioFiltradoRetornaListaVaciaSiNoSeEncuentraLaRareza() {
    ItemInventario item = new ItemInventario();
    item.setCantidad(2);
    item.setCarta(cartaConRareza(RAREZA_COMUN));
    when(repoItemMock.listarInventarioDeJugador(1L)).thenReturn(List.of(item));

    List<ItemInventario> resultado = servicio.obtenerInventarioFiltrado(1L, "Legendaria");
    assertThat(resultado, is(empty()));
  }

  @Test
  public void obtenerInventarioFiltradoConRarezaNulaRetornaTodoElInventario() {
    ItemInventario item = new ItemInventario();
    item.setCantidad(2);
    item.setCarta(cartaConRareza(RAREZA_COMUN));
    when(repoItemMock.listarInventarioDeJugador(1L)).thenReturn(List.of(item));

    List<ItemInventario> resultado = servicio.obtenerInventarioFiltrado(1L, null);
    assertThat(resultado, hasSize(1));
  }

  @Test
  public void obtenerInventarioFiltradoConRarezaVaciaRetornaTodoElInventario() {
    ItemInventario item = new ItemInventario();
    item.setCantidad(2);
    item.setCarta(cartaConRareza(RAREZA_COMUN));
    when(repoItemMock.listarInventarioDeJugador(1L)).thenReturn(List.of(item));

    List<ItemInventario> resultado = servicio.obtenerInventarioFiltrado(1L, "   ");
    assertThat(resultado, hasSize(1));
  }

  // -----------------------------------------------------------------------
  // transformarEnOro
  // -----------------------------------------------------------------------

  @Test
  public void transformarEnOroDebeRetornarLaMitadDelValorBase() {
    Carta carta = new Carta();
    carta.setValorOroBase(100);
    when(repoCartaMock.buscarPorId(1L)).thenReturn(carta);

    Double resultado = servicio.transformarEnOro(1L);
    assertThat(resultado, is(50.0));
  }

  // -----------------------------------------------------------------------
  // Helper
  // -----------------------------------------------------------------------

  private Carta cartaConRareza(String rareza) {
    Carta carta = new Carta();
    carta.setRareza(rareza);
    return carta;
  }
}
