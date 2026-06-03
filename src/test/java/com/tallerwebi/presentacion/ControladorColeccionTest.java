package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.ColeccionDTO;
import com.tallerwebi.dominio.ServicioCarta;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorColeccionTest {

  private HttpServletRequest request;
  private HttpSession session;
  private ServicioCarta servicioCarta;
  private ColeccionDTO coleccionDTOMock;
  private ControladorColeccion controladorColeccion;

  @BeforeEach
  public void init() {
    servicioCarta = mock(ServicioCarta.class);
    request = mock(HttpServletRequest.class);
    session = mock(HttpSession.class);
    coleccionDTOMock = mock(ColeccionDTO.class);

    controladorColeccion = new ControladorColeccion(servicioCarta);
  }

  @Test
  public void alIrAColeccionMuestraLaVistaColeccion() {
    // Preparación
    when(request.getSession()).thenReturn(session);
    when(request.getSession(anyBoolean())).thenReturn(session);
    when(session.getAttribute("jugadorId")).thenReturn(1L);

    when(servicioCarta.obtenerColeccionAgrupada(1L)).thenReturn(coleccionDTOMock);
    when(coleccionDTOMock.getCartasUnicas()).thenReturn(Collections.emptyList());
    when(coleccionDTOMock.getCantidades()).thenReturn(Collections.emptyMap());

    // Ejecución
    ModelAndView mav = controladorColeccion.verColeccion(request);

    // Validación
    assertThat(mav.getViewName(), equalTo("coleccion"));
    assertThat(mav.getModel().containsKey("todasLasCartas"), equalTo(true));
    assertThat(mav.getModel().containsKey("misCantidades"), equalTo(true));
  }

  @Test
  public void alIrAColeccionSinSesionDebeRedirigirALogin() {
    // Preparación
    when(request.getSession()).thenReturn(session);
    when(request.getSession(anyBoolean())).thenReturn(session);
    when(session.getAttribute("jugadorId")).thenReturn(null);

    // Ejecución
    ModelAndView mav = controladorColeccion.verColeccion(request);

    // Validación
    assertThat(mav.getViewName(), equalTo("redirect:/login"));
  }

  // TEST NUEVO PARA ARRANCARLE PUNTOS A JACOCO: Cubre el flujo de datos nulos/vacíos del DTO
  @Test
  public void alIrAColeccionSiLaColeccionVieneNulaDebeCargarseIgualSinRomper() {
    // Preparación
    when(request.getSession()).thenReturn(session);
    when(request.getSession(anyBoolean())).thenReturn(session);
    when(session.getAttribute("jugadorId")).thenReturn(1L);

    // Simulamos que el servicio devuelve un DTO vacío que responde null
    when(servicioCarta.obtenerColeccionAgrupada(1L)).thenReturn(coleccionDTOMock);
    when(coleccionDTOMock.getCartasUnicas()).thenReturn(null);
    when(coleccionDTOMock.getCantidades()).thenReturn(null);

    // Ejecución
    ModelAndView mav = controladorColeccion.verColeccion(request);

    // Validación
    assertThat(mav.getViewName(), equalTo("coleccion"));
  }
}
