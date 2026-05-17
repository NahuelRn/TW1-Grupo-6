package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Partida;
import com.tallerwebi.dominio.ServicioPartida;
import com.tallerwebi.dominio.Usuario;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorPartidaTest {

  private ControladorPartida controladorPartida;
  private ServicioPartida servicioPartidaMock;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;

  @BeforeEach
  public void init() {
    servicioPartidaMock = mock(ServicioPartida.class);
    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);

    when(requestMock.getSession()).thenReturn(sessionMock);

    controladorPartida = new ControladorPartida(servicioPartidaMock);
  }

  @Test
  public void alIrASeleccionarZonaDebeLlevarALaVistaSeleccionZona() {
    ModelAndView mav = controladorPartida.irASeleccionDeZona();

    assertThat(mav.getViewName(), equalToIgnoringCase("seleccion-zona"));
  }

  @Test
  public void alIniciarCombateDebeRedirigirAlHomeYGuardarPartidaEnSesion() {
    Usuario usuarioMock = new Usuario();
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);

    Partida partidaMock = new Partida();
    when(servicioPartidaMock.iniciarPartida(any(Usuario.class), anyString()))
      .thenReturn(partidaMock);

    ModelAndView mav = controladorPartida.iniciarCombate("Bosque Oscuro", requestMock);

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/home"));
    verify(sessionMock, times(1)).setAttribute("PARTIDA_ACTUAL", partidaMock);
  }
}
