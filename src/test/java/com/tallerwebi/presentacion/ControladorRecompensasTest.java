package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Partida;
import com.tallerwebi.dominio.RecompensaDTO;
import com.tallerwebi.dominio.ServicioCalculoRecompensa;
import com.tallerwebi.dominio.ServicioCombate;
import com.tallerwebi.dominio.ServicioUsuario;
import com.tallerwebi.dominio.Usuario;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.ModelAndView;

public class ControladorRecompensasTest {

  private ServicioUsuario servicioUsuarioMock;
  private ServicioCalculoRecompensa servicioCalculoRecompensaMock;
  private ServicioCombate servicioCombateMock;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;
  private ControladorRecompensas controlador;

  @BeforeEach
  public void init() {
    servicioUsuarioMock = mock(ServicioUsuario.class);
    servicioCalculoRecompensaMock = mock(ServicioCalculoRecompensa.class);
    servicioCombateMock = mock(ServicioCombate.class);
    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);

    when(requestMock.getSession()).thenReturn(sessionMock);

    controlador = new ControladorRecompensas();
    ReflectionTestUtils.setField(controlador, "servicioUsuario", servicioUsuarioMock);
    ReflectionTestUtils.setField(
      controlador,
      "servicioCalculoRecompensa",
      servicioCalculoRecompensaMock
    );
    ReflectionTestUtils.setField(controlador, "servicioCombate", servicioCombateMock);
  }

  @Test
  public void siNoHayPartidaActualEnSesionDebeRedirigirACombate() {
    when(sessionMock.getAttribute("PARTIDA_ACTUAL")).thenReturn(null);

    ModelAndView mav = controlador.recompensas(requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/combate"));
  }

  @Test
  public void siHayPartidaActualDebeMostrarLaVistaDeRecompensasConElDto() {
    Partida partida = new Partida();
    when(sessionMock.getAttribute("PARTIDA_ACTUAL")).thenReturn(partida);

    RecompensaDTO recompensaDTO = new RecompensaDTO();
    when(servicioCalculoRecompensaMock.obtenerRecompensa(partida)).thenReturn(recompensaDTO);

    ModelAndView mav = controlador.recompensas(requestMock);

    assertThat(mav.getViewName(), equalTo("recompensas"));
    assertThat(mav.getModel().get("recompensa"), equalTo(recompensaDTO));
  }

  @Test
  public void siNoHayUsuarioEnSesionAlReclamarDebeRedirigirALogin() {
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(null);

    ModelAndView mav = controlador.reclamar(requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  public void siNoHayPartidaActivaEnSesionAlReclamarDebeRedirigirALobby() {
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(1L);
    when(sessionMock.getAttribute("idPartidaActiva")).thenReturn(null);

    ModelAndView mav = controlador.reclamar(requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/lobby"));
  }

  @Test
  public void siHayUsuarioYPartidaDebeAplicarLaRecompensaYVolverAlLobby() {
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(1L);
    when(sessionMock.getAttribute("idPartidaActiva")).thenReturn(5L);

    Usuario usuario = new Usuario();
    Partida partida = new Partida();
    when(servicioUsuarioMock.buscarPorId(1L)).thenReturn(usuario);
    when(servicioCombateMock.obtenerPartidaPorIdentificador(5L)).thenReturn(partida);

    ModelAndView mav = controlador.reclamar(requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/lobby"));
    verify(servicioUsuarioMock, times(1)).aplicarRecompensa(usuario, partida);
    verify(sessionMock, times(1)).setAttribute("USUARIO", usuario);
    verify(sessionMock, times(1)).removeAttribute("idPartidaActiva");
  }
}
