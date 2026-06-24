package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioMercadoTest {

  private RepositorioMercado repoMercadoMock;
  private RepositorioCarta repoCartaMock;
  private ServicioMercado servicio;

  private Usuario emisor;
  private Usuario receptor;
  private Carta cartaComun;
  private Carta cartaRara;

  @BeforeEach
  public void setUp() {
    repoMercadoMock = mock(RepositorioMercado.class);
    repoCartaMock = mock(RepositorioCarta.class);
    servicio = new ServicioMercadoImpl(repoMercadoMock, repoCartaMock);

    cartaComun = new Carta();
    cartaComun.setId(1L);
    cartaComun.setRareza("COMUN");
    cartaComun.setNombre("Orco Basico");

    cartaRara = new Carta();
    cartaRara.setId(2L);
    cartaRara.setRareza("RARA");
    cartaRara.setNombre("Dragon de Fuego");

    emisor = new Usuario();
    emisor.setId(10L);
    if (emisor.getInventario() != null) {
      emisor.getInventario().clear();
    }
    Jugador jEmisor = new Jugador();
    jEmisor.setUsuario(emisor);
    emisor.setJugador(jEmisor);

    receptor = new Usuario();
    receptor.setId(20L);
    if (receptor.getInventario() != null) {
      receptor.getInventario().clear();
    }
    Jugador jReceptor = new Jugador();
    jReceptor.setUsuario(receptor);
    receptor.setJugador(jReceptor);
  }

  @Test
  public void alPublicarCartaYaPoseeLanzaError() {
    when(repoCartaMock.buscarPorId(1L)).thenReturn(cartaComun);

    ItemInventario item = new ItemInventario();
    item.setCarta(cartaComun);
    item.setCantidad(1);
    emisor.getInventario().add(item);

    Exception ex = assertThrows(Exception.class, () -> servicio.publicarSolicitud(emisor, 1L));
    assertThat(ex.getMessage(), containsString("No puedes solicitar una carta que ya posees"));
  }

  @Test
  public void alPublicarDuplicadoLanzaError() {
    when(repoCartaMock.buscarPorId(1L)).thenReturn(cartaComun);

    PropuestaIntercambio tradeActivo = new PropuestaIntercambio();
    tradeActivo.setCartaBuscada(cartaComun);
    tradeActivo.setEstado("ACTIVA");

    when(repoMercadoMock.listarMisTrades(emisor)).thenReturn(List.of(tradeActivo));

    Exception ex = assertThrows(Exception.class, () -> servicio.publicarSolicitud(emisor, 1L));
    assertThat(ex.getMessage(), containsString("Ya tienes una solicitud activa"));
  }

  @Test
  public void alAceptarSinCartaDuplicadaLanzaError() {
    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setEstado("ACTIVA");
    propuesta.setUsuarioEmisor(emisor);
    propuesta.setCartaBuscada(cartaRara);

    when(repoMercadoMock.buscarPorId(1L)).thenReturn(propuesta);

    Exception ex = assertThrows(
      Exception.class,
      () -> servicio.finalizarIntercambio(receptor, 1L, 2L)
    );
    assertThat(ex.getMessage(), containsString("No tienes la carta solicitada repetida"));
  }

  @Test
  public void laReglaDeRarezaDebeImpedirElegirUnaCartaDeRecompensaMayorALaEntregada() {
    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setEstado("ACTIVA");
    propuesta.setUsuarioEmisor(emisor);
    propuesta.setCartaBuscada(cartaComun);

    when(repoMercadoMock.buscarPorId(1L)).thenReturn(propuesta);
    when(repoCartaMock.buscarPorId(2L)).thenReturn(cartaRara);

    ItemInventario itemR = new ItemInventario();
    itemR.setCarta(cartaComun);
    itemR.setCantidad(2);
    receptor.getInventario().add(itemR);

    ItemInventario itemE = new ItemInventario();
    itemE.setCarta(cartaRara);
    itemE.setCantidad(2);
    emisor.getInventario().add(itemE);

    Exception ex = assertThrows(
      Exception.class,
      () -> servicio.finalizarIntercambio(receptor, 1L, 2L)
    );
    assertThat(ex.getMessage(), containsString("no permite una recompensa de rareza superior"));
  }

  @Test
  public void unIntercambioValidoModificaStocksYSeteaEstadoFinalizada() throws Exception {
    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setEstado("ACTIVA");
    propuesta.setUsuarioEmisor(emisor);
    propuesta.setCartaBuscada(cartaRara);

    when(repoMercadoMock.buscarPorId(1L)).thenReturn(propuesta);
    when(repoCartaMock.buscarPorId(1L)).thenReturn(cartaComun);

    ItemInventario itemE = new ItemInventario();
    itemE.setCarta(cartaComun);
    itemE.setCantidad(2);
    emisor.getInventario().add(itemE);

    ItemInventario itemR = new ItemInventario();
    itemR.setCarta(cartaRara);
    itemR.setCantidad(2);
    receptor.getInventario().add(itemR);

    servicio.finalizarIntercambio(receptor, 1L, 1L);

    assertThat(itemR.getCantidad(), is(1));
    assertThat(itemE.getCantidad(), is(1));
    assertThat(propuesta.getEstado(), is("FINALIZADA"));
  }

  @Test
  public void eliminarMiTradeExitosoInvocaEliminarEnRepositorio() throws Exception {
    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setEstado("ACTIVA");
    propuesta.setUsuarioEmisor(emisor);

    when(repoMercadoMock.buscarPorId(1L)).thenReturn(propuesta);

    servicio.eliminarMiTrade(emisor, 1L);

    verify(repoMercadoMock, times(1)).eliminar(propuesta);
  }

  @Test
  public void eliminarTradeDeOtroUsuarioLanzaError() {
    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setEstado("ACTIVA");
    propuesta.setUsuarioEmisor(receptor); // Es de otro usuario

    when(repoMercadoMock.buscarPorId(1L)).thenReturn(propuesta);

    Exception ex = assertThrows(Exception.class, () -> servicio.eliminarMiTrade(emisor, 1L));
    assertThat(ex.getMessage(), containsString("No puedes eliminar este trade"));
  }

  @Test
  public void finalizarIntercambioLanzaErrorSiLaPropuestaEsNull() {
    when(repoMercadoMock.buscarPorId(999L)).thenReturn(null);

    Exception ex = assertThrows(
      Exception.class,
      () -> servicio.finalizarIntercambio(receptor, 999L, 1L)
    );
    assertThat(ex.getMessage(), containsString("La propuesta ya no esta disponible"));
  }

  @Test
  public void finalizarIntercambioLanzaErrorSiElEmisorNoTieneLaRecompensaRepetida() {
    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setEstado("ACTIVA");
    propuesta.setUsuarioEmisor(emisor);
    propuesta.setCartaBuscada(cartaRara);

    when(repoMercadoMock.buscarPorId(1L)).thenReturn(propuesta);

    // Receptor tiene la carta que busca el emisor
    ItemInventario itemR = new ItemInventario();
    itemR.setCarta(cartaRara);
    itemR.setCantidad(2);
    receptor.getInventario().add(itemR);

    // Emisor NO tiene la recompensa suficiente (cantidad 1 no es repetida)
    ItemInventario itemE = new ItemInventario();
    itemE.setCarta(cartaComun);
    itemE.setCantidad(1);
    emisor.getInventario().add(itemE);

    Exception ex = assertThrows(
      Exception.class,
      () -> servicio.finalizarIntercambio(receptor, 1L, cartaComun.getId())
    );
    assertThat(ex.getMessage(), containsString("El emisor ya no dispone de esa recompensa"));
  }

  @Test
  public void validarRarezasPermiteIntercambioDeMismaRarezaLegendaria() throws Exception {
    Carta legendaria1 = new Carta();
    legendaria1.setId(3L);
    legendaria1.setRareza("LEGENDARIA");

    Carta legendaria2 = new Carta();
    legendaria2.setId(4L);
    legendaria2.setRareza("LEGENDARIA");

    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setEstado("ACTIVA");
    propuesta.setUsuarioEmisor(emisor);
    propuesta.setCartaBuscada(legendaria1);

    when(repoMercadoMock.buscarPorId(1L)).thenReturn(propuesta);
    when(repoCartaMock.buscarPorId(4L)).thenReturn(legendaria2);

    ItemInventario itemR = new ItemInventario();
    itemR.setCarta(legendaria1);
    itemR.setCantidad(2);
    receptor.getInventario().add(itemR);

    ItemInventario itemE = new ItemInventario();
    itemE.setCarta(legendaria2);
    itemE.setCantidad(2);
    emisor.getInventario().add(itemE);

    servicio.finalizarIntercambio(receptor, 1L, 4L);
    assertThat(propuesta.getEstado(), is("FINALIZADA"));
  }

  @Test
  public void validarRarezasPermiteIntercambioDeMismaRarezaEpica() throws Exception {
    Carta epica1 = new Carta();
    epica1.setId(5L);
    epica1.setRareza("EPICA");

    Carta epica2 = new Carta();
    epica2.setId(6L);
    epica2.setRareza("EPICA");

    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setEstado("ACTIVA");
    propuesta.setUsuarioEmisor(emisor);
    propuesta.setCartaBuscada(epica1);

    when(repoMercadoMock.buscarPorId(1L)).thenReturn(propuesta);
    when(repoCartaMock.buscarPorId(6L)).thenReturn(epica2);

    ItemInventario itemR = new ItemInventario();
    itemR.setCarta(epica1);
    itemR.setCantidad(2);
    receptor.getInventario().add(itemR);

    ItemInventario itemE = new ItemInventario();
    itemE.setCarta(epica2);
    itemE.setCantidad(2);
    emisor.getInventario().add(itemE);

    servicio.finalizarIntercambio(receptor, 1L, 6L);
    assertThat(propuesta.getEstado(), is("FINALIZADA"));
  }

  @Test
  public void obtenerOfertasCompatiblesFiltraCorrectamente() {
    PropuestaIntercambio propuestaAjena = new PropuestaIntercambio();
    propuestaAjena.setEstado("ACTIVA");
    propuestaAjena.setUsuarioEmisor(receptor); // Es de otro
    propuestaAjena.setCartaBuscada(cartaComun);

    when(repoMercadoMock.listarTodasLasActivas()).thenReturn(List.of(propuestaAjena));

    // El emisor tiene la carta repetida que la otra persona busca
    ItemInventario item = new ItemInventario();
    item.setCarta(cartaComun);
    item.setCantidad(2);
    emisor.getInventario().add(item);

    List<PropuestaIntercambio> resultado = servicio.obtenerOfertasCompatibles(emisor);
    assertThat(resultado, hasSize(1));
  }

  @Test
  public void cubrirMetodosNoUsadosDePropuestaIntercambioParaMejorarCobertura() {
    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    java.time.LocalDateTime fecha = java.time.LocalDateTime.now();

    propuesta.setCartaOfrecida(cartaComun);
    propuesta.setFechaCreacion(fecha);

    assertThat(propuesta.getCartaOfrecida(), is(cartaComun));
    assertThat(propuesta.getFechaCreacion(), is(fecha));
  }

  @Test
  public void obtenerCartasFaltantesDebeLlamarAlRepositorioCorrectamente() {
    ItemInventario item = new ItemInventario();
    item.setCarta(cartaComun);
    item.setCantidad(1);
    emisor.getInventario().add(item);

    when(repoCartaMock.listarCartasFaltantes(anyList())).thenReturn(List.of(cartaRara));

    List<Carta> faltantes = servicio.obtenerCartasFaltantes(emisor);

    assertThat(faltantes, hasSize(1));
    assertThat(faltantes.get(0), is(cartaRara));
    verify(repoCartaMock, times(1)).listarCartasFaltantes(anyList());
  }

  @Test
  public void eliminarMiTradeLanzaExceptionSiLaPropuestaNoEstaActiva() {
    PropuestaIntercambio propuestaInactiva = new PropuestaIntercambio();
    propuestaInactiva.setEstado("FINALIZADA"); // No está activa
    propuestaInactiva.setUsuarioEmisor(emisor);

    when(repoMercadoMock.buscarPorId(1L)).thenReturn(propuestaInactiva);

    assertThrows(Exception.class, () -> servicio.eliminarMiTrade(emisor, 1L));
  }

  @Test
  public void eliminarMiTradeLanzaExceptionSiLaPropuestaEsNull() {
    when(repoMercadoMock.buscarPorId(1L)).thenReturn(null);

    assertThrows(Exception.class, () -> servicio.eliminarMiTrade(emisor, 1L));
  }

  @Test
  public void publicarSolicitudExitosaGuardaLaPropuestaEnElRepositorio() throws Exception {
    when(repoCartaMock.buscarPorId(1L)).thenReturn(cartaComun);
    when(repoMercadoMock.listarMisTrades(emisor)).thenReturn(new ArrayList<>());

    // El usuario no tiene la carta en su inventario (camino feliz)
    servicio.publicarSolicitud(emisor, 1L);

    // Desempatamos usando explícitamente ArgumentMatchers de Mockito
    verify(repoMercadoMock, times(1))
      .guardar(org.mockito.ArgumentMatchers.any(PropuestaIntercambio.class));
  }

  @Test
  public void validarRarezaSoportaCasoPorDefectoComun() throws Exception {
    Carta cartaComun2 = new Carta();
    cartaComun2.setId(7L);
    cartaComun2.setRareza("COMUN"); // Entra en el 'default' del switch (retorna 1)

    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setEstado("ACTIVA");
    propuesta.setUsuarioEmisor(emisor);
    propuesta.setCartaBuscada(cartaComun); // COMÚN (1)

    when(repoMercadoMock.buscarPorId(1L)).thenReturn(propuesta);
    when(repoCartaMock.buscarPorId(7L)).thenReturn(cartaComun2); // COMÚN (1)

    ItemInventario itemR = new ItemInventario();
    itemR.setCarta(cartaComun);
    itemR.setCantidad(2);
    receptor.getInventario().add(itemR);

    ItemInventario itemE = new ItemInventario();
    itemE.setCarta(cartaComun2);
    itemE.setCantidad(2);
    emisor.getInventario().add(itemE);

    // 1 no es mayor que 1, por lo que debe finalizar con éxito
    servicio.finalizarIntercambio(receptor, 1L, 7L);
    assertThat(propuesta.getEstado(), is("FINALIZADA"));
  }

  @Test
  public void buscarItemEnInventarioDevuelveNullSiElUsuarioNoTieneLaCarta() {
    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setEstado("ACTIVA");
    propuesta.setUsuarioEmisor(emisor);
    propuesta.setCartaBuscada(cartaRara);

    when(repoMercadoMock.buscarPorId(1L)).thenReturn(propuesta);

    // El receptor no tiene absolutamente nada en su inventario
    // Esto va a forzar que buscarItemEnInventario devuelva null al inicio de finalizarIntercambio
    Exception ex = assertThrows(
      Exception.class,
      () -> servicio.finalizarIntercambio(receptor, 1L, 1L)
    );
    assertThat(ex.getMessage(), containsString("No tienes la carta solicitada repetida"));
  }

  @Test
  public void actualizarStocksCreaNuevoItemEnDestinoSiElUsuarioNoTeniaLaCarta() throws Exception {
    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setEstado("ACTIVA");
    propuesta.setUsuarioEmisor(emisor);
    propuesta.setCartaBuscada(cartaRara);

    when(repoMercadoMock.buscarPorId(1L)).thenReturn(propuesta);
    when(repoCartaMock.buscarPorId(1L)).thenReturn(cartaComun);

    // El emisor ofrece cartaComun (tiene 2)
    ItemInventario itemE = new ItemInventario();
    itemE.setCarta(cartaComun);
    itemE.setCantidad(2);
    emisor.getInventario().add(itemE);

    // El receptor ofrece cartaRara (tiene 2)
    ItemInventario itemR = new ItemInventario();
    itemR.setCarta(cartaRara);
    itemR.setCantidad(2);
    receptor.getInventario().add(itemR);

    // Forzamos el escenario: El emisor NO tiene la cartaRara en su inventario (destinoEmisor == null)
    // Y el receptor NO tiene la carta Común en su inventario (destinoReceptor == null)
    // Esto obliga a ejecutar los bloques 'if (destinoEmisor == null)' e 'if (destinoReceptor == null)' completos

    servicio.finalizarIntercambio(receptor, 1L, 1L);

    // Verificamos que se crearon los nuevos ItemInventario con cantidad 1
    assertThat(emisor.getInventario(), hasSize(2));
    assertThat(receptor.getInventario(), hasSize(2));
  }

  @Test
  public void actualizarStocksIncrementaCantidadSiElUsuarioYaTeniaLaCarta() throws Exception {
    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setEstado("ACTIVA");
    propuesta.setUsuarioEmisor(emisor);
    propuesta.setCartaBuscada(cartaRara);

    when(repoMercadoMock.buscarPorId(1L)).thenReturn(propuesta);
    when(repoCartaMock.buscarPorId(1L)).thenReturn(cartaComun);

    // El emisor ofrece cartaComun (tiene 2) y YA TIENE la cartaRara que va a recibir (tiene 1)
    ItemInventario itemE1 = new ItemInventario();
    itemE1.setCarta(cartaComun);
    itemE1.setCantidad(2);
    ItemInventario itemE2 = new ItemInventario();
    itemE2.setCarta(cartaRara);
    itemE2.setCantidad(1);
    emisor.getInventario().add(itemE1);
    emisor.getInventario().add(itemE2);

    // El receptor ofrece cartaRara (tiene 2) y YA TIENE la cartaComun que va a recibir (tiene 1)
    ItemInventario itemR1 = new ItemInventario();
    itemR1.setCarta(cartaRara);
    itemR1.setCantidad(2);
    ItemInventario itemR2 = new ItemInventario();
    itemR2.setCarta(cartaComun);
    itemR2.setCantidad(1);
    receptor.getInventario().add(itemR1);
    receptor.getInventario().add(itemR2);

    // Esto obliga a ejecutar los bloques 'else { destinoEmisor.setCantidad(...); }'
    servicio.finalizarIntercambio(receptor, 1L, 1L);

    // El stock existente debió incrementarse a 2
    assertThat(itemE2.getCantidad(), is(2));
    assertThat(itemR2.getCantidad(), is(2));
  }

  @Test
  public void validarRarezasPermiteIntercambioDeMismaRarezaRara() throws Exception {
    Carta rara1 = new Carta();
    rara1.setId(8L);
    rara1.setRareza("RARA");

    Carta rara2 = new Carta();
    rara2.setId(9L);
    rara2.setRareza("RARA");

    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setEstado("ACTIVA");
    propuesta.setUsuarioEmisor(emisor);
    propuesta.setCartaBuscada(rara1);

    when(repoMercadoMock.buscarPorId(1L)).thenReturn(propuesta);
    when(repoCartaMock.buscarPorId(9L)).thenReturn(rara2);

    ItemInventario itemR = new ItemInventario();
    itemR.setCarta(rara1);
    itemR.setCantidad(2);
    receptor.getInventario().add(itemR);

    ItemInventario itemE = new ItemInventario();
    itemE.setCarta(rara2);
    itemE.setCantidad(2);
    emisor.getInventario().add(itemE);

    servicio.finalizarIntercambio(receptor, 1L, 9L);
    assertThat(propuesta.getEstado(), is("FINALIZADA"));
  }

  @Test
  public void finalizarIntercambioLanzaErrorSiLaPropuestaNoEstaActiva() {
    PropuestaIntercambio propuestaInactiva = new PropuestaIntercambio();
    propuestaInactiva.setEstado("FINALIZADA"); // Rompe validación básica
    propuestaInactiva.setUsuarioEmisor(emisor);

    when(repoMercadoMock.buscarPorId(1L)).thenReturn(propuestaInactiva);

    Exception ex = assertThrows(
      Exception.class,
      () -> servicio.finalizarIntercambio(receptor, 1L, 1L)
    );
    assertThat(ex.getMessage(), containsString("La propuesta ya no esta disponible"));
  }

  @Test
  public void obtenerOfertasCompatiblesNoDevuelveNadaSiNoTieneLaCartaRepetida() {
    PropuestaIntercambio propuestaAjena = new PropuestaIntercambio();
    propuestaAjena.setEstado("ACTIVA");
    propuestaAjena.setUsuarioEmisor(receptor);
    propuestaAjena.setCartaBuscada(cartaComun);

    when(repoMercadoMock.listarTodasLasActivas()).thenReturn(List.of(propuestaAjena));

    // El emisor tiene la carta pero NO REPETIDA (cantidad = 1)
    ItemInventario item = new ItemInventario();
    item.setCarta(cartaComun);
    item.setCantidad(1);
    emisor.getInventario().add(item);

    List<PropuestaIntercambio> resultado = servicio.obtenerOfertasCompatibles(emisor);
    assertThat(resultado, is(empty()));
  }

  @Test
  public void obtenerOfertasCompatiblesNoDevuelveNadaSiLaCartaNoCoincide() {
    PropuestaIntercambio propuestaAjena = new PropuestaIntercambio();
    propuestaAjena.setEstado("ACTIVA");
    propuestaAjena.setUsuarioEmisor(receptor);
    propuestaAjena.setCartaBuscada(cartaRara); // Busca Rara

    when(repoMercadoMock.listarTodasLasActivas()).thenReturn(List.of(propuestaAjena));

    // El emisor tiene repetida la Común, no la Rara
    ItemInventario item = new ItemInventario();
    item.setCarta(cartaComun);
    item.setCantidad(3);
    emisor.getInventario().add(item);

    List<PropuestaIntercambio> resultado = servicio.obtenerOfertasCompatibles(emisor);
    assertThat(resultado, is(empty()));
  }

  @Test
  public void obtenerCartasFaltantesConInventarioVacioDebeListarTodasLasCartas() {
    // Forzamos el caso donde el usuario no tiene ninguna carta (lista vacía)
    // Esto prueba que el flujo no se rompe y pasa un array vacío al repositorio
    when(repoCartaMock.listarCartasFaltantes(anyList())).thenReturn(List.of(cartaComun, cartaRara));

    List<Carta> faltantes = servicio.obtenerCartasFaltantes(receptor);

    assertThat(faltantes, hasSize(2));
    verify(repoCartaMock, times(1)).listarCartasFaltantes(anyList());
  }

  @Test
  public void finalizarIntercambioLanzaErrorSiElEmisorNoTieneLaCartaOfrecidaEnAbsoluto() {
    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setEstado("ACTIVA");
    propuesta.setUsuarioEmisor(emisor);
    propuesta.setCartaBuscada(cartaRara);

    when(repoMercadoMock.buscarPorId(1L)).thenReturn(propuesta);

    // El receptor tiene la carta solicitada correctamente
    ItemInventario itemR = new ItemInventario();
    itemR.setCarta(cartaRara);
    itemR.setCantidad(2);
    receptor.getInventario().add(itemR);

    // El emisor tiene el inventario COMPLETAMENTE VACÍO (buscarItemEnInventario devolverá null)
    Exception ex = assertThrows(
      Exception.class,
      () -> servicio.finalizarIntercambio(receptor, 1L, 999L)
    );
    assertThat(ex.getMessage(), containsString("El emisor ya no dispone de esa recompensa"));
  }
}
