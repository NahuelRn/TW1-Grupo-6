package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.ItemInventario;
import com.tallerwebi.dominio.ServicioIntercambio;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorIntercambioTest {

  private ServicioIntercambio servicioIntercambioMock;
  private HttpSession sessionMock;
  private ControladorIntercambio controlador;

  @BeforeEach
  public void setUp() {
    servicioIntercambioMock = mock(ServicioIntercambio.class);
    sessionMock = mock(HttpSession.class);
    controlador = new ControladorIntercambio(servicioIntercambioMock);
  }

  // ─── GET /contrato-mejora ─────────────────────────────────────────────────

  @Test
  public void siNoHayJugadorEnSesionElGetDebeRedirigirALogin() {
    when(sessionMock.getAttribute("jugadorId")).thenReturn(null);

    ModelAndView mav = controlador.irAIntercambio(sessionMock);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  public void siHayJugadorEnSesionElGetDebeMostrarLaVistaConInventario() {
    Long jugadorId = 1L;
    List<ItemInventario> inventario = List.of(new ItemInventario(), new ItemInventario());

    when(sessionMock.getAttribute("jugadorId")).thenReturn(jugadorId);
    when(servicioIntercambioMock.obtenerInventario(jugadorId)).thenReturn(inventario);

    ModelAndView mav = controlador.irAIntercambio(sessionMock);

    assertThat(mav.getViewName(), equalTo("contrato-mejora"));
    assertThat(mav.getModel().get("inventario"), equalTo(inventario));
  }

  //POST /contrato-mejora/intercambiar

  @Test
  public void siNoHayJugadorEnSesionElPostDebeRedirigirALogin() {
    when(sessionMock.getAttribute("jugadorId")).thenReturn(null);

    ModelAndView mav = controlador.intercambiar(List.of(1L, 2L, 3L, 4L), sessionMock);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  public void siElIntercambioEsExitosoDebeRedirigirALobby() throws Exception {
    Long jugadorId = 1L;
    List<Long> ids = List.of(1L, 2L, 3L, 4L);

    when(sessionMock.getAttribute("jugadorId")).thenReturn(jugadorId);
    // realizarMejora retorna Carta, mockeamos que devuelva una carta cualquiera
    when(servicioIntercambioMock.realizarMejora(ids))
      .thenReturn(new com.tallerwebi.dominio.Carta());

    ModelAndView mav = controlador.intercambiar(ids, sessionMock);

    assertThat(mav.getViewName(), equalTo("redirect:/lobby"));
  }

  @Test
  public void siElIntercambioFallaDebeMostrarErrorEnLaVista() throws Exception {
    Long jugadorId = 1L;
    List<Long> ids = List.of(1L, 2L, 3L, 4L);
    List<ItemInventario> inventario = List.of(new ItemInventario());
    String mensajeError = "Debes entregar exactamente 4 cartas";

    when(sessionMock.getAttribute("jugadorId")).thenReturn(jugadorId);
    when(servicioIntercambioMock.realizarMejora(ids)).thenThrow(new Exception(mensajeError));
    when(servicioIntercambioMock.obtenerInventario(jugadorId)).thenReturn(inventario);

    ModelAndView mav = controlador.intercambiar(ids, sessionMock);

    assertThat(mav.getViewName(), equalTo("contrato-mejora"));
    assertThat(mav.getModel().get("error").toString(), equalTo(mensajeError));
    assertThat(mav.getModel().get("inventario"), equalTo(inventario));
  }
}
