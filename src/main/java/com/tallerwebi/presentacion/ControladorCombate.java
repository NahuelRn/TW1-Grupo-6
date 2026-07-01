package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Carta;
import com.tallerwebi.dominio.EnumEstadoPartida;
import com.tallerwebi.dominio.Partida;
import com.tallerwebi.dominio.ServicioCarta;
import com.tallerwebi.dominio.ServicioCombate;
import com.tallerwebi.dominio.Usuario;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
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

  private static final String PARTIDA_ACTUAL = "PARTIDA_ACTUAL";

  private static final String LOG_COMBATE = "logCombate"; // Tuve que crear esto para que el pmd funcione.

  @Autowired
  public ControladorCombate(ServicioCombate servicioCombate, ServicioCarta servicioCarta) {
    this.servicioCombate = servicioCombate;
    this.servicioCarta = servicioCarta;
  }

  @RequestMapping(path = "/combate", method = RequestMethod.GET)
  public ModelAndView iniciarCombate(
    @RequestParam(value = "zona", required = false) String zona,
    HttpServletRequest request
  ) {
    ModelMap modelo = new ModelMap();

    Partida partida = (Partida) request.getSession().getAttribute(this.PARTIDA_ACTUAL);
    if (partida == null) {
      Usuario usuario = (Usuario) request.getSession().getAttribute("USUARIO"); // L
      partida = new Partida(100, 50, 1);
      partida.setUsuario(usuario);
      request.getSession().setAttribute(this.PARTIDA_ACTUAL, partida);
    }

    String nombreZona = (zona != null) ? zona.toUpperCase(java.util.Locale.ROOT) : "DESCONOCIDA";
    String logCombate =
      "¡Entraste a la zona " + nombreZona + " y un INFECTADO te cortó el paso! Es tu turno.";

    cargarManoEnModelo(modelo);

    modelo.put("partida", partida);
    modelo.put(LOG_COMBATE, logCombate);

    return new ModelAndView("combate", modelo);
  }

  @RequestMapping(path = "/jugar-carta", method = RequestMethod.POST)
  public ModelAndView jugarCarta(
    @RequestParam Long idCarta,
    @RequestParam Integer hpJugador,
    @RequestParam Integer hpEnemigo,
    HttpServletRequest request //
  ) {
    Partida partidaActual = (Partida) request.getSession().getAttribute(this.PARTIDA_ACTUAL);

    if (partidaActual == null) {
      partidaActual = new Partida(100, 50, 1);
      partidaActual.setHpJugador(hpJugador);
      partidaActual.setHpEnemigo(hpEnemigo);
      Usuario usuario = (Usuario) request.getSession().getAttribute("USUARIO"); // L
      partidaActual.setUsuario(usuario);
      request.getSession().setAttribute(this.PARTIDA_ACTUAL, partidaActual);
    }

    String logCombate = this.servicioCombate.jugarTurno(partidaActual, idCarta);
    request.getSession().setAttribute(this.PARTIDA_ACTUAL, partidaActual);

    ModelMap modelo = new ModelMap();
    if (partidaActual.getEnumEstadoPartida() == EnumEstadoPartida.GANADOR_JUGADOR) {
      modelo.put(LOG_COMBATE, logCombate);
      return new ModelAndView("redirect:/recompensas", modelo);
    }

    if (partidaActual.getEnumEstadoPartida() == EnumEstadoPartida.GANADOR_ENEMIGO) {
      modelo.put("partida", partidaActual);
      modelo.put(LOG_COMBATE, logCombate);
      return new ModelAndView("game-over", modelo);
    }

    cargarManoEnModelo(modelo);
    modelo.put("partida", partidaActual);
    modelo.put(LOG_COMBATE, logCombate);

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
