package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Carta;
import com.tallerwebi.dominio.PropuestaIntercambio;
import com.tallerwebi.dominio.ServicioMercado;
import com.tallerwebi.dominio.Usuario;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorMercadoTest {

  private ServicioMercado servicioMercadoMock;
  private ControladorMercado controlador;
  private HttpSession sessionMock;

  private static final String ATTRIBUTE_JUGADOR_ID = "jugadorId";
  private static final Long JUGADOR_ID_TEST = 1L;
  private static final Long TRADE_ID_TEST = 10L;
  private static final Long CARTA_ID_TEST = 5L;

  @BeforeEach
  public void setUp() {
    servicioMercadoMock = mock(ServicioMercado.class);
    controlador = new ControladorMercado(servicioMercadoMock);
    sessionMock = mock(HttpSession.class);
  }

  // --- SESIÓN NULL ---

  @Test
  public void verMercadoComunidadSinSesionRedirecciona() {
    when(sessionMock.getAttribute(ATTRIBUTE_JUGADOR_ID)).thenReturn(null);
    assertThat(controlador.verMercadoComunidad(sessionMock).getViewName(), is("redirect:/login"));
  }

  @Test
  public void procesarPublicarTradeSinSesionRedirecciona() {
    when(sessionMock.getAttribute(ATTRIBUTE_JUGADOR_ID)).thenReturn(null);
    assertThat(
      controlador.procesarPublicarTrade(CARTA_ID_TEST, sessionMock).getViewName(),
      is("redirect:/login")
    );
  }

  @Test
  public void verMisTradesSinSesionRedirecciona() {
    when(sessionMock.getAttribute(ATTRIBUTE_JUGADOR_ID)).thenReturn(null);
    assertThat(controlador.verMisTrades(sessionMock).getViewName(), is("redirect:/login"));
  }

  @Test
  public void iniciarAceptarTradeSinSesionRedirecciona() {
    when(sessionMock.getAttribute(ATTRIBUTE_JUGADOR_ID)).thenReturn(null);
    assertThat(
      controlador.iniciarAceptarTrade(TRADE_ID_TEST, sessionMock).getViewName(),
      is("redirect:/login")
    );
  }

  @Test
  public void confirmarTradeSinSesionRedirecciona() {
    when(sessionMock.getAttribute(ATTRIBUTE_JUGADOR_ID)).thenReturn(null);
    assertThat(
      controlador.confirmarTrade(TRADE_ID_TEST, CARTA_ID_TEST, sessionMock).getViewName(),
      is("redirect:/login")
    );
  }

  @Test
  public void eliminarTradeSinSesionRedirecciona() {
    when(sessionMock.getAttribute(ATTRIBUTE_JUGADOR_ID)).thenReturn(null);
    assertThat(
      controlador.eliminarTrade(TRADE_ID_TEST, sessionMock).getViewName(),
      is("redirect:/login")
    );
  }

  @Test
  public void verDetalleTradeSinSesionRedirecciona() {
    when(sessionMock.getAttribute(ATTRIBUTE_JUGADOR_ID)).thenReturn(null);
    assertThat(
      controlador.verDetalleTrade(TRADE_ID_TEST, sessionMock).getViewName(),
      is("redirect:/login")
    );
  }

  // --- FLUJOS EXITOSOS ---

  @Test
  public void siElUsuarioEstaLogueadoDebeCargarOfertasCompatiblesEnVistaMercadoComunidad() {
    when(sessionMock.getAttribute(ATTRIBUTE_JUGADOR_ID)).thenReturn(JUGADOR_ID_TEST);

    Carta carta = new Carta();
    carta.setId(TRADE_ID_TEST);
    PropuestaIntercambio tradeFake = new PropuestaIntercambio();
    tradeFake.setCartaBuscada(carta);

    when(servicioMercadoMock.obtenerOfertasCompatibles(JUGADOR_ID_TEST))
      .thenReturn(List.of(tradeFake));
    when(servicioMercadoMock.obtenerCartasFaltantes(JUGADOR_ID_TEST)).thenReturn(new ArrayList<>());
    when(servicioMercadoMock.obtenerMisTrades(JUGADOR_ID_TEST)).thenReturn(new ArrayList<>());
    when(servicioMercadoMock.usuarioTieneCartaRepetida(JUGADOR_ID_TEST, TRADE_ID_TEST))
      .thenReturn(true);

    ModelAndView mav = controlador.verMercadoComunidad(sessionMock);
    assertThat(mav.getViewName(), is("intercambio"));
    assertThat(mav.getModel().get("ofertas"), notNullValue());
  }

  @Test
  public void publicarTradeExitosoRedirecciona() {
    when(sessionMock.getAttribute(ATTRIBUTE_JUGADOR_ID)).thenReturn(JUGADOR_ID_TEST);
    assertThat(
      controlador.procesarPublicarTrade(CARTA_ID_TEST, sessionMock).getViewName(),
      is("redirect:/mercado/mis-trades")
    );
  }

  @Test
  public void fallaPublicacionRecargaVistaConError() throws Exception {
    when(sessionMock.getAttribute(ATTRIBUTE_JUGADOR_ID)).thenReturn(JUGADOR_ID_TEST);
    doThrow(new RuntimeException("Error simulado"))
      .when(servicioMercadoMock)
      .publicarSolicitud(JUGADOR_ID_TEST, CARTA_ID_TEST);

    ModelAndView mav = controlador.procesarPublicarTrade(CARTA_ID_TEST, sessionMock);
    assertThat(mav.getViewName(), is("intercambio"));
    assertThat(mav.getModel().get("error"), is("Error simulado"));
  }

  @Test
  public void verMisTradesExitoso() {
    when(sessionMock.getAttribute(ATTRIBUTE_JUGADOR_ID)).thenReturn(JUGADOR_ID_TEST);
    when(servicioMercadoMock.obtenerMisTrades(JUGADOR_ID_TEST)).thenReturn(new ArrayList<>());
    assertThat(controlador.verMisTrades(sessionMock).getViewName(), is("mis-trades"));
  }

  @Test
  public void iniciarAceptarTradeExitoso() {
    when(sessionMock.getAttribute(ATTRIBUTE_JUGADOR_ID)).thenReturn(JUGADOR_ID_TEST);
    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    when(servicioMercadoMock.buscarPorId(TRADE_ID_TEST)).thenReturn(propuesta);
    assertThat(
      controlador.iniciarAceptarTrade(TRADE_ID_TEST, sessionMock).getViewName(),
      is("elegir-recompensa")
    );
  }

  @Test
  public void iniciarAceptarTradeFallaYRedireccionaConError() {
    when(sessionMock.getAttribute(ATTRIBUTE_JUGADOR_ID)).thenReturn(JUGADOR_ID_TEST);
    when(servicioMercadoMock.buscarPorId(TRADE_ID_TEST)).thenThrow(new RuntimeException("Fallo"));
    assertThat(
      controlador.iniciarAceptarTrade(TRADE_ID_TEST, sessionMock).getViewName(),
      is("redirect:/mercado?error=Fallo")
    );
  }

  @Test
  public void confirmarTradeExitoso() {
    when(sessionMock.getAttribute(ATTRIBUTE_JUGADOR_ID)).thenReturn(JUGADOR_ID_TEST);
    assertThat(
      controlador.confirmarTrade(TRADE_ID_TEST, CARTA_ID_TEST, sessionMock).getViewName(),
      is("redirect:/mercado")
    );
  }

  @Test
  public void confirmarTradeFallaYRedirecciona() throws Exception {
    when(sessionMock.getAttribute(ATTRIBUTE_JUGADOR_ID)).thenReturn(JUGADOR_ID_TEST);
    doThrow(new RuntimeException("Invalido"))
      .when(servicioMercadoMock)
      .finalizarIntercambio(anyLong(), anyLong(), anyLong());
    assertThat(
      controlador.confirmarTrade(TRADE_ID_TEST, CARTA_ID_TEST, sessionMock).getViewName(),
      is("redirect:/mercado?error=Invalido")
    );
  }

  @Test
  public void eliminarTradeExitoso() {
    when(sessionMock.getAttribute(ATTRIBUTE_JUGADOR_ID)).thenReturn(JUGADOR_ID_TEST);
    assertThat(
      controlador.eliminarTrade(TRADE_ID_TEST, sessionMock).getViewName(),
      is("redirect:/mercado/mis-trades")
    );
  }

  @Test
  public void eliminarTradeFallaYRedirecciona() throws Exception {
    when(sessionMock.getAttribute(ATTRIBUTE_JUGADOR_ID)).thenReturn(JUGADOR_ID_TEST);
    doThrow(new RuntimeException("Denegado"))
      .when(servicioMercadoMock)
      .eliminarMiTrade(anyLong(), anyLong());
    assertThat(
      controlador.eliminarTrade(TRADE_ID_TEST, sessionMock).getViewName(),
      is("redirect:/mercado/mis-trades?error=Denegado")
    );
  }

  // --- DETALLE DE TRADE ---

  /**
   * Cuando buscarPorId lanza excepción, el controlador redirige a mis-trades con error.
   * Alineado con el comportamiento real del controlador (verDetalleTrade usa try/catch
   * y redirige a redirect:/mercado/mis-trades?error=...).
   */
  @Test
  public void verDetalleTradeFallaYRedirecciona() {
    when(sessionMock.getAttribute(ATTRIBUTE_JUGADOR_ID)).thenReturn(JUGADOR_ID_TEST);
    when(servicioMercadoMock.buscarPorId(TRADE_ID_TEST))
      .thenThrow(new RuntimeException("No encontrado"));

    ModelAndView mav = controlador.verDetalleTrade(TRADE_ID_TEST, sessionMock);
    assertThat(mav.getViewName(), is("redirect:/mercado/mis-trades?error=No encontrado"));
  }

  @Test
  public void verDetalleTradeMuestraEmailEmisorCuandoSoyEmisor() {
    when(sessionMock.getAttribute(ATTRIBUTE_JUGADOR_ID)).thenReturn(JUGADOR_ID_TEST);

    Usuario emisor = new Usuario();
    emisor.setEmail("emisor@test.com");

    Usuario receptor = new Usuario();
    receptor.setEmail("receptor@test.com");

    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setUsuarioEmisor(emisor);
    propuesta.setUsuarioReceptor(receptor);

    when(servicioMercadoMock.buscarPorId(TRADE_ID_TEST)).thenReturn(propuesta);

    when(servicioMercadoMock.esEmisor(JUGADOR_ID_TEST, propuesta)).thenReturn(true);

    ModelAndView mav = controlador.verDetalleTrade(TRADE_ID_TEST, sessionMock);

    assertThat(mav.getModel().get("usuarioCoincidencia"), is("receptor@test.com"));
  }

  @Test
  public void verDetalleTradeMuestraEmailEmisorCuandoSoyReceptor() {
    when(sessionMock.getAttribute(ATTRIBUTE_JUGADOR_ID)).thenReturn(JUGADOR_ID_TEST);

    Usuario emisor = new Usuario();
    emisor.setEmail("emisor@test.com");

    Usuario receptor = new Usuario();
    receptor.setEmail("receptor@test.com");

    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setUsuarioEmisor(emisor);
    propuesta.setUsuarioReceptor(receptor);

    when(servicioMercadoMock.buscarPorId(TRADE_ID_TEST)).thenReturn(propuesta);

    when(servicioMercadoMock.esEmisor(JUGADOR_ID_TEST, propuesta)).thenReturn(false);

    ModelAndView mav = controlador.verDetalleTrade(TRADE_ID_TEST, sessionMock);

    assertThat(mav.getModel().get("usuarioCoincidencia"), is("emisor@test.com"));
  }

  @Test
  public void verDetalleTradeCuandoLaPropuestaNoExisteRedireccionaConError() {
    when(sessionMock.getAttribute(ATTRIBUTE_JUGADOR_ID)).thenReturn(JUGADOR_ID_TEST);

    when(servicioMercadoMock.buscarPorId(TRADE_ID_TEST)).thenReturn(null);

    ModelAndView mav = controlador.verDetalleTrade(TRADE_ID_TEST, sessionMock);

    assertThat(mav.getViewName(), is("redirect:/mercado/mis-trades?error=Trade no encontrado"));
  }
}
