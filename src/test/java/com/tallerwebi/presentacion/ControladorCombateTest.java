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
  private HttpSession sessionMock; // <-- Necesitamos mockear la sesión ahora

  @BeforeEach
  public void init() {
    servicioCombateMock = mock(ServicioCombate.class);
    servicioPartidaMock = mock(ServicioPartida.class);
    servicioUsuarioMock = mock(ServicioUsuario.class);

    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);

    // Le decimos al Request falso que devuelva nuestra Sesión falsa
    when(requestMock.getSession()).thenReturn(sessionMock);

    // Inicializamos con los 3 correctos
    controladorCombate =
      new ControladorCombate(servicioCombateMock, servicioPartidaMock, servicioUsuarioMock);
  }

  @Test
  public void queSePuedaIniciarUnCombate() {
    Long idUsuario = 1L;

    // 1. Simulamos que el usuario está logueado en la sesión
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(idUsuario);

    // 2. Creamos un Usuario falso con un Mazo falso para pasar la validación del Controlador
    Usuario usuarioSimulado = new Usuario();
    Mazo mazoSimulado = new Mazo();
    List<Carta> cartas = new ArrayList<>();
    cartas.add(new Carta()); // Tiene que tener al menos 1 carta para no rebotar al lobby
    mazoSimulado.setCartas(cartas);
    usuarioSimulado.setMazoActivo(mazoSimulado);

    when(servicioUsuarioMock.buscarPorId(idUsuario)).thenReturn(usuarioSimulado);

    // 3. Mockeamos el servicioPartida para que devuelva una Partida simulada
    Partida partidaSimulada = new Partida();
    partidaSimulada.setId(10L);
    when(
      servicioPartidaMock.iniciarPartida(
        org.mockito.Mockito.any(Usuario.class),
        org.mockito.Mockito.anyString()
      )
    )
      .thenReturn(partidaSimulada);

    // 4. Ejecutamos el método
    ModelAndView modelAndView = controladorCombate.iniciarCombate("bosque", requestMock);

    // 5. Verificamos
    assertThat(modelAndView.getViewName(), equalTo("combate"));
    assertThat(modelAndView.getModel().get("partida"), notNullValue());
  }

  @Test
  public void queAlJugarUnaCartaElControladorDelegueAlServicioYDevuelvaLaVista() {
    Long idCarta = 1L;
    Long idPartida = 1L;
    String zona = "bosque";
    Long idUsuario = 1L;

    // 1. Simulamos usuario logueado con mazo para pasar la validación
    when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(idUsuario);
    Usuario usuarioSimulado = new Usuario();
    Mazo mazoSimulado = new Mazo();
    mazoSimulado.setCartas(new ArrayList<>());
    usuarioSimulado.setMazoActivo(mazoSimulado);
    when(servicioUsuarioMock.buscarPorId(idUsuario)).thenReturn(usuarioSimulado);

    String logEsperado =
      "⚔️ Atacas con [Golpe Básico] causando 15 de daño. El Infectado contraataca y recibes 5 de daño.";
    Partida partidaActualizada = new Partida();
    partidaActualizada.setHpJugador(85);   // el valor que corresponda después de recibir 5 de daño
    partidaActualizada.setHpEnemigo(100);  // si el controlador también lee esto en la línea 109 o cerca

    // 2. Simulamos las respuestas de los servicios
    when(servicioCombateMock.jugarTurno(idPartida, idCarta)).thenReturn(logEsperado);
    when(servicioCombateMock.obtenerPartidaPorIdentificador(idPartida))
      .thenReturn(partidaActualizada);

    // 3. Ejecutamos (Acá te faltaba pasarle el requestMock)
    ModelAndView modelAndView = controladorCombate.jugarCarta(
      idCarta,
      idPartida,
      zona,
      requestMock
    );

    // 4. Verificamos
    assertThat(modelAndView.getViewName(), equalTo("combate"));
    String log = (String) modelAndView.getModel().get("logCombate");
    assertThat(log, equalTo(logEsperado));
  }
}
