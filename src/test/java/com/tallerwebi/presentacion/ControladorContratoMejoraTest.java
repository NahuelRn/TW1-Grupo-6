package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Carta;
import com.tallerwebi.dominio.ItemInventario;
import com.tallerwebi.dominio.ServicioIntercambio;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class ControladorContratoMejoraTest {

  private ServicioIntercambio servicioIntercambioMock;
  private HttpSession sessionMock;
  private RedirectAttributes redirectAttributesMock;
  private ControladorContratoMejora controlador; // CORRECCIÓN: Apunta al controlador correcto

  @BeforeEach
  public void setUp() {
    servicioIntercambioMock = mock(ServicioIntercambio.class);
    sessionMock = mock(HttpSession.class);
    redirectAttributesMock = mock(RedirectAttributes.class);
    controlador = new ControladorContratoMejora(servicioIntercambioMock); // CORRECCIÓN: Instancia el controlador unificado
  }

  // ─── GET /contrato-mejora ─────────────────────────────────────────────────

  @Test
  public void siNoHayJugadorEnSesionElGetDebeRedirigirALogin() {
    when(sessionMock.getAttribute("jugadorId")).thenReturn(null);

    ModelAndView mav = controlador.verContratoMejora(sessionMock);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  public void siHayJugadorEnSesionElGetDebeMostrarLaVistaConInventario() {
    Long jugadorId = 1L;
    List<ItemInventario> inventario = List.of(new ItemInventario(), new ItemInventario());

    when(sessionMock.getAttribute("jugadorId")).thenReturn(jugadorId);
    when(servicioIntercambioMock.obtenerInventario(jugadorId)).thenReturn(inventario);

    ModelAndView mav = controlador.verContratoMejora(sessionMock);

    assertThat(mav.getViewName(), equalTo("contrato-mejora"));
    assertThat(mav.getModel().get("inventario"), equalTo(inventario));
  }

  // ─── POST /contrato-mejora/intercambiar ───────────────────────────────────

  @Test
  public void siNoHayJugadorEnSesionElPostDebeRedirigirALogin() {
    when(sessionMock.getAttribute("jugadorId")).thenReturn(null);

    ModelAndView mav = controlador.intercambiarCartas(
      List.of(1L, 2L, 3L, 4L),
      sessionMock,
      redirectAttributesMock
    );

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  public void siElIntercambioEsExitosoDebeRedirigirAResultadoMejora() throws Exception {
    Long jugadorId = 1L;
    List<Long> ids = List.of(1L, 2L, 3L, 4L);
    Carta cartaPremioMock = new Carta();

    when(sessionMock.getAttribute("jugadorId")).thenReturn(jugadorId);
    when(servicioIntercambioMock.realizarMejora(jugadorId, ids)).thenReturn(cartaPremioMock);

    ModelAndView mav = controlador.intercambiarCartas(ids, sessionMock, redirectAttributesMock);

    assertThat(mav.getViewName(), equalTo("redirect:/contrato-mejora/resultado"));
    verify(redirectAttributesMock, times(1)).addFlashAttribute("cartaPremio", cartaPremioMock);
  }

  @Test
  public void siElIntercambioFallaPorExcepcionDebeRedirigirAContratoMejoraConFlashAttribute()
    throws Exception {
    Long jugadorId = 1L;
    List<Long> ids = List.of(1L, 2L, 3L, 4L);
    String mensajeError = "Todas las cartas deben ser de la misma rareza";

    when(sessionMock.getAttribute("jugadorId")).thenReturn(jugadorId);
    // Simulamos que el servicio lanza la excepción esperada
    when(servicioIntercambioMock.realizarMejora(jugadorId, ids))
      .thenThrow(new Exception(mensajeError));

    ModelAndView mav = controlador.intercambiarCartas(ids, sessionMock, redirectAttributesMock);

    // CORRECCIÓN: Ahora el controlador redirige en vez de retornar la vista directa
    assertThat(mav.getViewName(), equalTo("redirect:/contrato-mejora"));
    verify(redirectAttributesMock, times(1)).addFlashAttribute("error", mensajeError);
  }

  @Test
  public void siNoSeSeleccionanCartasDebeRedirigirAContratoMejoraYMostrarError() {
    Long jugadorId = 1L;
    when(sessionMock.getAttribute("jugadorId")).thenReturn(jugadorId);

    // Ejecutamos pasando la lista vacía o nula
    ModelAndView mav = controlador.intercambiarCartas(null, sessionMock, redirectAttributesMock);

    assertThat(mav.getViewName(), equalTo("redirect:/contrato-mejora"));
    verify(redirectAttributesMock, times(1))
      .addFlashAttribute(
        "error",
        "Debes seleccionar exactamente 4 cartas para firmar el contrato."
      );
  }

  // ─── GET /contrato-mejora/resultado ───────────────────────────────────────

  @Test
  public void siNoHayJugadorEnSesionAlVerResultadoDebeRedirigirALogin() {
    when(sessionMock.getAttribute("jugadorId")).thenReturn(null);

    ModelAndView mav = controlador.verResultado(sessionMock);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  public void siHayJugadorEnSesionAlVerResultadoDebeRetornarVistaResultadoMejora() {
    when(sessionMock.getAttribute("jugadorId")).thenReturn(1L);

    ModelAndView mav = controlador.verResultado(sessionMock);

    assertThat(mav.getViewName(), equalTo("resultado-mejora"));
  }
}
