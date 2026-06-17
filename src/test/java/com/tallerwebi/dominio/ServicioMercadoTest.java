package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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

  // =========================================================================
  //  Tests Publicar Oferta
  // =========================================================================

  @Test
  public void siElUsuarioNoTieneLaCartaEnSuInventarioAlPublicarDebeLanzarError() {
    // Inventario vacío
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
    item.setCantidad(1); // No está repetida
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
    item.setCantidad(3); // Repetidísima
    emisor.getInventario().add(item);

    servicio.publicarOferta(emisor, cartaComun, "Rara");

    verify(repoMercadoMock, times(1)).guardar(ArgumentMatchers.any(PropuestaIntercambio.class));
  }

  // =========================================================================
  //  Tests Aceptar Oferta
  // =========================================================================

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
    propuesta.setRarezaBuscada("Rara"); // Pide una rara

    when(repoMercadoMock.buscarPorId(1L)).thenReturn(propuesta);

    // El receptor solo tiene una común, no tiene la Rara solicitada
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

    // Stock Inicial Emisor: Ofrece la común (tiene 2)
    ItemInventario itemEmisor = new ItemInventario();
    itemEmisor.setCarta(cartaComun);
    itemEmisor.setCantidad(2);
    emisor.getInventario().add(itemEmisor);

    // Stock Inicial Receptor: Paga con una Rara (tiene 1)
    ItemInventario itemReceptor = new ItemInventario();
    itemReceptor.setCarta(cartaRara);
    itemReceptor.setCantidad(1);
    receptor.getInventario().add(itemReceptor);

    // Ejecución del trade
    servicio.aceptarOferta(receptor, 1L);

    // Verificaciones de stock final
    assertThat(itemEmisor.getCantidad(), is(1)); // Bajó de 2 a 1
    assertThat(itemReceptor.getCantidad(), is(0)); // Bajó de 1 a 0

    // El mercado debe dar de baja la propuesta transaccionada
    verify(repoMercadoMock, times(1)).eliminar(propuesta);
  }
}
