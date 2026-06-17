package com.tallerwebi.dominio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class ServicioHistorialTest {

  private RepositorioHistorialPartida repositorioHistorialPartida = mock(
    RepositorioHistorialPartida.class
  );
  private ServicioHistorial servicioHistorial = new ServicioHistorialImpl(
    repositorioHistorialPartida
  );

  @Test
  public void deberiaGuardarUnaPartidaEnElHistorial() {
    Usuario usuarioCreado = new Usuario();
    usuarioCreado.setId(1L);

    HistorialPartida historialPartida = new HistorialPartida();
    historialPartida.setUsuario(usuarioCreado);
    historialPartida.setResultado("Victoria");
    historialPartida.setOroGanado(50);
    historialPartida.setExperienciaGanada(100);

    this.servicioHistorial.guardarHistorialPartidaServicio(historialPartida);

    verify(this.repositorioHistorialPartida).guardarHistorialPartidaRepositorio(historialPartida);
  }

  @Test
  public void deberiaListarElHistorialDeUnUsuario() {
    Usuario usuarioCreado = new Usuario();
    usuarioCreado.setId(1L);

    List<HistorialPartida> listHistorialPartidas = new ArrayList<>();
    when(this.repositorioHistorialPartida.listarPorUsuario(1L)).thenReturn(listHistorialPartidas);

    assertEquals(listHistorialPartidas, this.servicioHistorial.listarHistorialPorUsuario(1L));
  }

  @Test
  public void deberiaAsignarFechaAlGuardarHistorial() {
    Usuario usuarioCreado = new Usuario();
    usuarioCreado.setId(1L);

    HistorialPartida historialPartida = new HistorialPartida();
    historialPartida.setUsuario(usuarioCreado);
    historialPartida.setResultado("Victoria");
    historialPartida.setOroGanado(50);
    historialPartida.setExperienciaGanada(100);

    this.servicioHistorial.guardarHistorialPartidaServicio(historialPartida);

    assertNotNull(historialPartida.getFecha());
  }

  @Test
  public void noDeberiaGuardarHistorialSiElUsuarioEsNull() {
    HistorialPartida historialPartida = new HistorialPartida();
    historialPartida.setUsuario(null);
    historialPartida.setResultado("Victoria");
    historialPartida.setOroGanado(50);
    historialPartida.setExperienciaGanada(100);

    try {
      this.servicioHistorial.guardarHistorialPartidaServicio(historialPartida);
    } catch (RuntimeException runtimeException) {
      assertEquals("Error, el usuario no existe.", runtimeException.getMessage());
    }
  }
}
