package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.ServicioMazo;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ModelMap;

public class ControladorMazoTest {

  @Test
  public void siElMazoSeGuardaExitosamenteDebeRedirigirASeleccionZona() throws Exception {
    ServicioMazo servicioMock = mock(ServicioMazo.class);
    ControladorMazo controlador = new ControladorMazo(servicioMock);
    List<Long> cartasIds = new ArrayList<>();

    for (long i = 1; i <= 15; i++) {
      cartasIds.add(i);
    }

    ModelMap modelo = new ModelMap();
    String vista = controlador.guardarMazo(cartasIds, modelo);

    assertThat(vista, is("redirect:/seleccion-zona"));
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

    // Instanciamos el ModelMap
    ModelMap modelo = new ModelMap();
    String vista = controlador.guardarMazo(cartasIds, modelo);

    // Then: Validamos contra el String de la vista y sacamos el error directo del ModelMap
    assertThat(vista, is("deckbuilding"));
    assertThat(modelo.get("error").toString(), containsString("Error de validación"));
  }
}
