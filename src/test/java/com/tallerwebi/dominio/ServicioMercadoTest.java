package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

public class ServicioMercadoTest {

  private RepositorioMercado repoMercadoMock;
  private RepositorioCarta repoCartaMock;
  private RepositorioInventario repoInventarioMock;
  private ServicioMercadoImpl servicio;

  private static final Long USER_ID = 1L;
  private static final Long CARTA_ID = 10L;
  private static final Long PROPUESTA_ID = 100L;

  @BeforeEach
  public void setUp() {
    repoMercadoMock = mock(RepositorioMercado.class);
    repoCartaMock = mock(RepositorioCarta.class);
    repoInventarioMock = mock(RepositorioInventario.class);

    servicio = new ServicioMercadoImpl(repoMercadoMock, repoCartaMock, repoInventarioMock);
  }

  /**
   * Crea un usuario con su jugador y configura el mock de inventario.
   * IMPORTANTE: mockea buscarUsuarioPorJugadorId (lo que usa el servicio en producción).
   */
  private Usuario crearUsuarioBase(Long id) {
    Usuario usuario = new Usuario();
    usuario.setId(id);

    Jugador jugador = new Jugador();
    jugador.setId(id + 1000L);
    usuario.setJugador(jugador);

    List<ItemInventario> listaInventario = new ArrayList<>();
    when(repoInventarioMock.listarInventarioDeJugador(jugador.getId())).thenReturn(listaInventario);

    // El servicio llama buscarUsuarioPorJugadorId(jugadorId) donde jugadorId = jugador.getId()
    when(repoMercadoMock.buscarUsuarioPorJugadorId(jugador.getId())).thenReturn(usuario);

    return usuario;
  }

  private List<ItemInventario> obtenerListaMockeada(Usuario usuario) {
    return repoInventarioMock.listarInventarioDeJugador(usuario.getJugador().getId());
  }

  /**
   * Helper: devuelve el jugadorId que hay que pasar al servicio para este usuario.
   * El servicio recibe el ID del Jugador (no del Usuario).
   */
  private Long jugadorIdDe(Usuario usuario) {
    return usuario.getJugador().getId();
  }

  // -----------------------------------------------------------------------
  // publicarSolicitud
  // -----------------------------------------------------------------------

  @Test
  public void publicarSolicitudExitoso() throws Exception {
    Usuario usuario = crearUsuarioBase(USER_ID);

    when(repoMercadoMock.listarMisTrades(usuario)).thenReturn(new ArrayList<>());
    when(repoCartaMock.buscarPorId(CARTA_ID)).thenReturn(new Carta());

    servicio.publicarSolicitud(jugadorIdDe(usuario), CARTA_ID);

    verify(repoMercadoMock, times(1)).guardar(ArgumentMatchers.any(PropuestaIntercambio.class));
  }

  @Test
  public void publicarSolicitudLanzaExcepcionSiUsuarioNoExiste() {
    // jugadorId que no existe en el mock → buscarUsuarioPorJugadorId devuelve null
    when(repoMercadoMock.buscarUsuarioPorJugadorId(999L)).thenReturn(null);

    Exception exception = assertThrows(
      Exception.class,
      () -> servicio.publicarSolicitud(999L, CARTA_ID)
    );

    assertThat(exception.getMessage(), is("El usuario no existe en el sistema"));
  }

  @Test
  public void publicarSolicitudLanzaExcepcionSiUsuarioYaTieneLaCarta() {
    Usuario usuario = crearUsuarioBase(USER_ID);

    ItemInventario item = new ItemInventario();
    Carta carta = new Carta();
    carta.setId(CARTA_ID);
    item.setCarta(carta);
    item.setCantidad(1);

    obtenerListaMockeada(usuario).add(item);

    assertThrows(Exception.class, () -> servicio.publicarSolicitud(jugadorIdDe(usuario), CARTA_ID));
  }

