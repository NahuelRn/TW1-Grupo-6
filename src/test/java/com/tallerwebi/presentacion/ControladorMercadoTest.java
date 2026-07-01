package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.*;
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

  @BeforeEach
  public void setUp() {
    servicioMercadoMock = mock(ServicioMercado.class);
    controlador = new ControladorMercado(servicioMercadoMock);
    sessionMock = mock(HttpSession.class);
  }

  @Test
  public void usuarioNoLogueadoVaAlLogin() {
    when(sessionMock.getAttribute("jugadorId")).thenReturn(null);
    ModelAndView mav = controlador.verMercadoComunidad(sessionMock);
    assertThat(mav.getViewName(), is("redirect:/login"));
  }

  @Test
  public void siElUsuarioEstaLogueadoDebeCargarOfertasCompatiblesEnVistaMercadoComunidad() {
    when(sessionMock.getAttribute("jugadorId")).thenReturn(1L);

    PropuestaIntercambio tradeFake = new PropuestaIntercambio();
    tradeFake.setCartaBuscada(new Carta());

    when(servicioMercadoMock.obtenerOfertasCompatibles(1L)).thenReturn(new ArrayList<>());
    when(servicioMercadoMock.obtenerCartasFaltantes(1L)).thenReturn(new ArrayList<>());
    when(servicioMercadoMock.obtenerMisTrades(1L)).thenReturn(List.of(tradeFake));

    ModelAndView mav = controlador.verMercadoComunidad(sessionMock);

    assertThat(mav.getViewName(), is("intercambio"));
    assertThat(mav.getModel().get("ofertas"), notNullValue());
  }

  @Test
  public void publicarTradeExitosoRedirecciona() throws Exception {
    when(sessionMock.getAttribute("jugadorId")).thenReturn(1L);

    ModelAndView mav = controlador.procesarPublicarTrade(5L, sessionMock);

    assertThat(mav.getViewName(), is("redirect:/mercado/mis-trades"));
  }

  @Test
  public void fallaPublicacionRecargaVistaConError() throws Exception {
    when(sessionMock.getAttribute("jugadorId")).thenReturn(1L);

    doThrow(new RuntimeException("Ya tienes una solicitud activa"))
      .when(servicioMercadoMock)
      .publicarSolicitud(1L, 5L);

    ModelAndView mav = controlador.procesarPublicarTrade(5L, sessionMock);

    assertThat(mav.getViewName(), is("intercambio"));
    assertThat(mav.getModel().get("error"), is("Ya tienes una solicitud activa"));
  }

  @Test
  public void siElUsuarioNoEstaLogueadoAlPublicarTradeDebeRedirigirAlLogin() throws Exception {
    when(sessionMock.getAttribute("jugadorId")).thenReturn(null);

    ModelAndView mav = controlador.procesarPublicarTrade(5L, sessionMock);

    assertThat(mav.getViewName(), is("redirect:/login"));
  }

  @Test
  public void verMisTradesExitosoConInicializacionDeCartas() {
    when(sessionMock.getAttribute("jugadorId")).thenReturn(1L);

    PropuestaIntercambio tradeFake = new PropuestaIntercambio();
    tradeFake.setCartaBuscada(new Carta());
    tradeFake.setCartaOfrecida(new Carta());

    when(servicioMercadoMock.obtenerMisTrades(1L)).thenReturn(List.of(tradeFake));

    ModelAndView mav = controlador.verMisTrades(sessionMock);

    assertThat(mav.getViewName(), is("mis-trades"));
    assertThat(mav.getModel().get("misTrades"), notNullValue());
  }

  @Test
  public void iniciarAceptarTradeExitoso() throws Exception {
    when(sessionMock.getAttribute("jugadorId")).thenReturn(1L);
    PropuestaIntercambio propuesta = new PropuestaIntercambio();

    when(servicioMercadoMock.buscarPorId(10L)).thenReturn(propuesta);
    when(servicioMercadoMock.obtenerOpcionesRecompensa(propuesta)).thenReturn(new ArrayList<>());

    ModelAndView mav = controlador.iniciarAceptarTrade(10L, sessionMock);

    assertThat(mav.getViewName(), is("elegir-recompensa"));
  }

  @Test
  public void iniciarAceptarTradeFallaYRedireccionaConError() throws Exception {
    when(sessionMock.getAttribute("jugadorId")).thenReturn(1L);

    when(servicioMercadoMock.buscarPorId(10L))
      .thenThrow(new RuntimeException("Error de propuesta"));

    ModelAndView mav = controlador.iniciarAceptarTrade(10L, sessionMock);

    assertThat(mav.getViewName(), is("redirect:/mercado?error=Error de propuesta"));
  }

  @Test
  public void confirmarTradeExitoso() throws Exception {
    when(sessionMock.getAttribute("jugadorId")).thenReturn(1L);

    ModelAndView mav = controlador.confirmarTrade(10L, 5L, sessionMock);

    assertThat(mav.getViewName(), is("redirect:/mercado"));
  }

  @Test
  public void confirmarTradeFallaYRedirecciona() throws Exception {
    when(sessionMock.getAttribute("jugadorId")).thenReturn(1L);

    doThrow(new RuntimeException("No tienes la carta"))
      .when(servicioMercadoMock)
      .finalizarIntercambio(1L, 10L, 5L);

    ModelAndView mav = controlador.confirmarTrade(10L, 5L, sessionMock);

    assertThat(mav.getViewName(), is("redirect:/mercado?error=No tienes la carta"));
  }

  @Test
  public void eliminarTradeExitoso() throws Exception {
    when(sessionMock.getAttribute("jugadorId")).thenReturn(1L);

    ModelAndView mav = controlador.eliminarTrade(10L, sessionMock);

    assertThat(mav.getViewName(), is("redirect:/mercado/mis-trades"));
  }

  @Test
  public void eliminarTradeFallaYRedirecciona() throws Exception {
    when(sessionMock.getAttribute("jugadorId")).thenReturn(1L);

    doThrow(new RuntimeException("No se pudo eliminar"))
      .when(servicioMercadoMock)
      .eliminarMiTrade(1L, 10L);

    ModelAndView mav = controlador.eliminarTrade(10L, sessionMock);

    assertThat(mav.getViewName(), is("redirect:/mercado/mis-trades?error=No se pudo eliminar"));
  }

  @Test
  public void verDetalleDeTradeFallaSiEsNuloOActivo() {
    when(sessionMock.getAttribute("jugadorId")).thenReturn(1L);

    PropuestaIntercambio propuestaActiva = new PropuestaIntercambio();
    propuestaActiva.setEstado("ACTIVA");

    when(servicioMercadoMock.buscarPorId(10L)).thenReturn(propuestaActiva);

    ModelAndView mav = controlador.verDetalleTrade(10L, sessionMock);

    assertThat(
      mav.getViewName(),
      is("redirect:/mercado/mis-trades?error=El intercambio no esta finalizado o no existe")
    );
  }

  @Test
  public void verDetalleDeTradeExitoso() {
    when(sessionMock.getAttribute("jugadorId")).thenReturn(1L);

    Usuario emisor = new Usuario();
    emisor.setId(1L);
    emisor.setEmail("emisor@test.com");

    Usuario receptor = new Usuario();
    receptor.setId(2L);
    receptor.setEmail("receptor@test.com");

    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setId(10L);
    propuesta.setEstado("FINALIZADO");
    propuesta.setUsuarioEmisor(emisor);
    propuesta.setUsuarioReceptor(receptor);
    propuesta.setCartaBuscada(new Carta());
    propuesta.setCartaOfrecida(new Carta());

    when(servicioMercadoMock.buscarPorId(10L)).thenReturn(propuesta);

    ModelAndView mav = controlador.verDetalleTrade(10L, sessionMock);

    assertThat(mav.getViewName(), is("mercado-detalle-trade"));
    assertThat(mav.getModel().get("usuarioCoincidencia"), is("receptor@test.com"));
  }

  @Test
  public void verDetalleDeTradeComoReceptor() {
    when(sessionMock.getAttribute("jugadorId")).thenReturn(2L);

    Usuario emisor = new Usuario();
    emisor.setId(1L);
    emisor.setEmail("emisor@test.com");

    Usuario receptor = new Usuario();
    receptor.setId(2L);
    receptor.setEmail("receptor@test.com");

    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setId(10L);
    propuesta.setEstado("FINALIZADO");
    propuesta.setUsuarioEmisor(emisor);
    propuesta.setUsuarioReceptor(receptor);
    propuesta.setCartaBuscada(new Carta());
    propuesta.setCartaOfrecida(new Carta());

    when(servicioMercadoMock.buscarPorId(10L)).thenReturn(propuesta);

    ModelAndView mav = controlador.verDetalleTrade(10L, sessionMock);

    assertThat(mav.getViewName(), is("mercado-detalle-trade"));
    assertThat(mav.getModel().get("usuarioCoincidencia"), is("emisor@test.com"));
  }

  @Test
  public void todosLosMetodosNuevosRedirigenAlLoginSiNoHaySesion() throws Exception {
    when(sessionMock.getAttribute("jugadorId")).thenReturn(null);

    assertThat(controlador.verMisTrades(sessionMock).getViewName(), is("redirect:/login"));
    assertThat(
      controlador.iniciarAceptarTrade(10L, sessionMock).getViewName(),
      is("redirect:/login")
    );
    assertThat(
      controlador.confirmarTrade(10L, 5L, sessionMock).getViewName(),
      is("redirect:/login")
    );
    assertThat(controlador.eliminarTrade(10L, sessionMock).getViewName(), is("redirect:/login"));
    assertThat(controlador.verDetalleTrade(10L, sessionMock).getViewName(), is("redirect:/login"));
  }
}
