package com.tallerwebi.presentacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.*;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorCombateTest {

  ServicioHistorial servicioHistorial = mock(ServicioHistorial.class);
  RepositorioPartida repositorioPartida = mock(RepositorioPartida.class);
  ServicioCombate servicioCombate = new ServicioCombateImpl(repositorioPartida, servicioHistorial);

  @Test
  public void deberiaMostrarVistaCombate() {
    ControladorCombate controladorCombate = new ControladorCombate(this.servicioCombate);

    ModelAndView modelAndView = controladorCombate.combate();

    assertEquals("combate", modelAndView.getViewName());
  }

  @Test
  public void deberiaJugarCartaYMostrarDatosEnVista() {
    ControladorCombate controladorCombate = new ControladorCombate(this.servicioCombate);

    Partida partida = new Partida(100, 100, 1);
    when(this.repositorioPartida.buscarPartidaPorIdentificador(1L)).thenReturn(partida);

    ArrayList<Integer> cartasEnMano = new ArrayList<>();
    cartasEnMano.add(1);

    partida.setCartasEnManoJugador(cartasEnMano);

    ModelAndView modelAndView = controladorCombate.jugarCarta(1, 1L);

    assertEquals("combate", modelAndView.getViewName());
  }
}