  @Test
  public void publicarSolicitudLanzaExcepcionSiYaTieneSolicitudActiva() {
    Usuario usuario = crearUsuarioBase(USER_ID);

    PropuestaIntercambio tradeActivo = new PropuestaIntercambio();
    tradeActivo.setEstado("ACTIVA");
    Carta carta = new Carta();
    carta.setId(CARTA_ID);
    tradeActivo.setCartaBuscada(carta);

    when(repoMercadoMock.listarMisTrades(usuario)).thenReturn(List.of(tradeActivo));

    Exception exception = assertThrows(
      Exception.class,
      () -> servicio.publicarSolicitud(jugadorIdDe(usuario), CARTA_ID)
    );

    assertThat(exception.getMessage(), is("Ya tienes una solicitud activa para esta carta"));
  }

  // -----------------------------------------------------------------------
  // obtenerOfertasCompatibles
  // -----------------------------------------------------------------------

  @Test
  public void obtenerOfertasCompatiblesDevuelveVacioSiUsuarioNoExiste() {
    when(repoMercadoMock.buscarUsuarioPorJugadorId(999L)).thenReturn(null);
    List<PropuestaIntercambio> result = servicio.obtenerOfertasCompatibles(999L);
    assertThat(result, is(empty()));
  }

  @Test
  public void obtenerOfertasCompatiblesFiltraLasPropias() {
    Usuario usuario = crearUsuarioBase(USER_ID);
    Usuario otroUsuario = crearUsuarioBase(2L);

    PropuestaIntercambio propia = new PropuestaIntercambio();
    propia.setUsuarioEmisor(usuario);

    PropuestaIntercambio ajena = new PropuestaIntercambio();
    ajena.setUsuarioEmisor(otroUsuario);

    when(repoMercadoMock.listarTodasLasActivas()).thenReturn(List.of(propia, ajena));

    List<PropuestaIntercambio> result = servicio.obtenerOfertasCompatibles(jugadorIdDe(usuario));

    assertThat(result, hasSize(1));
    assertThat(result.get(0), is(ajena));
  }

  // -----------------------------------------------------------------------
  // usuarioTieneCartaRepetida
  // -----------------------------------------------------------------------

  @Test
  public void usuarioTieneCartaRepetidaDevuelveFalsoSiUsuarioNoExiste() {
    when(repoMercadoMock.buscarUsuarioPorJugadorId(999L)).thenReturn(null);
    boolean result = servicio.usuarioTieneCartaRepetida(999L, CARTA_ID);
    assertThat(result, is(false));
  }

  @Test
  public void usuarioTieneCartaRepetidaDevuelveFalsoSiNoAlcanzaCantidadMinima() {
    Usuario usuario = crearUsuarioBase(USER_ID);
    ItemInventario item = new ItemInventario();
    Carta carta = new Carta();
    carta.setId(CARTA_ID);
    item.setCarta(carta);
    item.setCantidad(1);

    obtenerListaMockeada(usuario).add(item);

    boolean result = servicio.usuarioTieneCartaRepetida(jugadorIdDe(usuario), CARTA_ID);
    assertThat(result, is(false));
  }

  @Test
  public void usuarioTieneCartaRepetidaDevuelveVerdaderoSiSuperaLaCantidadMinima() {
    Usuario usuario = crearUsuarioBase(USER_ID);
    ItemInventario item = new ItemInventario();
    Carta carta = new Carta();
    carta.setId(CARTA_ID);
    item.setCarta(carta);
    item.setCantidad(2);

    obtenerListaMockeada(usuario).add(item);

    boolean result = servicio.usuarioTieneCartaRepetida(jugadorIdDe(usuario), CARTA_ID);
    assertThat(result, is(true));
  }

  // -----------------------------------------------------------------------
  // obtenerCartasFaltantes y obtenerMisTrades
  // -----------------------------------------------------------------------

