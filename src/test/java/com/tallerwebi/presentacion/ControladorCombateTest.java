package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.*;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorCombateTest {

  private ServicioCombate servicioCombateMock;
  private ServicioPartida servicioPartidaMock;
  private ServicioUsuario servicioUsuarioMock;
  private ControladorCombate controladorCombate;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;

  @BeforeEach
  public void init() {
    servicioCombateMock = mock(ServicioCombate.class);
    servicioPartidaMock = mock(ServicioPartida.class);
    servicioUsuarioMock = mock(ServicioUsuario.class);

    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);

    when(requestMock.getSession()).thenReturn(sessionMock);

    controladorCombate =
      new ControladorCombate(servicioCombateMock, servicioPartidaMock, servicioUsuarioMock);
  }

  @Test
  public void queSePuedaIniciarUnCombate() {
    Long idUsuario = 1L;

    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(idUsuario);

    Usuario usuarioSimulado = new Usuario();
    Mazo mazoSimulado = new Mazo();
    List<Carta> cartas = new ArrayList<>();
    cartas.add(new Carta());
    mazoSimulado.setCartas(cartas);
    usuarioSimulado.setMazoActivo(mazoSimulado);

    when(servicioUsuarioMock.buscarPorId(idUsuario)).thenReturn(usuarioSimulado);

    Partida partidaSimulada = new Partida();
    partidaSimulada.setId(10L);
    when(
      servicioPartidaMock.iniciarPartida(
        org.mockito.Mockito.any(Usuario.class),
        org.mockito.Mockito.anyString()
      )
    )
      .thenReturn(partidaSimulada);

    ModelAndView modelAndView = controladorCombate.iniciarCombate("bosque", requestMock);

    assertThat(modelAndView.getViewName(), equalTo("combate"));
    assertThat(modelAndView.getModel().get("partida"), notNullValue());
  }

  /**
   * Test específico del fix: el controlador NO debe volver a mezclar el mazo.
   * Tiene que tomar la mano y el mazo restante tal cual los devuelve el servicio,
   * y las sesiones guardadas ("idsMano" / "idsMazoRobo") tienen que reflejar
   * exactamente eso, sin re-armar nada por su cuenta.
   */
  @Test
  public void alCrearNuevoCombateDebeUsarLaManoYElMazoRestanteQueDevuelveElServicioSinVolverAMezclar() {
    Long idUsuario = 1L;
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(idUsuario);

    Usuario usuarioSimulado = new Usuario();
    Mazo mazoSimulado = new Mazo();
    List<Carta> cartasDelMazo = new ArrayList<>();
    for (long i = 1; i <= 8; i++) {
      Carta c = new Carta();
      c.setId(i);
      cartasDelMazo.add(c);
    }
    mazoSimulado.setCartas(cartasDelMazo);
    usuarioSimulado.setMazoActivo(mazoSimulado);
    when(servicioUsuarioMock.buscarPorId(idUsuario)).thenReturn(usuarioSimulado);

    // Simulamos el contrato real de ServicioPartida.iniciarPartida(): ya viene
    // con la mano y el mazo restante calculados.
    List<Carta> manoQueDevuelveElServicio = new ArrayList<>(cartasDelMazo.subList(0, 5));
    List<Carta> mazoRestanteQueDevuelveElServicio = new ArrayList<>(cartasDelMazo.subList(5, 8));

    Partida partidaSimulada = new Partida();
    partidaSimulada.setId(20L);
    partidaSimulada.setManoJugador(manoQueDevuelveElServicio);
    partidaSimulada.setMazoRestante(mazoRestanteQueDevuelveElServicio);

    when(
      servicioPartidaMock.iniciarPartida(
        org.mockito.ArgumentMatchers.any(Usuario.class),
        anyString()
      )
    )
      .thenReturn(partidaSimulada);

    ModelAndView mav = controladorCombate.iniciarCombate("bosque", requestMock);

    assertThat(mav.getViewName(), equalTo("combate"));

    @SuppressWarnings("unchecked")
    List<Carta> manoEnModelo = (List<Carta>) mav.getModel().get("mano");
    assertThat(manoEnModelo, hasSize(5));
    assertThat(manoEnModelo, is(manoQueDevuelveElServicio));

    assertThat(mav.getModel().get("cartasEnMazo"), is(3));

    verify(sessionMock, times(1)).setAttribute(eq("idsMano"), anyList());
    verify(sessionMock, times(1)).setAttribute(eq("idsMazoRobo"), anyList());
    verify(servicioPartidaMock, times(1))
      .iniciarPartida(org.mockito.ArgumentMatchers.any(Usuario.class), anyString());
  }

  @Test
  public void queAlJugarUnaCartaElControladorDelegueAlServicioYDevuelvaLaVista() {
    Long idCarta = 1L;
    Long idPartida = 1L;
    String zona = "bosque";
    Long idUsuario = 1L;

    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(idUsuario);
    Usuario usuarioSimulado = new Usuario();
    Mazo mazoSimulado = new Mazo();
    mazoSimulado.setCartas(new ArrayList<>());
    usuarioSimulado.setMazoActivo(mazoSimulado);
    when(servicioUsuarioMock.buscarPorId(idUsuario)).thenReturn(usuarioSimulado);

    String logEsperado =
      "⚔️ Atacas con [Golpe Básico] causando 15 de daño. El Infectado contraataca y recibes 5 de daño.";
    Partida partidaActualizada = new Partida();
    partidaActualizada.setHpJugador(85);
    partidaActualizada.setHpEnemigo(100);

    when(servicioCombateMock.jugarTurno(idPartida, idCarta)).thenReturn(logEsperado);
    when(servicioCombateMock.obtenerPartidaPorIdentificador(idPartida))
      .thenReturn(partidaActualizada);

    ModelAndView modelAndView = controladorCombate.jugarCarta(
      idCarta,
      idPartida,
      zona,
      requestMock
    );

    assertThat(modelAndView.getViewName(), equalTo("combate"));
    String log = (String) modelAndView.getModel().get("logCombate");
    assertThat(log, equalTo(logEsperado));
  }
}
