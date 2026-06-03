package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.ServicioMazo;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorMazoTest {

  private ServicioMazo servicioMock;
  private HttpSession sessionMock;
  private ControladorMazo controlador;

  @BeforeEach
  public void init() {
    servicioMock = mock(ServicioMazo.class);
    sessionMock = mock(HttpSession.class);
    controlador = new ControladorMazo(servicioMock);

    // Simulamos que siempre hay un jugador logueado con ID 1
    when(sessionMock.getAttribute("jugadorId")).thenReturn(1L);
  }

  @Test
  public void siElMazoSeGuardaExitosamenteDebeRedirigirASeleccionZona() throws Exception {
    // Given
    List<Long> cartasIds = new ArrayList<>();
    for (long i = 1; i <= 15; i++) {
      cartasIds.add(i);
    }

    // When
    ModelAndView modelAndView = controlador.guardarMazo(cartasIds, sessionMock);

    // Then
    assertThat(modelAndView.getViewName(), is("redirect:/seleccion-zona"));
    verify(servicioMock, times(1)).validarYGuardarMazo(any());
  }

  @Test
  public void siElServicioLanzaExcepcionDebeVolverADeckbuildingConError() throws Exception {
    // Given
    doThrow(new Exception("Error de validación")).when(servicioMock).validarYGuardarMazo(any());

    List<Long> cartasIds = new ArrayList<>();
    for (long i = 1; i <= 15; i++) {
      cartasIds.add(i);
    }

    // When
    ModelAndView modelAndView = controlador.guardarMazo(cartasIds, sessionMock);

    // Then
    assertThat(modelAndView.getViewName(), is("deckbuilding"));
    assertThat(
      modelAndView.getModel().get("error").toString(),
      containsString("Error de validación")
    );
    verify(servicioMock, times(1)).obtenerInventarioPorJugador(1L);
  }
}