  @Test
  public void obtenerCartasFaltantesYMisTradesDevuelvenVacioSiUsuarioNoExiste() {
    when(repoMercadoMock.buscarUsuarioPorJugadorId(999L)).thenReturn(null);
    assertThat(servicio.obtenerCartasFaltantes(999L), is(empty()));
    assertThat(servicio.obtenerMisTrades(999L), is(empty()));
  }

  @Test
  public void obtenerCartasFaltantesExitoso() {
    Usuario usuario = crearUsuarioBase(USER_ID);
    ItemInventario item = new ItemInventario();
    Carta carta = new Carta();
    carta.setId(CARTA_ID);
    item.setCarta(carta);
    item.setCantidad(1);

    obtenerListaMockeada(usuario).add(item);

    when(repoCartaMock.listarCartasFaltantes(anyList())).thenReturn(List.of(carta));

    List<Carta> result = servicio.obtenerCartasFaltantes(jugadorIdDe(usuario));
    assertThat(result, hasSize(1));
  }

  @Test
  public void obtenerMisTradesExitoso() {
    Usuario usuario = crearUsuarioBase(USER_ID);
    when(repoMercadoMock.listarMisTrades(usuario)).thenReturn(new ArrayList<>());

    List<PropuestaIntercambio> result = servicio.obtenerMisTrades(jugadorIdDe(usuario));
    assertThat(result, is(notNullValue()));
  }

  // -----------------------------------------------------------------------
  // buscarPorId
  // -----------------------------------------------------------------------

  @Test
  public void buscarPorIdExitoso() {
    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    when(repoMercadoMock.buscarPorId(PROPUESTA_ID)).thenReturn(propuesta);
    assertThat(servicio.buscarPorId(PROPUESTA_ID), is(propuesta));
  }

  // -----------------------------------------------------------------------
  // obtenerOpcionesRecompensa
  // -----------------------------------------------------------------------

  @Test
  public void obtenerOpcionesRecompensaFiltraSoloRepetidas() {
    Usuario emisor = crearUsuarioBase(USER_ID);

    ItemInventario item1 = new ItemInventario();
    Carta c1 = new Carta();
    c1.setId(1L);
    c1.setRareza("COMUN");
    item1.setCarta(c1);
    item1.setCantidad(2);

    ItemInventario item2 = new ItemInventario();
    Carta c2 = new Carta();
    c2.setId(2L);
    c2.setRareza("COMUN");
    item2.setCarta(c2);
    item2.setCantidad(1);

    obtenerListaMockeada(emisor).add(item1);
    obtenerListaMockeada(emisor).add(item2);

    Carta cartaBuscada = new Carta();
    cartaBuscada.setId(99L);
    cartaBuscada.setRareza("RARA"); // nivelMaximo = 3, COMUN = 1, ambas pasan el filtro de rareza

    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setUsuarioEmisor(emisor);
    propuesta.setCartaBuscada(cartaBuscada);

    List<Carta> opciones = servicio.obtenerOpcionesRecompensa(propuesta);
    assertThat(opciones, is(notNullValue()));
  }

  // -----------------------------------------------------------------------
  // finalizarIntercambio
  // -----------------------------------------------------------------------

  @Test
  public void finalizarIntercambioLanzaExcepcionSiReceptorNoExiste() {
    when(repoMercadoMock.buscarUsuarioPorJugadorId(999L)).thenReturn(null);
    assertThrows(
      Exception.class,
      () -> servicio.finalizarIntercambio(999L, PROPUESTA_ID, CARTA_ID)
    );
  }

  @Test
  public void finalizarIntercambioLanzaExcepcionSiPropuestaNoEstaActiva() {
    Usuario receptor = crearUsuarioBase(USER_ID);
    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setEstado("FINALIZADA");

    when(repoMercadoMock.buscarPorId(PROPUESTA_ID)).thenReturn(propuesta);

    assertThrows(
      Exception.class,
      () -> servicio.finalizarIntercambio(jugadorIdDe(receptor), PROPUESTA_ID, CARTA_ID)
    );
  }

