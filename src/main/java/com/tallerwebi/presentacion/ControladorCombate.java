package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Partida;
import com.tallerwebi.dominio.ServicioCombate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorCombate {

  private ServicioCombate servicioCombate;

  @Autowired
  public ControladorCombate(ServicioCombate servicioCombate) {
    this.servicioCombate = servicioCombate;
  }

  @RequestMapping(path = "/combate", method = RequestMethod.GET)
  public ModelAndView combate() {
    return new ModelAndView("combate");
  }

  @RequestMapping(path = "/jugar-carta", method = RequestMethod.POST)
  public ModelAndView jugarCarta(
    @RequestParam Integer identificadorCarta,
    @RequestParam Long identificadorPartida
  ) {
    Integer danioCartaHaciaEnemigo =
      this.servicioCombate.jugarCarta(identificadorCarta, identificadorPartida);
    Partida partida = servicioCombate.obtenerPartidaPorIdentificador(identificadorPartida);

    ModelMap modelo = new ModelMap();
    modelo.put("danioCartaHaciaEnemigo", danioCartaHaciaEnemigo);
    modelo.put("hpEnemigo", partida.getHpEnemigo());
    modelo.put("turno", partida.getTurno());

    return new ModelAndView("combate", modelo);
  }
}
