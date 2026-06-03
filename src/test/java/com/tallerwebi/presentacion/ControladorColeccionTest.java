package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.ServicioCarta;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorColeccionTest {

  private HttpServletRequest request;
  private HttpSession session;
  private ServicioCarta servicioCarta;
  private ControladorColeccion controladorColeccion;

  @BeforeEach
  public void init() {
    // 1. Solo mockeamos el servicio y las cosas web
    servicioCarta = mock(ServicioCarta.class);
    request = mock(HttpServletRequest.class);
    session = mock(HttpSession.class);

    // 2. Le pasamos UN SOLO parámetro al controlador
    controladorColeccion = new ControladorColeccion(servicioCarta);
  }

  @Test
  public void alIrAColeccionMuestraLaVistaColeccion() {
    // Preparación
    when(request.getSession()).thenReturn(session);
    when(session.getAttribute("jugadorId")).thenReturn(1L);

    // 3. Cuando el controlador pida las cosas, el mock del SERVICIO le responde
    when(servicioCarta.obtenerTodas()).thenReturn(new ArrayList<>());
    when(servicioCarta.obtenerInventario(1L)).thenReturn(new ArrayList<>());

    // Ejecución
    ModelAndView mav = controladorColeccion.verColeccion(request);

    // Validación
    assertThat(mav.getViewName(), equalTo("coleccion"));
    assertThat(mav.getModel().containsKey("todasLasCartas"), equalTo(true));
  }

  @Test
  public void alIrAColeccionSinSesionDebeRedirigirALogin() {
    when(request.getSession()).thenReturn(session);
    when(session.getAttribute("jugadorId")).thenReturn(null);

    ModelAndView mav = controladorColeccion.verColeccion(request);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
  }
}
