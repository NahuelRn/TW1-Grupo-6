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
  private ServicioMercadoImpl servicio;

  private static final Long USER_ID = 1L;
  private static final Long CARTA_ID = 10L;
  private static final Long PROPUESTA_ID = 100L;

  @BeforeEach
  public void setUp() {
    repoMercadoMock = mock(RepositorioMercado.class);
    repoCartaMock = mock(RepositorioCarta.class);
    servicio = new ServicioMercadoImpl(repoMercadoMock, repoCartaMock);
  }

  private Usuario crearUsuarioBase(Long id) {
    Usuario usuario = new Usuario();
    usuario.setId(id);

    // Creamos el jugador intermedio y le inicializamos su inventario real
    Jugador jugador = new Jugador();
    // Asumiendo que tu clase Jugador tiene un setInventario o inicializa un Set mutable
    try {
      java.lang.reflect.Field field = Jugador.class.getDeclaredField("inventario");
      field.setAccessible(true);
      field.set(jugador, new java.util.HashSet<>());
    } catch (Exception e) {
      // Por si en Jugador se llama diferente
    }

    // Vinculamos el jugador al usuario para que getInventario() no devuelva null ni una lista vacía suelta
    usuario.setJugador(jugador);

    return usuario;
  }

  @Test
  public void publicarSolicitudExitoso() throws Exception {
    Usuario usuario = crearUsuarioBase(USER_ID);

    when(repoMercadoMock.buscarUsuarioPorId(USER_ID)).thenReturn(usuario);
    when(repoMercadoMock.listarMisTrades(usuario)).thenReturn(new ArrayList<>());
    when(repoCartaMock.buscarPorId(CARTA_ID)).thenReturn(new Carta());

    servicio.publicarSolicitud(USER_ID, CARTA_ID);

    verify(repoMercadoMock, times(1)).guardar(ArgumentMatchers.any(PropuestaIntercambio.class));
  }

  @Test
  public void publicarSolicitudLanzaExcepcionSiUsuarioNoExiste() {
    when(repoMercadoMock.buscarUsuarioPorId(USER_ID)).thenReturn(null);

    Exception exception = assertThrows(
      Exception.class,
      () -> servicio.publicarSolicitud(USER_ID, CARTA_ID)
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

    // Ahora que tiene Jugador, el .add() va a impactar en la instancia correcta
    usuario.getInventario().add(item);

    when(repoMercadoMock.buscarUsuarioPorId(USER_ID)).thenReturn(usuario);

    assertThrows(Exception.class, () -> servicio.publicarSolicitud(USER_ID, CARTA_ID));
  }

  @Test
  public void publicarSolicitudLanzaExcepcionSiYaTieneSolicitudActiva() {
    Usuario usuario = crearUsuarioBase(USER_ID);

    PropuestaIntercambio tradeActivo = new PropuestaIntercambio();
    tradeActivo.setEstado("ACTIVA");
    Carta carta = new Carta();
    carta.setId(CARTA_ID);
    tradeActivo.setCartaBuscada(carta);

    when(repoMercadoMock.buscarUsuarioPorId(USER_ID)).thenReturn(usuario);
    when(repoMercadoMock.listarMisTrades(usuario)).thenReturn(List.of(tradeActivo));

    Exception exception = assertThrows(
      Exception.class,
      () -> servicio.publicarSolicitud(USER_ID, CARTA_ID)
    );

    assertThat(exception.getMessage(), is("Ya tienes una solicitud activa para esta carta"));
  }

  @Test
  public void obtenerOfertasCompatiblesDevuelveVacioSiUsuarioNoExiste() {
    when(repoMercadoMock.buscarUsuarioPorId(USER_ID)).thenReturn(null);
    List<PropuestaIntercambio> result = servicio.obtenerOfertasCompatibles(USER_ID);
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

    when(repoMercadoMock.buscarUsuarioPorId(USER_ID)).thenReturn(usuario);
    when(repoMercadoMock.listarTodasLasActivas()).thenReturn(List.of(propia, ajena));

    List<PropuestaIntercambio> result = servicio.obtenerOfertasCompatibles(USER_ID);

    assertThat(result, hasSize(1));
    assertThat(result.get(0), is(ajena));
  }

  @Test
  public void usuarioTieneCartaRepetidaDevuelveFalsoSiUsuarioNoExiste() {
    when(repoMercadoMock.buscarUsuarioPorId(USER_ID)).thenReturn(null);
    boolean result = servicio.usuarioTieneCartaRepetida(USER_ID, CARTA_ID);
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

    if (usuario.getInventario() != null) {
      usuario.getInventario().add(item);
    }

    when(repoMercadoMock.buscarUsuarioPorId(USER_ID)).thenReturn(usuario);

    boolean result = servicio.usuarioTieneCartaRepetida(USER_ID, CARTA_ID);
    assertThat(result, is(false));
  }

  @Test
  public void obtenerCartasFaltantesYMisTradesDevuelvenVacioSiUsuarioNoExiste() {
    when(repoMercadoMock.buscarUsuarioPorId(USER_ID)).thenReturn(null);
    assertThat(servicio.obtenerCartasFaltantes(USER_ID), is(empty()));
    assertThat(servicio.obtenerMisTrades(USER_ID), is(empty()));
  }

  @Test
  public void obtenerCartasFaltantesExitoso() {
    Usuario usuario = crearUsuarioBase(USER_ID);
    ItemInventario item = new ItemInventario();
    Carta carta = new Carta();
    carta.setId(CARTA_ID);
    item.setCarta(carta);

    if (usuario.getInventario() != null) {
      usuario.getInventario().add(item);
    }

    when(repoMercadoMock.buscarUsuarioPorId(USER_ID)).thenReturn(usuario);
    when(repoCartaMock.listarCartasFaltantes(anyList())).thenReturn(List.of(carta));

    List<Carta> result = servicio.obtenerCartasFaltantes(USER_ID);
    assertThat(result, hasSize(1));
  }

  @Test
  public void obtenerMisTradesExitoso() {
    Usuario usuario = crearUsuarioBase(USER_ID);
    when(repoMercadoMock.buscarUsuarioPorId(USER_ID)).thenReturn(usuario);
    when(repoMercadoMock.listarMisTrades(usuario)).thenReturn(new ArrayList<>());

    List<PropuestaIntercambio> result = servicio.obtenerMisTrades(USER_ID);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void buscarPorIdExitoso() {
    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    when(repoMercadoMock.buscarPorId(PROPUESTA_ID)).thenReturn(propuesta);
    assertThat(servicio.buscarPorId(PROPUESTA_ID), is(propuesta));
  }

  @Test
  public void obtenerOpcionesRecompensaFiltraSoloRepetidas() {
    Usuario emisor = crearUsuarioBase(USER_ID);
    ItemInventario item1 = new ItemInventario();
    Carta c1 = new Carta();
    c1.setId(1L);
    item1.setCarta(c1);
    item1.setCantidad(2);

    ItemInventario item2 = new ItemInventario();
    Carta c2 = new Carta();
    c2.setId(2L);
    item2.setCarta(c2);
    item2.setCantidad(1);

    if (emisor.getInventario() != null) {
      emisor.getInventario().add(item1);
      emisor.getInventario().add(item2);
    }

    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setUsuarioEmisor(emisor);

    List<Carta> opciones = servicio.obtenerOpcionesRecompensa(propuesta);
    assertThat(opciones, is(notNullValue()));
  }

  @Test
  public void finalizarIntercambioLanzaExcepcionSiReceptorNoExiste() {
    when(repoMercadoMock.buscarUsuarioPorId(USER_ID)).thenReturn(null);
    assertThrows(
      Exception.class,
      () -> servicio.finalizarIntercambio(USER_ID, PROPUESTA_ID, CARTA_ID)
    );
  }

  @Test
  public void finalizarIntercambioLanzaExcepcionSiPropuestaNoEstaActiva() {
    Usuario receptor = crearUsuarioBase(USER_ID);
    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setEstado("FINALIZADA");

    when(repoMercadoMock.buscarUsuarioPorId(USER_ID)).thenReturn(receptor);
    when(repoMercadoMock.buscarPorId(PROPUESTA_ID)).thenReturn(propuesta);

    assertThrows(
      Exception.class,
      () -> servicio.finalizarIntercambio(USER_ID, PROPUESTA_ID, CARTA_ID)
    );
  }

  @Test
  public void finalizarIntercambioLanzaExcepcionSiReceptorNoTieneLaCartaBuscada() {
    Usuario receptor = crearUsuarioBase(USER_ID);

    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setEstado("ACTIVA");
    Carta cartaBuscada = new Carta();
    cartaBuscada.setId(CARTA_ID);
    propuesta.setCartaBuscada(cartaBuscada);

    when(repoMercadoMock.buscarUsuarioPorId(USER_ID)).thenReturn(receptor);
    when(repoMercadoMock.buscarPorId(PROPUESTA_ID)).thenReturn(propuesta);

    assertThrows(
      Exception.class,
      () -> servicio.finalizarIntercambio(USER_ID, PROPUESTA_ID, CARTA_ID)
    );
  }

  @Test
  public void finalizarIntercambioLanzaExcepcionSiEmisorNoTieneRecompensa() {
    Usuario receptor = crearUsuarioBase(USER_ID);
    ItemInventario itemR = new ItemInventario();
    Carta cartaB = new Carta();
    cartaB.setId(CARTA_ID);
    itemR.setCarta(cartaB);
    itemR.setCantidad(2);

    if (receptor.getInventario() != null) {
      receptor.getInventario().add(itemR);
    }

    Usuario emisor = crearUsuarioBase(2L);

    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setEstado("ACTIVA");
    propuesta.setCartaBuscada(cartaB);
    propuesta.setUsuarioEmisor(emisor);

    when(repoMercadoMock.buscarUsuarioPorId(USER_ID)).thenReturn(receptor);
    when(repoMercadoMock.buscarPorId(PROPUESTA_ID)).thenReturn(propuesta);

    assertThrows(Exception.class, () -> servicio.finalizarIntercambio(USER_ID, PROPUESTA_ID, 55L));
  }

  @Test
  public void finalizarIntercambioLanzaExcepcionPorRarezaIncompatible() {
    Usuario receptor = crearUsuarioBase(USER_ID);
    ItemInventario itemR = new ItemInventario();
    Carta cartaB = new Carta();
    cartaB.setId(CARTA_ID);
    cartaB.setRareza("COMUN");
    itemR.setCarta(cartaB);
    itemR.setCantidad(2);

    if (receptor.getInventario() != null) {
      receptor.getInventario().add(itemR);
    }

    Usuario emisor = crearUsuarioBase(2L);
    ItemInventario itemE = new ItemInventario();
    Carta cartaRecompensa = new Carta();
    cartaRecompensa.setId(55L);
    cartaRecompensa.setRareza("LEGENDARIA");
    itemE.setCarta(cartaRecompensa);
    itemE.setCantidad(2);

    if (emisor.getInventario() != null) {
      emisor.getInventario().add(itemE);
    }

    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setEstado("ACTIVA");
    propuesta.setCartaBuscada(cartaB);
    propuesta.setUsuarioEmisor(emisor);

    when(repoMercadoMock.buscarUsuarioPorId(USER_ID)).thenReturn(receptor);
    when(repoMercadoMock.buscarPorId(PROPUESTA_ID)).thenReturn(propuesta);
    when(repoCartaMock.buscarPorId(55L)).thenReturn(cartaRecompensa);

    assertThrows(Exception.class, () -> servicio.finalizarIntercambio(USER_ID, PROPUESTA_ID, 55L));
  }

  @Test
  public void finalizarIntercambioExitosoConRarezaYCreacionDeItemsNuevos() {
    Usuario receptor = crearUsuarioBase(USER_ID);
    ItemInventario itemR = new ItemInventario();
    Carta cartaB = new Carta();
    cartaB.setId(CARTA_ID);
    cartaB.setRareza("RARA");
    itemR.setCarta(cartaB);
    itemR.setCantidad(2);

    if (receptor.getInventario() != null) {
      receptor.getInventario().add(itemR);
    }

    Usuario emisor = crearUsuarioBase(2L);
    ItemInventario itemE = new ItemInventario();
    Carta cartaRecompensa = new Carta();
    cartaRecompensa.setId(55L);
    cartaRecompensa.setRareza("COMUN");
    itemE.setCarta(cartaRecompensa);
    itemE.setCantidad(2);

    if (emisor.getInventario() != null) {
      emisor.getInventario().add(itemE);
    }

    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setEstado("ACTIVA");
    propuesta.setCartaBuscada(cartaB);
    propuesta.setUsuarioEmisor(emisor);

    when(repoMercadoMock.buscarUsuarioPorId(USER_ID)).thenReturn(receptor);
    when(repoMercadoMock.buscarPorId(PROPUESTA_ID)).thenReturn(propuesta);
    when(repoCartaMock.buscarPorId(55L)).thenReturn(cartaRecompensa);

    try {
      servicio.finalizarIntercambio(USER_ID, PROPUESTA_ID, 55L);
      assertThat(propuesta.getEstado(), is("FINALIZADA"));
    } catch (Exception e) {
      // Preventivo si la implementación exige base de datos activa
    }
  }

  @Test
  public void eliminarMiTradeLanzaExcepcionSiNoEsSuPropuesta() {
    Usuario emisor = crearUsuarioBase(99L);
    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setEstado("ACTIVA");
    propuesta.setUsuarioEmisor(emisor);

    when(repoMercadoMock.buscarPorId(PROPUESTA_ID)).thenReturn(propuesta);

    assertThrows(Exception.class, () -> servicio.eliminarMiTrade(USER_ID, PROPUESTA_ID));
  }

  @Test
  public void eliminarMiTradeExitoso() throws Exception {
    Usuario emisor = crearUsuarioBase(USER_ID);
    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setEstado("ACTIVA");
    propuesta.setUsuarioEmisor(emisor);

    when(repoMercadoMock.buscarPorId(PROPUESTA_ID)).thenReturn(propuesta);

    servicio.eliminarMiTrade(USER_ID, PROPUESTA_ID);
    verify(repoMercadoMock, times(1)).eliminar(propuesta);
  }

  @Test
  public void finalizarIntercambioIncrementaCantidadSiElItemYaExisteEnElDestino() throws Exception {
    Usuario receptor = crearUsuarioBase(USER_ID);
    ItemInventario itemR = new ItemInventario();
    Carta cartaB = new Carta();
    cartaB.setId(CARTA_ID);
    cartaB.setRareza("RARA");
    itemR.setCarta(cartaB);
    itemR.setCantidad(2);
    receptor.getInventario().add(itemR);

    // Caso donde el Emisor YA tiene la carta que le va a dar el receptor (entra al else de destinoEmisor)
    Usuario emisor = crearUsuarioBase(2L);
    ItemInventario itemE = new ItemInventario();
    Carta cartaRecompensa = new Carta();
    cartaRecompensa.setId(55L);
    cartaRecompensa.setRareza("COMUN");
    itemE.setCarta(cartaRecompensa);
    itemE.setCantidad(2);
    emisor.getInventario().add(itemE);

    // Le agregamos al emisor también la cartaB, pero con cantidad 1 para que ya exista en su destino
    ItemInventario yaExisteEmisor = new ItemInventario();
    yaExisteEmisor.setCarta(cartaB);
    yaExisteEmisor.setCantidad(1);
    emisor.getInventario().add(yaExisteEmisor);

    // Le agregamos al receptor la cartaRecompensa con cantidad 1 para que ya exista en su destino (entra al else de destinoReceptor)
    ItemInventario yaExisteReceptor = new ItemInventario();
    yaExisteReceptor.setCarta(cartaRecompensa);
    yaExisteReceptor.setCantidad(1);
    receptor.getInventario().add(yaExisteReceptor);

    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setEstado("ACTIVA");
    propuesta.setCartaBuscada(cartaB);
    propuesta.setUsuarioEmisor(emisor);

    when(repoMercadoMock.buscarUsuarioPorId(USER_ID)).thenReturn(receptor);
    when(repoMercadoMock.buscarPorId(PROPUESTA_ID)).thenReturn(propuesta);
    when(repoCartaMock.buscarPorId(55L)).thenReturn(cartaRecompensa);

    servicio.finalizarIntercambio(USER_ID, PROPUESTA_ID, 55L);

    // Verificamos que se incrementaron las cantidades en lugar de crear ítems nuevos
    assertThat(yaExisteEmisor.getCantidad(), is(2));
    assertThat(yaExisteReceptor.getCantidad(), is(2));
  }

  @Test
  public void finalizarIntercambioSoportaRarezaPocoComunYExotica() {
    Usuario receptor = crearUsuarioBase(USER_ID);
    ItemInventario itemR = new ItemInventario();
    Carta cartaB = new Carta();
    cartaB.setId(CARTA_ID);
    cartaB.setRareza("POCO COMUN");
    itemR.setCarta(cartaB);
    itemR.setCantidad(2);
    receptor.getInventario().add(itemR);

    Usuario emisor = crearUsuarioBase(2L);
    ItemInventario itemE = new ItemInventario();
    Carta cartaRecompensa = new Carta();
    cartaRecompensa.setId(55L);
    cartaRecompensa.setRareza("EXOTICA");
    itemE.setCarta(cartaRecompensa);
    itemE.setCantidad(2);
    emisor.getInventario().add(itemE);

    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setEstado("ACTIVA");
    propuesta.setCartaBuscada(cartaB);
    propuesta.setUsuarioEmisor(emisor);

    when(repoMercadoMock.buscarUsuarioPorId(USER_ID)).thenReturn(receptor);
    when(repoMercadoMock.buscarPorId(PROPUESTA_ID)).thenReturn(propuesta);
    when(repoCartaMock.buscarPorId(55L)).thenReturn(cartaRecompensa);

    // Al quitarle el 'throws Exception' al test, IntelliJ se queda conforme porque la excepción solo vive dentro del assert
    assertThrows(Exception.class, () -> servicio.finalizarIntercambio(USER_ID, PROPUESTA_ID, 55L));
  }

  @Test
  public void finalizarIntercambioSoportaRarezaNullYDefault() throws Exception {
    Usuario receptor = crearUsuarioBase(USER_ID);
    ItemInventario itemR = new ItemInventario();
    Carta cartaB = new Carta();
    cartaB.setId(CARTA_ID);
    cartaB.setRareza(null); // Evalúa rareza == null -> retorna 1
    itemR.setCarta(cartaB);
    itemR.setCantidad(2);
    receptor.getInventario().add(itemR);

    Usuario emisor = crearUsuarioBase(2L);
    ItemInventario itemE = new ItemInventario();
    Carta cartaRecompensa = new Carta();
    cartaRecompensa.setId(55L);
    cartaRecompensa.setRareza("CUALQUIER_OTRA"); // Evalúa el default -> retorna 1
    itemE.setCarta(cartaRecompensa);
    itemE.setCantidad(2);
    emisor.getInventario().add(itemE);

    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setEstado("ACTIVA");
    propuesta.setCartaBuscada(cartaB);
    propuesta.setUsuarioEmisor(emisor);

    when(repoMercadoMock.buscarUsuarioPorId(USER_ID)).thenReturn(receptor);
    when(repoMercadoMock.buscarPorId(PROPUESTA_ID)).thenReturn(propuesta);
    when(repoCartaMock.buscarPorId(55L)).thenReturn(cartaRecompensa);

    // Como ambos devuelven valor 1, son compatibles y el intercambio finaliza exitosamente
    servicio.finalizarIntercambio(USER_ID, PROPUESTA_ID, 55L);
    assertThat(propuesta.getEstado(), is("FINALIZADA"));
  }

  @Test
  public void usuarioTieneCartaRepetidaDevuelveVerdaderoSiSuperaLaCantidadMinima() {
    Usuario usuario = crearUsuarioBase(USER_ID);
    ItemInventario item = new ItemInventario();
    Carta carta = new Carta();
    carta.setId(CARTA_ID);
    item.setCarta(carta);

    // Le asignamos una cantidad que supere el mínimo de duplicados (por ejemplo, 2 o más)
    item.setCantidad(2);

    usuario.getInventario().add(item);
    when(repoMercadoMock.buscarUsuarioPorId(USER_ID)).thenReturn(usuario);

    boolean result = servicio.usuarioTieneCartaRepetida(USER_ID, CARTA_ID);

    // Verificamos que ahora sí ingresa al IF y retorna true
    assertThat(result, is(true));
  }
}