  @Test
  public void finalizarIntercambioLanzaExcepcionSiReceptorNoTieneLaCartaBuscada() {
    Usuario receptor = crearUsuarioBase(USER_ID);

    Carta cartaBuscada = new Carta();
    cartaBuscada.setId(CARTA_ID);
    cartaBuscada.setRareza("COMUN");

    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setEstado("ACTIVA");
    propuesta.setCartaBuscada(cartaBuscada);

    when(repoMercadoMock.buscarPorId(PROPUESTA_ID)).thenReturn(propuesta);
    // buscarItemDeJugador devuelve null → receptor no tiene la carta
    when(repoInventarioMock.buscarItemDeJugador(jugadorIdDe(receptor), CARTA_ID)).thenReturn(null);

    assertThrows(
      Exception.class,
      () -> servicio.finalizarIntercambio(jugadorIdDe(receptor), PROPUESTA_ID, CARTA_ID)
    );
  }

  @Test
  public void finalizarIntercambioLanzaExcepcionSiEmisorNoTieneRecompensa() {
    Usuario receptor = crearUsuarioBase(USER_ID);
    Usuario emisor = crearUsuarioBase(2L);

    Carta cartaB = new Carta();
    cartaB.setId(CARTA_ID);
    cartaB.setRareza("RARA");

    ItemInventario itemReceptor = new ItemInventario();
    itemReceptor.setCarta(cartaB);
    itemReceptor.setCantidad(2);

    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setEstado("ACTIVA");
    propuesta.setCartaBuscada(cartaB);
    propuesta.setUsuarioEmisor(emisor);

    when(repoMercadoMock.buscarPorId(PROPUESTA_ID)).thenReturn(propuesta);
    when(repoInventarioMock.buscarItemDeJugador(jugadorIdDe(receptor), CARTA_ID))
      .thenReturn(itemReceptor);
    when(repoInventarioMock.buscarItemDeJugador(jugadorIdDe(emisor), 55L)).thenReturn(null);

    assertThrows(
      Exception.class,
      () -> servicio.finalizarIntercambio(jugadorIdDe(receptor), PROPUESTA_ID, 55L)
    );
  }

  /**
   * NUEVA REGLA: entrego COMUN → solo puedo pedir COMUN o superior.
   * Si intento pedir una carta de rareza INFERIOR (imposible en este caso con COMUN),
   * el test verifica el caso donde entrego RARA y pido COMUN (rareza inferior → lanza error).
   */
  @Test
  public void finalizarIntercambioLanzaExcepcionPorRarezaIncompatible() {
    Usuario receptor = crearUsuarioBase(USER_ID);
    Usuario emisor = crearUsuarioBase(2L);

    // El receptor entrega una RARA
    Carta cartaB = new Carta();
    cartaB.setId(CARTA_ID);
    cartaB.setRareza("RARA");

    // El emisor intenta dar una COMUN → rareza inferior a RARA → debe fallar
    Carta cartaRecompensa = new Carta();
    cartaRecompensa.setId(55L);
    cartaRecompensa.setRareza("COMUN");

    ItemInventario itemReceptor = new ItemInventario();
    itemReceptor.setCarta(cartaB);
    itemReceptor.setCantidad(2);

    ItemInventario itemEmisor = new ItemInventario();
    itemEmisor.setCarta(cartaRecompensa);
    itemEmisor.setCantidad(2);

    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setEstado("ACTIVA");
    propuesta.setCartaBuscada(cartaB);
    propuesta.setUsuarioEmisor(emisor);

    when(repoMercadoMock.buscarPorId(PROPUESTA_ID)).thenReturn(propuesta);
    when(repoInventarioMock.buscarItemDeJugador(jugadorIdDe(receptor), CARTA_ID))
      .thenReturn(itemReceptor);
    when(repoInventarioMock.buscarItemDeJugador(jugadorIdDe(emisor), 55L)).thenReturn(itemEmisor);
    when(repoCartaMock.buscarPorId(55L)).thenReturn(cartaRecompensa);

    assertThrows(
      Exception.class,
      () -> servicio.finalizarIntercambio(jugadorIdDe(receptor), PROPUESTA_ID, 55L)
    );
  }

