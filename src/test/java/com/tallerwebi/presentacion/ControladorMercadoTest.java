package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.PropuestaIntercambio;
import com.tallerwebi.dominio.RepositorioMercado;
import com.tallerwebi.dominio.ServicioMercado;
import com.tallerwebi.dominio.Usuario;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ModelMap;
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
    usuarioFake.setEmail("tester@mythicstack.com");
  }

  @Test
  public void siElUsuarioNoEstaLogueadoAlVerMercadoDebeRedireccionarAlLogin() {
    when(sessionMock.getAttribute("jugadorId")).thenReturn(null);

    ModelAndView mav = controlador.verMercado(sessionMock);

    assertThat(mav.getViewName(), is("redirect:/login"));
  }

  @Test
  public void siElUsuarioEstaLogueadoDebeMostrarLaVistaMercadoConLasOfertas() {
    when(sessionMock.getAttribute("jugadorId")).thenReturn(1L);
    when(repositorioMercadoMock.buscarUsuarioPorId(1L)).thenReturn(usuarioFake);

    List<PropuestaIntercambio> listaSimulada = new ArrayList<>();
    when(servicioMercadoMock.obtenerMercado(usuarioFake)).thenReturn(listaSimulada);

    ModelAndView mav = controlador.verMercado(sessionMock);

    assertThat(mav.getViewName(), is("intercambio")); // Actualizado a intercambio
    assertThat(mav.getModel().get("ofertas"), notNullValue());
  }

  @Test
  public void siElTradeEsExitosoDebeRedireccionarAlMercado() throws Exception {
    when(sessionMock.getAttribute("jugadorId")).thenReturn(1L);
    when(repositorioMercadoMock.buscarUsuarioPorId(1L)).thenReturn(usuarioFake);
    doNothing().when(servicioMercadoMock).aceptarOferta(usuarioFake, 1L);

    ModelAndView mav = controlador.aceptarTrade(1L, sessionMock);

    assertThat(mav.getViewName(), is("redirect:/mercado"));
  }

  @Test
  public void siElServicioLanzaExcepcionAlAceptarDebeRecargarLaVistaConUnMensajeDeError()
    throws Exception {
    when(sessionMock.getAttribute("jugadorId")).thenReturn(1L);
    when(repositorioMercadoMock.buscarUsuarioPorId(1L)).thenReturn(usuarioFake);

    doThrow(new Exception("No tienes la rareza solicitada"))
      .when(servicioMercadoMock)
      .aceptarOferta(usuarioFake, 1L);

    ModelAndView mav = controlador.aceptarTrade(1L, sessionMock);

    assertThat(mav.getViewName(), is("intercambio")); // Actualizado a intercambio
    assertThat(mav.getModel().get("error"), is("No tienes la rareza solicitada"));
    assertThat(mav.getModel().get("ofertas"), notNullValue());
  }

  // CORREGIDO: Se alinearon los mocks con 'servicioMercadoMock' y 'repositorioMercadoMock'
  @Test
  public void alPublicarUnaOfertaExitosaDebeRedireccionarAlMercado() throws Exception {
    when(sessionMock.getAttribute("jugadorId")).thenReturn(1L);
    when(repositorioMercadoMock.buscarUsuarioPorId(1L)).thenReturn(usuarioFake);

    ModelAndView mav = controlador.publicarOferta(10L, "LEGENDARIA", sessionMock);

    assertThat(mav.getViewName(), is("redirect:/mercado"));
    verify(servicioMercadoMock, times(1)).crearPropuesta(usuarioFake, 10L, "LEGENDARIA");
  }
}
