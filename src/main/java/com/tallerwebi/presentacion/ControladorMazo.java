package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Carta;
import com.tallerwebi.dominio.Mazo;
import com.tallerwebi.dominio.MazoCarta;
import com.tallerwebi.dominio.ServicioMazo;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorMazo {

  private final ServicioMazo servicioMazo;

  @Autowired
  public ControladorMazo(ServicioMazo servicioMazo) {
    this.servicioMazo = servicioMazo;
  }

  @RequestMapping("/deckbuilding")
  public ModelAndView irADeckbuilding(HttpSession session) {
    Long jugadorId = (Long) session.getAttribute("jugadorId");

    if (jugadorId == null) {
      return new ModelAndView("redirect:/login");
    }

    ModelMap modelo = new ModelMap();
    List<Carta> inventario = servicioMazo.obtenerInventarioPorJugador(jugadorId);
    modelo.put("inventario", inventario);
    return new ModelAndView("deckbuilding", modelo);
  }

  @RequestMapping(path = "/mazo/guardar", method = RequestMethod.POST)
  public ModelAndView guardarMazo(
    @RequestParam(value = "cartasIds", required = false) List<Long> cartasIds,
    HttpSession session
  ) {
    Long jugadorId = (Long) session.getAttribute("jugadorId");

    if (jugadorId == null) {
      return new ModelAndView("redirect:/login");
    }

    // Si no seleccionó cartas, creamos el modelo LOCALMENTE porque solo se usa acá
    if (cartasIds == null || cartasIds.isEmpty()) {
      ModelMap modeloErrorValidacion = new ModelMap();
      modeloErrorValidacion.put("error", "Debes seleccionar exactamente 15 cartas.");
      modeloErrorValidacion.put("inventario", servicioMazo.obtenerInventarioPorJugador(jugadorId));
      return new ModelAndView("deckbuilding", modeloErrorValidacion);
    }

    try {
      Mazo nuevoMazo = new Mazo();
      List<Carta> cartasSeleccionadas = this.servicioMazo.buscarCartasPorIds(cartasIds);

      for (Carta carta : cartasSeleccionadas) {
        MazoCarta nexo = new MazoCarta();
        nexo.setMazo(nuevoMazo);
        nexo.setCarta(carta);
        nuevoMazo.getMazoCartas().add(nexo);
      }

      servicioMazo.validarYGuardarMazo(nuevoMazo);
      // Al redirigir en éxito, no arrastramos ninguna variable ModelMap vacía
      return new ModelAndView("redirect:/seleccion-zona");
    } catch (Exception e) {
      // Si salta una excepción, recién acá creamos el modelo para la pantalla de error
      ModelMap modeloErrorCatch = new ModelMap();
      modeloErrorCatch.put("error", e.getMessage());
      modeloErrorCatch.put("inventario", servicioMazo.obtenerInventarioPorJugador(jugadorId));
      return new ModelAndView("deckbuilding", modeloErrorCatch);
    }
  }
}
