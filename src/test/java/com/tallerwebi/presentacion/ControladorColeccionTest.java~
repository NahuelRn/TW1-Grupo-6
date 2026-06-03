package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.RepositorioInventario;
import com.tallerwebi.dominio.ServicioCarta;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorColeccionTest {

  private ControladorColeccion controladorColeccion;
  private ServicioCarta servicioCarta;
  private RepositorioInventario repositorioInventario;
  private HttpServletRequest request;
  private HttpSession session;

  @BeforeEach
  public void init() {
    servicioCarta = mock(ServicioCarta.class);
    repositorioInventario = mock(RepositorioInventario.class);
    request = mock(HttpServletRequest.class);
    session = mock(HttpSession.class);
    controladorColeccion = new ControladorColeccion(servicioCarta, repositorioInventario);
  }

  @Test
  public void alIrAColeccionConSesionSeCarganLasCartasEnElModelo() {
    when(request.getSession()).thenReturn(session);
    when(session.getAttribute("JUGADOR_ID")).thenReturn(1L);
    when(servicioCarta.obtenerTodas()).thenReturn(new ArrayList<>());
    when(repositorioInventario.listarInventarioDeJugador(1L)).thenReturn(new ArrayList<>());

    ModelAndView mav = controladorColeccion.verColeccion(request);

    assertThat(mav.getViewName(), equalTo("coleccion"));
    assertThat(mav.getModel().containsKey("todasLasCartas"), is(true));
  }

  @Test
  public void alIrAColeccionSinSesionDebeRedirigirALogin() {
    when(request.getSession()).thenReturn(session);
    when(session.getAttribute("JUGADOR_ID")).thenReturn(null);

    ModelAndView mav = controladorColeccion.verColeccion(request);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
  }
}