  @Test
  public void finalizarIntercambioExitosoConRarezaYCreacionDeItemsNuevos() throws Exception {
    Usuario receptor = crearUsuarioBase(USER_ID);
    Usuario emisor = crearUsuarioBase(2L);

    // El receptor entrega COMUN → puede pedir COMUN o superior
    Carta cartaB = new Carta();
    cartaB.setId(CARTA_ID);
    cartaB.setRareza("COMUN");

    // El emisor da RARA → rareza mayor que COMUN → PERMITIDO
    Carta cartaRecompensa = new Carta();
    cartaRecompensa.setId(55L);
    cartaRecompensa.setRareza("RARA");

    ItemInventario itemReceptor = new ItemInventario();
    itemReceptor.setCarta(cartaB);
    itemReceptor.setCantidad(2);

    ItemInventario itemEmisor = new ItemInventario();
    itemEmisor.setCarta(cartaRecompensa);
    itemEmisor.setCantidad(2);

    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setEstado("ACTIVA");
    propuesta.setCartaBuscada(cartaB);
    propuesta.setUsuarioEmisor(emisor);

    when(repoMercadoMock.buscarPorId(PROPUESTA_ID)).thenReturn(propuesta);
    when(repoInventarioMock.buscarItemDeJugador(jugadorIdDe(receptor), CARTA_ID))
      .thenReturn(itemReceptor);
    when(repoInventarioMock.buscarItemDeJugador(jugadorIdDe(emisor), 55L)).thenReturn(itemEmisor);
    // Acreditaciones: ninguno tiene el item del otro todavía → null → se crea nuevo
    when(repoInventarioMock.buscarItemDeJugador(jugadorIdDe(emisor), CARTA_ID)).thenReturn(null);
    when(repoInventarioMock.buscarItemDeJugador(jugadorIdDe(receptor), 55L)).thenReturn(null);
    when(repoCartaMock.buscarPorId(55L)).thenReturn(cartaRecompensa);

    servicio.finalizarIntercambio(jugadorIdDe(receptor), PROPUESTA_ID, 55L);

    assertThat(propuesta.getEstado(), is("FINALIZADA"));
  }

