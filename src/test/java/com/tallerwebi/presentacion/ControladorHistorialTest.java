package com.tallerwebi.presentacion;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.*;
import java.util.ArrayList;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorHistorialTest {

  private RepositorioHistorialPartida repositorioHistorialPartida = mock(
    RepositorioHistorialPartida.class
  );
  private ServicioHistorial servicioHistorial = new ServicioHistorialImpl(
    repositorioHistorialPartida
  );

  @Test
  public void deberiaMostrarVistaHistorial() {
    ControladorHistorialPartida controladorHistorialPartida = new ControladorHistorialPartida(
      this.servicioHistorial
    );

    ArrayList<HistorialPartida> lista = new ArrayList<>();

    when(this.repositorioHistorialPartida.listarPorUsuario(1L)).thenReturn(lista);

    ModelAndView modelAndView = controladorHistorialPartida.mostrarHistorialPartida(1L);

    assertEquals("historial", modelAndView.getViewName());
  }
}
