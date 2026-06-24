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
  private RepositorioMercado repositorioMercadoMock;
  private ControladorMercado controlador;
  private HttpSession sessionMock;
  private Usuario usuarioFake;

  @BeforeEach
  public void setUp() {
    servicioMercadoMock = mock(ServicioMercado.class);
    repositorioMercadoMock = mock(RepositorioMercado.class);
    controlador = new ControladorMercado(servicioMercadoMock, repositorioMercadoMock);
    sessionMock = mock(HttpSession.class);

    usuarioFake = new Usuario();
    usuarioFake.setId(1L);
    usuarioFake.setEmail("alpha@mythicstack.com");
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
    when(repositorioMercadoMock.buscarUsuarioPorId(1L)).thenReturn(usuarioFake);
    when(servicioMercadoMock.obtenerOfertasCompatibles(usuarioFake)).thenReturn(new ArrayList<>());

    ModelAndView mav = controlador.verMercadoComunidad(sessionMock);

    assertThat(mav.getViewName(), is("intercambio"));
    assertThat(mav.getModel().get("ofertas"), notNullValue());
  }

  @Test
  public void publicarTradeExitosoRedirecciona() throws Exception {
    when(sessionMock.getAttribute("jugadorId")).thenReturn(1L);
    when(repositorioMercadoMock.buscarUsuarioPorId(1L)).thenReturn(usuarioFake);

    ModelAndView mav = controlador.procesarPublicarTrade(5L, sessionMock);

    assertThat(mav.getViewName(), is("redirect:/mercado/mis-trades"));
    verify(servicioMercadoMock, times(1)).publicarSolicitud(usuarioFake, 5L);
  }

  @Test
  public void fallaPublicacionRecargaVistaConError() throws Exception {
    when(sessionMock.getAttribute("jugadorId")).thenReturn(1L);
    when(repositorioMercadoMock.buscarUsuarioPorId(1L)).thenReturn(usuarioFake);

    doThrow(new Exception("Ya tienes una solicitud activa"))
      .when(servicioMercadoMock)
      .publicarSolicitud(usuarioFake, 5L);

    ModelAndView mav = controlador.procesarPublicarTrade(5L, sessionMock);

    assertThat(mav.getViewName(), is("publicar-trade"));
    assertThat(mav.getModel().get("error"), is("Ya tienes una solicitud activa"));
  }

  @Test
  public void siElUsuarioNoEstaLogueadoAlPublicarTradeDebeRedirigirAlLogin() {
    when(sessionMock.getAttribute("jugadorId")).thenReturn(null);

    ModelAndView mav = controlador.procesarPublicarTrade(5L, sessionMock);

    assertThat(mav.getViewName(), is("redirect:/login"));
  }
}
