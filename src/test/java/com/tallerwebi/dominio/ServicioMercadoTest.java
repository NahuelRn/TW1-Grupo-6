package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

public class ServicioMercadoTest {

  private RepositorioMercado repoMercadoMock;
  private ServicioMercado servicio;

  private Usuario emisor;
  private Usuario receptor;
  private Carta cartaComun;
  private Carta cartaRara;

  @BeforeEach
  public void setUp() {
    repoMercadoMock = mock(RepositorioMercado.class);
    servicio = new ServicioMercadoImpl(repoMercadoMock);

    // Armamos la infraestructura básica de datos reales
    cartaComun = new Carta();
    cartaComun.setId(1L);
    cartaComun.setRareza("Comun");
    cartaComun.setNombre("Orco Básico");

    cartaRara = new Carta();
    cartaRara.setId(2L);
    cartaRara.setRareza("Rara");
    cartaRara.setNombre("Dragón de Fuego");

    // Configuración Emisor
    emisor = new Usuario();
    emisor.setId(10L);

    Jugador jugadorEmisor = new Jugador();
    jugadorEmisor.setUsuario(emisor);
    emisor.setJugador(jugadorEmisor);

    // Configuración Receptor
    receptor = new Usuario();
    receptor.setId(20L);
    Jugador jugadorReceptor = new Jugador();
    jugadorReceptor.setUsuario(receptor);
    receptor.setJugador(jugadorReceptor);
  }

  //  Tests Publicar Oferta

  @Test
  public void siElUsuarioNoTieneLaCartaEnSuInventarioAlPublicarDebeLanzarError() {
    Exception ex = assertThrows(
      Exception.class,
      () -> servicio.publicarOferta(emisor, cartaComun, "Rara")
    );
    assertThat(ex.getMessage(), containsString("No posees esta carta"));
  }

  @Test
  public void siElUsuarioTieneLaCartaPeroNoEstaRepetidaDebeLanzarError() {
    ItemInventario item = new ItemInventario();
    item.setCarta(cartaComun);
    item.setCantidad(1);
    emisor.getInventario().add(item);

    Exception ex = assertThrows(
      Exception.class,
      () -> servicio.publicarOferta(emisor, cartaComun, "Rara")
    );
    assertThat(ex.getMessage(), containsString("Solo puedes tradear cartas que tengas repetidas"));
  }

  @Test
  public void siLaCartaEstaRepetidaDebeGuardarLaPropuestaExitosamente() throws Exception {
    ItemInventario item = new ItemInventario();
    item.setCarta(cartaComun);
    item.setCantidad(3);
    emisor.getInventario().add(item);

    servicio.publicarOferta(emisor, cartaComun, "Rara");

    verify(repoMercadoMock, times(1)).guardar(ArgumentMatchers.any(PropuestaIntercambio.class));
  }

  //  Tests Aceptar Oferta

  @Test
  public void siLaPropuestaNoExisteAlAceptarDebeLanzarError() {
    when(repoMercadoMock.buscarPorId(99L)).thenReturn(null);

    Exception ex = assertThrows(Exception.class, () -> servicio.aceptarOferta(receptor, 99L));
    assertThat(ex.getMessage(), containsString("ya no está disponible"));
  }

  @Test
  public void siElReceptorNoTieneLaRarezaBuscadaDebeLanzarError() {
    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setId(1L);
    propuesta.setUsuarioEmisor(emisor);
    propuesta.setCartaOfrecida(cartaComun);
    propuesta.setRarezaBuscada("Rara");

    when(repoMercadoMock.buscarPorId(1L)).thenReturn(propuesta);

    ItemInventario itemReceptor = new ItemInventario();
    itemReceptor.setCarta(cartaComun);
    itemReceptor.setCantidad(1);
    receptor.getInventario().add(itemReceptor);

    Exception ex = assertThrows(Exception.class, () -> servicio.aceptarOferta(receptor, 1L));
    assertThat(ex.getMessage(), containsString("No tienes ninguna carta de rareza 'Rara'"));
  }

  @Test
  public void siElTradeEsValidoDebeIntercambiarStocksYBorrarLaPropuesta() throws Exception {
    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setId(1L);
    propuesta.setUsuarioEmisor(emisor);
    propuesta.setCartaOfrecida(cartaComun);
    propuesta.setRarezaBuscada("Rara");

    when(repoMercadoMock.buscarPorId(1L)).thenReturn(propuesta);

    ItemInventario itemEmisor = new ItemInventario();
    itemEmisor.setCarta(cartaComun);
    itemEmisor.setCantidad(2);
    emisor.getInventario().add(itemEmisor);

    ItemInventario itemReceptor = new ItemInventario();
    itemReceptor.setCarta(cartaRara);
    itemReceptor.setCantidad(1);
    receptor.getInventario().add(itemReceptor);

    servicio.aceptarOferta(receptor, 1L);

    assertThat(itemEmisor.getCantidad(), is(1));
    assertThat(itemReceptor.getCantidad(), is(0));

    verify(repoMercadoMock, times(1)).eliminar(propuesta);
  }

  // CORREGIDO: Cambiado a Set para solucionar el WrongTypeOfReturnValue
  @Test
  public void siElUsuarioCreaUnaPropuestaValidaSeDebeGuardarEnElRepositorio() throws Exception {
    Usuario usuario = mock(Usuario.class);
    Set<ItemInventario> inventarioSimulado = new LinkedHashSet<>();

    Carta carta = new Carta();
    carta.setId(10L);

    ItemInventario item = new ItemInventario();
    item.setCarta(carta);
    item.setCantidad(2);
    inventarioSimulado.add(item);

    doReturn(inventarioSimulado).when(usuario).getInventario();

    servicio.crearPropuesta(usuario, 10L, "EPICA");

    verify(repoMercadoMock, times(1)).guardar(ArgumentMatchers.any(PropuestaIntercambio.class));
  }

  @Test
  public void obtenerCartasRepetidasDebeFiltrarSoloLasQueTenganCantidadMayorAUno() {
    Usuario usuario = mock(Usuario.class);
    Set<ItemInventario> inventarioSimulado = new LinkedHashSet<>();

    ItemInventario unica = new ItemInventario();
    unica.setCarta(cartaComun);
    unica.setCantidad(1);

    ItemInventario repetida = new ItemInventario();
    repetida.setCarta(cartaRara);
    repetida.setCantidad(3);

    inventarioSimulado.add(unica);
    inventarioSimulado.add(repetida);

    doReturn(inventarioSimulado).when(usuario).getInventario();

    List<ItemInventario> resultado = servicio.obtenerCartasRepetidas(usuario);

    assertThat(resultado, hasSize(1));
    assertThat(resultado.get(0).getCarta().getId(), is(2L));
  }
}
