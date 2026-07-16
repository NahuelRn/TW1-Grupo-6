package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

public class ControladorCombateTest {

  private ServicioCombate servicioCombateMock;
  private ServicioPartida servicioPartidaMock;
  private ServicioUsuario servicioUsuarioMock;
  private ServicioCalculoRecompensa servicioCalculoRecompensaMock;
  private ServicioHistorial servicioHistorialMock;
  private ServicioCarta servicioCartaMock;
  private ControladorCombate controladorCombate;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;
  private Model modelMock;

  @BeforeEach
  public void init() {
    servicioCombateMock = mock(ServicioCombate.class);
    servicioPartidaMock = mock(ServicioPartida.class);
    servicioUsuarioMock = mock(ServicioUsuario.class);
    servicioCalculoRecompensaMock = mock(ServicioCalculoRecompensa.class);
    servicioHistorialMock = mock(ServicioHistorial.class);
    servicioCartaMock = mock(ServicioCarta.class);

    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);
    modelMock = mock(Model.class);

    when(requestMock.getSession()).thenReturn(sessionMock);

    controladorCombate =
      new ControladorCombate(
        servicioCombateMock,
        servicioPartidaMock,
        servicioUsuarioMock,
        servicioCalculoRecompensaMock,
        servicioHistorialMock,
        servicioCartaMock
      );
  }

  // ------------------------------------------------------------------
  // GET /combate
  // ------------------------------------------------------------------

  @Test
  public void siNoHayUsuarioEnSesionDebeRedirigirALogin() {
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(null);

    ModelAndView mav = controladorCombate.iniciarCombate("bosque", requestMock);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  public void siElUsuarioNoTieneMazoActivoDebeMostrarErrorEnLobby() {
    Long idUsuario = 1L;
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(idUsuario);

    Usuario usuarioSimulado = new Usuario();
    usuarioSimulado.setMazoActivo(null);
    when(servicioUsuarioMock.buscarPorId(idUsuario)).thenReturn(usuarioSimulado);

    ModelAndView mav = controladorCombate.iniciarCombate("bosque", requestMock);

    assertThat(mav.getViewName(), equalTo("lobby"));
    assertThat(mav.getModel().get("error"), notNullValue());
  }

  @Test
  public void queSePuedaIniciarUnCombateNuevo() {
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
    partidaSimulada.setManoJugador(new ArrayList<>());
    partidaSimulada.setMazoRestante(new ArrayList<>());
    when(servicioPartidaMock.iniciarPartida(any(Usuario.class), anyString()))
      .thenReturn(partidaSimulada);

    ModelAndView modelAndView = controladorCombate.iniciarCombate("bosque", requestMock);

    assertThat(modelAndView.getViewName(), equalTo("combate"));
    assertThat(modelAndView.getModel().get("partida"), notNullValue());
    verify(sessionMock, times(1)).setAttribute("idPartidaActiva", 10L);
  }

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

    List<Carta> manoQueDevuelveElServicio = new ArrayList<>(cartasDelMazo.subList(0, 5));
    List<Carta> mazoRestanteQueDevuelveElServicio = new ArrayList<>(cartasDelMazo.subList(5, 8));

    Partida partidaSimulada = new Partida();
    partidaSimulada.setId(20L);
    partidaSimulada.setManoJugador(manoQueDevuelveElServicio);
    partidaSimulada.setMazoRestante(mazoRestanteQueDevuelveElServicio);

    when(servicioPartidaMock.iniciarPartida(any(Usuario.class), anyString()))
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
    verify(servicioPartidaMock, times(1)).iniciarPartida(any(Usuario.class), anyString());
  }

  @Test
  public void siHayPartidaActivaEnSesionDebeRetomarElCombateSinCrearUnaNueva() {
    Long idUsuario = 1L;
    Long idPartidaActiva = 99L;
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(idUsuario);
    when(sessionMock.getAttribute("idPartidaActiva")).thenReturn(idPartidaActiva);

    Usuario usuarioSimulado = new Usuario();
    Mazo mazoSimulado = new Mazo();
    Carta c1 = new Carta();
    c1.setId(1L);
    mazoSimulado.setCartas(List.of(c1));
    usuarioSimulado.setMazoActivo(mazoSimulado);
    when(servicioUsuarioMock.buscarPorId(idUsuario)).thenReturn(usuarioSimulado);

    when(sessionMock.getAttribute("idsMano")).thenReturn(List.of(1L));
    when(sessionMock.getAttribute("idsMazoRobo")).thenReturn(new ArrayList<Long>());

    Partida partidaExistente = new Partida();
    partidaExistente.setId(idPartidaActiva);
    when(servicioCombateMock.obtenerPartidaPorIdentificador(idPartidaActiva))
      .thenReturn(partidaExistente);

    ModelAndView mav = controladorCombate.iniciarCombate("bosque", requestMock);

    assertThat(mav.getViewName(), equalTo("combate"));
    assertThat(mav.getModel().get("logCombate"), equalTo("Retomas el combate."));
    verify(servicioPartidaMock, never()).iniciarPartida(any(), anyString());
  }

  // ------------------------------------------------------------------
  // POST /jugar-carta
  // ------------------------------------------------------------------

  @Test
  public void alJugarUnaCartaSinFinalizarLaPartidaDebeDevolverLaVistaDeCombate() {
    // 1. Setup de variables
    Long idCarta = 1L; // Esta es la carta que vamos a jugar
    Long idPartida = 1L;
    Long idUsuario = 1L;

    // 2. Mocks de Sesión y Usuario
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(idUsuario);

    Usuario usuarioSimulado = new Usuario();
    List<Carta> cartasDelMazo = new ArrayList<>();
    cartasDelMazo.add(new Carta());

    Mazo mazoSimulado = new Mazo();
    mazoSimulado.setCartas(cartasDelMazo);
    usuarioSimulado.setMazoActivo(mazoSimulado);
    when(servicioUsuarioMock.buscarPorId(idUsuario)).thenReturn(usuarioSimulado);

    // 3. Mocks de la Mano y Mazo de Robo con CARTAS DE SOBRA
    List<Long> idsManoSimulada = new ArrayList<>(Arrays.asList(1L, 2L)); // Ponemos la 1L y la 2L
    when(sessionMock.getAttribute("idsMano")).thenReturn(idsManoSimulada);

    List<Long> idsMazoRoboSimulado = new ArrayList<>(Arrays.asList(3L, 4L)); // Ponemos un par más acá
    when(sessionMock.getAttribute("idsMazoRobo")).thenReturn(idsMazoRoboSimulado);

    // 4. Mocks del Combate
    String logEsperado = "Atacas con [Golpe Básico] causando 15 de daño.";
    Partida partidaActualizada = new Partida();
    partidaActualizada.setHpJugador(85);
    partidaActualizada.setHpEnemigo(50); // Nadie llega a 0 HP

    when(servicioCombateMock.jugarTurno(idPartida, idCarta)).thenReturn(logEsperado);
    when(servicioCombateMock.obtenerPartidaPorIdentificador(idPartida))
      .thenReturn(partidaActualizada);

    // 5. Ejecución
    ModelAndView mav = controladorCombate.jugarCarta(
      idCarta,
      idPartida,
      "bosque",
      requestMock,
      modelMock
    );

    // 6. Validaciones
    assertThat(mav.getViewName(), equalTo("combate"));
    assertThat(mav.getModel().get("logCombate"), equalTo(logEsperado));
  }

  @Test
  public void siElEnemigoQuedaSinVidaDebeGuardarHistorialDeVictoriaYMostrarRecompensas() {
    Long idCarta = 1L;
    Long idPartida = 1L;
    Long idUsuario = 1L;

    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(idUsuario);

    Usuario usuarioSimulado = new Usuario();
    Mazo mazoSimulado = new Mazo();
    mazoSimulado.setCartas(new ArrayList<>());
    usuarioSimulado.setMazoActivo(mazoSimulado);
    when(servicioUsuarioMock.buscarPorId(idUsuario)).thenReturn(usuarioSimulado);

    Partida partidaGanada = new Partida();
    partidaGanada.setHpJugador(50);
    partidaGanada.setHpEnemigo(0);

    when(servicioCombateMock.jugarTurno(idPartida, idCarta)).thenReturn("Golpe letal");
    when(servicioCombateMock.obtenerPartidaPorIdentificador(idPartida)).thenReturn(partidaGanada);

    when(sessionMock.getAttribute("idsMano")).thenReturn(null);
    when(sessionMock.getAttribute("idsMazoRobo")).thenReturn(null);

    RecompensaDTO recompensaDTO = new RecompensaDTO();
    recompensaDTO.setOro(20);
    recompensaDTO.setExperiencia(50);
    when(servicioCalculoRecompensaMock.obtenerRecompensa(partidaGanada)).thenReturn(recompensaDTO);

    ModelAndView mav = controladorCombate.jugarCarta(
      idCarta,
      idPartida,
      "bosque",
      requestMock,
      modelMock
    );

    assertThat(mav.getViewName(), equalTo("recompensas"));
    assertThat(mav.getModel().get("recompensa"), is(recompensaDTO));
    verify(servicioHistorialMock, times(1))
      .guardarHistorialPartidaServicio(any(HistorialPartida.class));
  }

  @Test
  public void siElJugadorQuedaSinVidaDebeGuardarHistorialDeDerrotaYMostrarGameOver() {
    Long idCarta = 1L;
    Long idPartida = 1L;
    Long idUsuario = 1L;

    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(idUsuario);

    Usuario usuarioSimulado = new Usuario();
    List<Carta> cartasDelMazo = new ArrayList<>();
    cartasDelMazo.add(new Carta());

    Mazo mazoSimulado = new Mazo();
    mazoSimulado.setCartas(cartasDelMazo);
    usuarioSimulado.setMazoActivo(mazoSimulado);
    when(servicioUsuarioMock.buscarPorId(idUsuario)).thenReturn(usuarioSimulado);

    Partida partidaPerdida = new Partida();
    partidaPerdida.setHpJugador(0);
    partidaPerdida.setHpEnemigo(30);

    when(servicioCombateMock.jugarTurno(idPartida, idCarta)).thenReturn("Fuiste derrotado");
    when(servicioCombateMock.obtenerPartidaPorIdentificador(idPartida)).thenReturn(partidaPerdida);

    List<Long> idsManoSimulada = new ArrayList<>(Arrays.asList(1L, 2L));
    when(sessionMock.getAttribute("idsMano")).thenReturn(idsManoSimulada);

    List<Long> idsMazoRoboSimulado = new ArrayList<>(Arrays.asList(3L, 4L));
    when(sessionMock.getAttribute("idsMazoRobo")).thenReturn(idsMazoRoboSimulado);

    when(servicioCalculoRecompensaMock.obtenerRecompensa(partidaPerdida))
      .thenReturn(new RecompensaDTO());

    Carta cartaJugada = new Carta();
    cartaJugada.setNombre("Golpe Débil");
    when(servicioCartaMock.buscarPorId(idCarta)).thenReturn(cartaJugada);

    ModelAndView mav = controladorCombate.jugarCarta(
      idCarta,
      idPartida,
      "bosque",
      requestMock,
      modelMock
    );

    assertThat(mav.getViewName(), equalTo("game-over"));
    assertThat(
      mav.getModel().get("motivoDerrota"),
      equalTo("¡Tus puntos de vida llegaron a cero!")
    );
    assertThat(mav.getModel().get("ultimaCartaJugada"), is(cartaJugada));
    verify(servicioHistorialMock, times(1))
      .guardarHistorialPartidaServicio(any(HistorialPartida.class));
  }

  @Test
  public void siSeQuedaSinCartasEnManoYMazoDebePerderPorFaltaDeCartas() {
    Long idCarta = 1L;
    Long idPartida = 1L;
    Long idUsuario = 1L;

    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(idUsuario);

    Usuario usuarioSimulado = new Usuario();
    Mazo mazoSimulado = new Mazo();
    mazoSimulado.setCartas(new ArrayList<>());
    usuarioSimulado.setMazoActivo(mazoSimulado);
    when(servicioUsuarioMock.buscarPorId(idUsuario)).thenReturn(usuarioSimulado);

    Partida partidaEnCurso = new Partida();
    partidaEnCurso.setHpJugador(40);
    partidaEnCurso.setHpEnemigo(60);

    when(servicioCombateMock.jugarTurno(idPartida, idCarta)).thenReturn("Última carta jugada");
    when(servicioCombateMock.obtenerPartidaPorIdentificador(idPartida)).thenReturn(partidaEnCurso);

    // mano y mazo de robo vacíos -> se acabaron las cartas
    when(sessionMock.getAttribute("idsMano")).thenReturn(new ArrayList<Long>());
    when(sessionMock.getAttribute("idsMazoRobo")).thenReturn(new ArrayList<Long>());

    when(servicioCalculoRecompensaMock.obtenerRecompensa(partidaEnCurso))
      .thenReturn(new RecompensaDTO());
    when(servicioCartaMock.buscarPorId(idCarta)).thenReturn(new Carta());

    ModelAndView mav = controladorCombate.jugarCarta(
      idCarta,
      idPartida,
      "bosque",
      requestMock,
      modelMock
    );

    assertThat(mav.getViewName(), equalTo("game-over"));
    assertThat(
      mav.getModel().get("motivoDerrota"),
      equalTo("¡Te quedaste sin cartas y el enemigo resistió!")
    );
  }

  // ------------------------------------------------------------------
  // Regresión del bug: "vuelvo al lobby y el próximo combate sigue trabado"
  // ------------------------------------------------------------------

  @Test
  public void alFinalizarUnaPartidaDebeLimpiarLaPartidaActivaDeSesionParaHabilitarUnaNueva() {
    Long idCarta = 1L;
    Long idPartida = 1L;
    Long idUsuario = 1L;

    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(idUsuario);

    Usuario usuarioSimulado = new Usuario();
    Mazo mazoSimulado = new Mazo();
    mazoSimulado.setCartas(new ArrayList<>());
    usuarioSimulado.setMazoActivo(mazoSimulado);
    when(servicioUsuarioMock.buscarPorId(idUsuario)).thenReturn(usuarioSimulado);

    Partida partidaGanada = new Partida();
    partidaGanada.setHpJugador(50);
    partidaGanada.setHpEnemigo(0);

    when(servicioCombateMock.jugarTurno(idPartida, idCarta)).thenReturn("Golpe letal");
    when(servicioCombateMock.obtenerPartidaPorIdentificador(idPartida)).thenReturn(partidaGanada);
    when(sessionMock.getAttribute("idsMano")).thenReturn(null);
    when(sessionMock.getAttribute("idsMazoRobo")).thenReturn(null);
    when(servicioCalculoRecompensaMock.obtenerRecompensa(partidaGanada))
      .thenReturn(new RecompensaDTO());

    controladorCombate.jugarCarta(idCarta, idPartida, "bosque", requestMock, modelMock);

    // Si esto no se limpia, el próximo GET /combate entra a retomarCombate()
    // con una partida ya terminada -> combate "trabado" que reportaste.
    verify(sessionMock, times(1)).removeAttribute("idPartidaActiva");
    verify(sessionMock, times(1)).removeAttribute("idsMano");
    verify(sessionMock, times(1)).removeAttribute("idsMazoRobo");
  }
}
