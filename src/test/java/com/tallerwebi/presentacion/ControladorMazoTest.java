package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.ServicioMazo;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorMazoTest {

  @Test
  public void siElMazoSeGuardaExitosamenteDebeRedirigirAlLobby() throws Exception {
    ServicioMazo servicioMock = mock(ServicioMazo.class);
    ControladorMazo controlador = new ControladorMazo(servicioMock);
    List<Long> cartasIds = new ArrayList<>();

    for (long i = 1; i <= 15; i++) {
      cartasIds.add(i);
    }

    ModelAndView mav = controlador.guardarMazo(cartasIds);

    // Then
    assertThat(mav.getViewName(), is("redirect:/lobby"));
    verify(servicioMock, times(1)).validarYGuardarMazo(any());
  }

  @Test
  public void siElServicioLanzaExcepcionDebeVolverADeckbuildingConError() throws Exception {
    // Given
    ServicioMazo servicioMock = mock(ServicioMazo.class);
    doThrow(new Exception("Error de validación")).when(servicioMock).validarYGuardarMazo(any());
    ControladorMazo controlador = new ControladorMazo(servicioMock);

    // When
    List<Long> cartasIds = new ArrayList<>();

    for (long i = 1; i <= 15; i++) {
      cartasIds.add(i);
    }

    ModelAndView mav = controlador.guardarMazo(cartasIds);

    // Then
    assertThat(mav.getViewName(), is("deckbuilding"));
    assertThat(mav.getModel().get("error").toString(), containsString("Error de validación"));
  }
}