  @Test
  public void finalizarIntercambioIncrementaCantidadSiElItemYaExisteEnElDestino() throws Exception {
    Usuario receptor = crearUsuarioBase(USER_ID);
    Usuario emisor = crearUsuarioBase(2L);

    Carta cartaB = new Carta();
    cartaB.setId(CARTA_ID);
    cartaB.setRareza("COMUN");

    Carta cartaRecompensa = new Carta();
    cartaRecompensa.setId(55L);
    cartaRecompensa.setRareza("RARA");

    ItemInventario itemReceptorEntrega = new ItemInventario();
    itemReceptorEntrega.setCarta(cartaB);
    itemReceptorEntrega.setCantidad(2);

    ItemInventario itemEmisorOfrece = new ItemInventario();
    itemEmisorOfrece.setCarta(cartaRecompensa);
    itemEmisorOfrece.setCantidad(2);

    // Ya existen en destino
    ItemInventario yaExisteEmisor = new ItemInventario();
    yaExisteEmisor.setCarta(cartaB);
    yaExisteEmisor.setCantidad(1);

    ItemInventario yaExisteReceptor = new ItemInventario();
    yaExisteReceptor.setCarta(cartaRecompensa);
    yaExisteReceptor.setCantidad(1);

    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setEstado("ACTIVA");
    propuesta.setCartaBuscada(cartaB);
    propuesta.setUsuarioEmisor(emisor);

    when(repoMercadoMock.buscarPorId(PROPUESTA_ID)).thenReturn(propuesta);

    when(repoCartaMock.buscarPorId(55L)).thenReturn(cartaRecompensa);

    // Validación inicial
    when(repoInventarioMock.buscarItemDeJugador(jugadorIdDe(receptor), CARTA_ID))
      .thenReturn(itemReceptorEntrega);

    when(repoInventarioMock.buscarItemDeJugador(jugadorIdDe(emisor), 55L))
      .thenReturn(itemEmisorOfrece);

    // Acreditación
    when(repoInventarioMock.buscarItemDeJugador(jugadorIdDe(emisor), CARTA_ID))
      .thenReturn(yaExisteEmisor);

    when(repoInventarioMock.buscarItemDeJugador(jugadorIdDe(receptor), 55L))
      .thenReturn(yaExisteReceptor);

    servicio.finalizarIntercambio(jugadorIdDe(receptor), PROPUESTA_ID, 55L);

    assertThat(yaExisteEmisor.getCantidad(), is(2));
    assertThat(yaExisteReceptor.getCantidad(), is(2));
  }

  /**
   * NUEVA REGLA: entrego POCO COMUN → puedo pedir EXOTICA (rareza mayor) → PERMITIDO.
   */
  @Test
  public void finalizarIntercambioSoportaRarezaPocoComunYExotica() throws Exception {
    Usuario receptor = crearUsuarioBase(USER_ID);
    Usuario emisor = crearUsuarioBase(2L);

    // El receptor entrega POCO COMUN
    Carta cartaB = new Carta();
    cartaB.setId(CARTA_ID);
    cartaB.setRareza("POCO COMUN");

    // El emisor da EXOTICA → rareza mayor → PERMITIDO con la nueva regla
    Carta cartaRecompensa = new Carta();
    cartaRecompensa.setId(55L);
    cartaRecompensa.setRareza("EXOTICA");

    ItemInventario itemReceptor = new ItemInventario();
    itemReceptor.setCarta(cartaB);
    itemReceptor.setCantidad(2);

    ItemInventario itemEmisor = new ItemInventario();
    itemEmisor.setCarta(cartaRecompensa);
    itemEmisor.setCantidad(2);

    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setEstado("ACTIVA");
    propuesta.setCartaBuscada(cartaB);
    propuesta.setUsuarioEmisor(emisor);

    when(repoMercadoMock.buscarPorId(PROPUESTA_ID)).thenReturn(propuesta);
    when(repoInventarioMock.buscarItemDeJugador(jugadorIdDe(receptor), CARTA_ID))
      .thenReturn(itemReceptor);
    when(repoInventarioMock.buscarItemDeJugador(jugadorIdDe(emisor), 55L)).thenReturn(itemEmisor);
    // Acreditaciones
    when(repoInventarioMock.buscarItemDeJugador(jugadorIdDe(emisor), CARTA_ID)).thenReturn(null);
    when(repoInventarioMock.buscarItemDeJugador(jugadorIdDe(receptor), 55L)).thenReturn(null);
    when(repoCartaMock.buscarPorId(55L)).thenReturn(cartaRecompensa);

    // Con la nueva regla esto debe ser exitoso
    servicio.finalizarIntercambio(jugadorIdDe(receptor), PROPUESTA_ID, 55L);
    assertThat(propuesta.getEstado(), is("FINALIZADA"));
  }

