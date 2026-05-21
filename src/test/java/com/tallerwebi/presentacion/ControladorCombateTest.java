package com.tallerwebi.presentacion;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tallerwebi.dominio.ServicioCombate;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorCombateTest {

  ServicioCombate servicioCombate = new ServicioCombate();

  @Test
  public void deberiaMostrarVistaCombate() {
    ControladorCombate controladorCombate = new ControladorCombate(this.servicioCombate);

    ModelAndView modelAndView = controladorCombate.combate();

    assertEquals("combate", modelAndView.getViewName());
  }

  @Test
  public void deberiaJugarCartaYMostrarDatosEnVista() {
    ControladorCombate controladorCombate = new ControladorCombate(this.servicioCombate);

    ModelAndView modelAndView = controladorCombate.jugarCarta(1, 1);

    assertEquals("combate", modelAndView.getViewName());
  }
}
