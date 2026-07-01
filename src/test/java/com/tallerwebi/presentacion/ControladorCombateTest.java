//package com.tallerwebi.presentacion;
//
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.*;
//import static org.mockito.Mockito.*;
//
//import com.tallerwebi.dominio.Partida;
//import com.tallerwebi.dominio.ServicioCarta;
//import com.tallerwebi.dominio.ServicioCombate;
//import java.util.ArrayList;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.web.servlet.ModelAndView;
//
//public class ControladorCombateTest {
//
//  private ServicioCombate servicioCombateMock;
//  private ServicioCarta servicioCartaMock;
//  private ControladorCombate controladorCombate;
//
//  @BeforeEach
//  public void init() {
//    servicioCombateMock = mock(ServicioCombate.class);
//    servicioCartaMock = mock(ServicioCarta.class);
//    controladorCombate = new ControladorCombate(servicioCombateMock, servicioCartaMock);
//  }
//
//  @Test
//  public void queSePuedaIniciarUnCombate() {
//    when(servicioCartaMock.obtenerTodas()).thenReturn(new ArrayList<>());
//
//    ModelAndView modelAndView = controladorCombate.iniciarCombate("bosque");
//
//    assertThat(modelAndView.getViewName(), equalTo("combate"));
//    assertThat(modelAndView.getModel().get("partida"), notNullValue());
//  }
//
//  @Test
//  public void queAlJugarUnaCartaElControladorDelegueAlServicioYDevuelvaLaVista() {
//    Long idCarta = 1L;
//
//    // Simulamos la respuesta del Servicio
//    String logEsperado = "Usaste [Golpe Básico]. Hiciste 15 de Daño. El Infectado te sacó 5 HP.";
//    when(servicioCombateMock.jugarTurno(org.mockito.Mockito.any(Partida.class), eq(idCarta)))
//      .thenReturn(logEsperado);
//
//    ModelAndView modelAndView = controladorCombate.jugarCarta(idCarta, 100, 50);
//
//    assertThat(modelAndView.getViewName(), equalTo("combate"));
//
//    String log = (String) modelAndView.getModel().get("logCombate");
//    assertThat(log, equalTo(logEsperado));
//  }
//}