  @Test
  public void finalizarIntercambioSoportaRarezaNullYDefault() throws Exception {
    Usuario receptor = crearUsuarioBase(USER_ID);
    Usuario emisor = crearUsuarioBase(2L);

    Carta cartaB = new Carta();
    cartaB.setId(CARTA_ID);
    cartaB.setRareza(null); // null → nivel 0

    Carta cartaRecompensa = new Carta();
    cartaRecompensa.setId(55L);
    cartaRecompensa.setRareza("CUALQUIER_OTRA"); // default → nivel 0, igual a null → permitido

    ItemInventario itemReceptor = new ItemInventario();
    itemReceptor.setCarta(cartaB);
    itemReceptor.setCantidad(2);

    ItemInventario itemEmisor = new ItemInventario();
    itemEmisor.setCarta(cartaRecompensa);
    itemEmisor.setCantidad(2);

    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setEstado("ACTIVA");
    propuesta.setCartaBuscada(cartaB);
    propuesta.setUsuarioEmisor(emisor);

    when(repoMercadoMock.buscarPorId(PROPUESTA_ID)).thenReturn(propuesta);
    when(repoInventarioMock.buscarItemDeJugador(jugadorIdDe(receptor), CARTA_ID))
      .thenReturn(itemReceptor);
    when(repoInventarioMock.buscarItemDeJugador(jugadorIdDe(emisor), 55L)).thenReturn(itemEmisor);
    when(repoInventarioMock.buscarItemDeJugador(jugadorIdDe(emisor), CARTA_ID)).thenReturn(null);
    when(repoInventarioMock.buscarItemDeJugador(jugadorIdDe(receptor), 55L)).thenReturn(null);
    when(repoCartaMock.buscarPorId(55L)).thenReturn(cartaRecompensa);

    servicio.finalizarIntercambio(jugadorIdDe(receptor), PROPUESTA_ID, 55L);
    assertThat(propuesta.getEstado(), is("FINALIZADA"));
  }

  // -----------------------------------------------------------------------
  // eliminarMiTrade
  // -----------------------------------------------------------------------

  @Test
  public void eliminarMiTradeLanzaExcepcionSiNoEsSuPropuesta() {
    // El emisor tiene userId=99, jugadorId=1099
    Usuario emisor = crearUsuarioBase(99L);
    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setEstado("ACTIVA");
    propuesta.setUsuarioEmisor(emisor);

    when(repoMercadoMock.buscarPorId(PROPUESTA_ID)).thenReturn(propuesta);
    // El usuario que intenta eliminar tiene jugadorId = USER_ID+1000 = 1001, userId = USER_ID = 1
    Usuario otroUsuario = crearUsuarioBase(USER_ID);

    assertThrows(
      Exception.class,
      () -> servicio.eliminarMiTrade(jugadorIdDe(otroUsuario), PROPUESTA_ID)
    );
  }

  @Test
  public void eliminarMiTradeExitoso() throws Exception {
    Usuario emisor = crearUsuarioBase(USER_ID);
    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setEstado("ACTIVA");
    propuesta.setUsuarioEmisor(emisor);

    when(repoMercadoMock.buscarPorId(PROPUESTA_ID)).thenReturn(propuesta);

    servicio.eliminarMiTrade(jugadorIdDe(emisor), PROPUESTA_ID);
    verify(repoMercadoMock, times(1)).eliminar(propuesta);
  }

  // -----------------------------------------------------------------------
  // esEmisor
  // -----------------------------------------------------------------------

  @Test
  public void esEmisorDevuelveTrueCuandoElJugadorEsElEmisor() {
    Usuario usuario = crearUsuarioBase(USER_ID);

    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setUsuarioEmisor(usuario);

    boolean resultado = servicio.esEmisor(jugadorIdDe(usuario), propuesta);

    assertThat(resultado, is(true));
  }

  @Test
  public void esEmisorDevuelveFalseCuandoElJugadorNoEsElEmisor() {
    Usuario emisor = crearUsuarioBase(USER_ID);
    Usuario otro = crearUsuarioBase(2L);

    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setUsuarioEmisor(emisor);

    boolean resultado = servicio.esEmisor(jugadorIdDe(otro), propuesta);

    assertThat(resultado, is(false));
  }

