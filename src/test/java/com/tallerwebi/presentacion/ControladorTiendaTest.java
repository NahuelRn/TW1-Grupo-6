package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Carta;
import com.tallerwebi.dominio.RepositorioCarta;
import com.tallerwebi.dominio.ServicioTienda;
import com.tallerwebi.dominio.Usuario;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorTiendaTest {

  private ServicioTienda servicioTiendaMock;
  private RepositorioCarta repositorioCartaMock;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;
  private ControladorTienda controladorTienda;

  @BeforeEach
  public void init() {
    servicioTiendaMock = mock(ServicioTienda.class);
    repositorioCartaMock = mock(RepositorioCarta.class);
    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);

    when(requestMock.getSession()).thenReturn(sessionMock);

    controladorTienda = new ControladorTienda(servicioTiendaMock, repositorioCartaMock);
  }

  @Test
  public void siNoHayUsuarioEnSesionAlVerLaTiendaDebeRedirigirALogin() {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(null);

    ModelAndView mav = controladorTienda.verTienda(requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  public void siHayUsuarioEnSesionDebeMostrarLaTiendaConElListadoDeCartas() {
    Usuario usuario = new Usuario();
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuario);
    when(servicioTiendaMock.listarCartas()).thenReturn(List.of(new Carta()));

    ModelAndView mav = controladorTienda.verTienda(requestMock);

    assertThat(mav.getViewName(), equalTo("tienda"));
    assertThat(mav.getModel().containsKey("cartas"), equalTo(true));
  }

  @Test
  public void siNoHayUsuarioEnSesionAlComprarDebeRedirigirALogin() {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(null);

    ModelAndView mav = controladorTienda.realizarCompra(1L, requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  public void siLaCompraEsExitosaDebeMostrarMensajeDeExitoYActualizarLaSesion() {
    Usuario usuario = new Usuario();
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuario);

    ModelAndView mav = controladorTienda.realizarCompra(1L, requestMock);

    assertThat(mav.getViewName(), equalTo("tienda"));
    assertThat(mav.getModel().get("mensaje").toString(), equalTo("¡Carta comprada con éxito!"));
    verify(sessionMock, times(1)).setAttribute("USUARIO", usuario);
  }

  @Test
  public void siLaCompraFallaDebeMostrarElMensajeDeErrorYNoActualizarLaSesion() {
    Usuario usuario = new Usuario();
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuario);
    doThrow(new RuntimeException("Oro insuficiente"))
      .when(servicioTiendaMock)
      .comprarCarta(usuario, 1L);

    ModelAndView mav = controladorTienda.realizarCompra(1L, requestMock);

    assertThat(mav.getViewName(), equalTo("tienda"));
    assertThat(mav.getModel().get("error").toString(), equalTo("Oro insuficiente"));
    verify(sessionMock, never()).setAttribute(eq("USUARIO"), any());
  }
}
