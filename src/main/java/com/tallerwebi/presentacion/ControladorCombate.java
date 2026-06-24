package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Carta;
import com.tallerwebi.dominio.Partida;
import com.tallerwebi.dominio.ServicioCarta;
import com.tallerwebi.dominio.ServicioCombate;
import java.util.Collections;
import java.util.List;
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
  private ServicioCarta servicioCarta;

  @Autowired
  public ControladorCombate(ServicioCombate servicioCombate, ServicioCarta servicioCarta) {
    this.servicioCombate = servicioCombate;
    this.servicioCarta = servicioCarta;
  }

  @RequestMapping(path = "/combate", method = RequestMethod.GET)
  public ModelAndView iniciarCombate(@RequestParam(value = "zona", required = false) String zona) {
    ModelMap modelo = new ModelMap();

    Partida partida = new Partida(100, 50, 1);

    String nombreZona = (zona != null) ? zona.toUpperCase(java.util.Locale.ROOT) : "DESCONOCIDA";
    String logCombate =
      "¡Entraste a la zona " + nombreZona + " y un INFECTADO te cortó el paso! Es tu turno.";

    cargarManoEnModelo(modelo);

    modelo.put("partida", partida);
    modelo.put("logCombate", logCombate);

    return new ModelAndView("combate", modelo);
  }

  @RequestMapping(path = "/jugar-carta", method = RequestMethod.POST)
  public ModelAndView jugarCarta(
    @RequestParam Long idCarta,
    @RequestParam Integer hpJugador,
    @RequestParam Integer hpEnemigo
  ) {
    ModelMap modelo = new ModelMap();

    Partida partidaActual = new Partida(hpJugador, hpEnemigo, 1);

    String logCombate = servicioCombate.jugarTurno(partidaActual, idCarta);

    cargarManoEnModelo(modelo);
    modelo.put("partida", partidaActual);
    modelo.put("logCombate", logCombate);

    return new ModelAndView("combate", modelo);
  }

  private void cargarManoEnModelo(ModelMap modelo) {
    List<Carta> catalogo = servicioCarta.obtenerTodas();
    Collections.shuffle(catalogo);

    List<Carta> mazoSimulado = catalogo.subList(0, Math.min(15, catalogo.size()));
    List<Carta> mano = mazoSimulado.subList(0, Math.min(5, mazoSimulado.size()));

    int cartasEnMazo = mazoSimulado.size() - mano.size();

    modelo.put("mano", mano);
    modelo.put("cartasEnMazo", cartasEnMazo);
  }
}