  @Test
  public void esEmisorDevuelveFalseSiLaPropuestaEsNull() {
    boolean resultado = servicio.esEmisor(USER_ID, null);

    assertThat(resultado, is(false));
  }

  @Test
  public void obtenerOpcionesRecompensaAceptaCartaLegendaria() {
    Usuario emisor = crearUsuarioBase(USER_ID);

    Carta cartaBuscada = new Carta();
    cartaBuscada.setRareza("RARA");

    Carta legendaria = new Carta();
    legendaria.setRareza("LEGENDARIA");

    ItemInventario item = new ItemInventario();
    item.setCarta(legendaria);
    item.setCantidad(2);

    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setUsuarioEmisor(emisor);
    propuesta.setCartaBuscada(cartaBuscada);

    when(repoInventarioMock.listarInventarioDeJugador(jugadorIdDe(emisor)))
      .thenReturn(List.of(item));

    List<Carta> opciones = servicio.obtenerOpcionesRecompensa(propuesta);

    assertThat(opciones.size(), is(1));
    assertThat(opciones.get(0), is(legendaria));
  }

  @Test
  public void obtenerTradesAceptadosDevuelveListaDelRepositorio() {
    Usuario usuario = crearUsuarioBase(USER_ID);

    List<PropuestaIntercambio> lista = List.of(new PropuestaIntercambio());

    when(repoMercadoMock.buscarUsuarioPorJugadorId(USER_ID)).thenReturn(usuario);

    when(repoMercadoMock.listarTradesAceptados(usuario)).thenReturn(lista);

    List<PropuestaIntercambio> resultado = servicio.obtenerTradesAceptados(USER_ID);

    assertThat(resultado, is(lista));
  }

  @Test
  public void obtenerOpcionesRecompensaAgregaCartaValida() {
    Usuario emisor = crearUsuarioBase(USER_ID);

    Carta buscada = new Carta();
    buscada.setRareza("COMUN");

    Carta recompensa = new Carta();
    recompensa.setRareza("RARA");

    ItemInventario item = new ItemInventario();
    item.setCarta(recompensa);
    item.setCantidad(2);

    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setUsuarioEmisor(emisor);
    propuesta.setCartaBuscada(buscada);

    when(repoInventarioMock.listarInventarioDeJugador(jugadorIdDe(emisor)))
      .thenReturn(List.of(item));

    List<Carta> resultado = servicio.obtenerOpcionesRecompensa(propuesta);

    assertThat(resultado.size(), is(1));
  }

  @Test
  public void finalizarIntercambioFallaCuandoElReceptorNoTieneLaCarta() {
    Usuario receptor = crearUsuarioBase(USER_ID);
    Usuario emisor = crearUsuarioBase(2L);

    Carta buscada = new Carta();
    buscada.setId(CARTA_ID);
    buscada.setRareza("COMUN");

    Carta recompensa = new Carta();
    recompensa.setId(55L);
    recompensa.setRareza("RARA");

    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setEstado("ACTIVA");
    propuesta.setCartaBuscada(buscada);
    propuesta.setUsuarioEmisor(emisor);

    when(repoMercadoMock.buscarUsuarioPorJugadorId(USER_ID)).thenReturn(receptor);

    when(repoMercadoMock.buscarPorId(PROPUESTA_ID)).thenReturn(propuesta);

    when(repoCartaMock.buscarPorId(55L)).thenReturn(recompensa);

    when(repoInventarioMock.buscarItemDeJugador(jugadorIdDe(receptor), CARTA_ID)).thenReturn(null);

    Exception ex = assertThrows(
      Exception.class,
      () -> servicio.finalizarIntercambio(jugadorIdDe(receptor), PROPUESTA_ID, 55L)
    );

    assertThat(ex.getMessage(), is("No tienes la carta solicitada repetida"));
  }
}
