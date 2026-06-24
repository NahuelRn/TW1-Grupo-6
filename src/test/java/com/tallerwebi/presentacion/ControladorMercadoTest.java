package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.*;
import java.util.ArrayList;
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
    // Cambiado para coincidir con el constructor real de producción que solo usa el Servicio
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
    when(servicioMercadoMock.obtenerOfertasCompatibles(1L)).thenReturn(new ArrayList<>());
    when(servicioMercadoMock.obtenerCartasFaltantes(1L)).thenReturn(new ArrayList<>());
    when(servicioMercadoMock.obtenerMisTrades(1L)).thenReturn(new ArrayList<>());

    ModelAndView mav = controlador.verMercadoComunidad(sessionMock);

    assertThat(mav.getViewName(), is("intercambio"));
    assertThat(mav.getModel().get("ofertas"), notNullValue());
  }

  @Test
  public void publicarTradeExitosoRedirecciona() throws Exception {
    when(sessionMock.getAttribute("jugadorId")).thenReturn(1L);

    ModelAndView mav = controlador.procesarPublicarTrade(5L, sessionMock);

    assertThat(mav.getViewName(), is("redirect:/mercado/mis-trades"));
    verify(servicioMercadoMock, times(1)).publicarSolicitud(1L, 5L);
  }

  @Test
  public void fallaPublicacionRecargaVistaConError() throws Exception {
    when(sessionMock.getAttribute("jugadorId")).thenReturn(1L);

    doThrow(new Exception("Ya tienes una solicitud activa"))
      .when(servicioMercadoMock)
      .publicarSolicitud(1L, 5L);

    ModelAndView mav = controlador.procesarPublicarTrade(5L, sessionMock);

    assertThat(mav.getViewName(), is("intercambio")); // Nombre de vista real actualizado
    assertThat(mav.getModel().get("error"), is("Ya tienes una solicitud activa"));
  }

  @Test
  public void siElUsuarioNoEstaLogueadoAlPublicarTradeDebeRedirigirAlLogin() {
    when(sessionMock.getAttribute("jugadorId")).thenReturn(null);

    ModelAndView mav = controlador.procesarPublicarTrade(5L, sessionMock);

    assertThat(mav.getViewName(), is("redirect:/login"));
  }
}
