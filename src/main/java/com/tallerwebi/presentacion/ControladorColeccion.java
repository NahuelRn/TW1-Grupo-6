package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Carta;
import com.tallerwebi.dominio.ItemInventario;
import com.tallerwebi.dominio.RepositorioInventario;
import com.tallerwebi.dominio.ServicioCarta;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorColeccion {

  private final ServicioCarta servicioCarta;
  private final RepositorioInventario repositorioInventario;

  @Autowired
  public ControladorColeccion(
    ServicioCarta servicioCarta,
    RepositorioInventario repositorioInventario
  ) {
    this.servicioCarta = servicioCarta;
    this.repositorioInventario = repositorioInventario;
  }

  @RequestMapping(path = "/coleccion", method = RequestMethod.GET)
  public ModelAndView verColeccion(HttpServletRequest request) {
    Long jugadorId = (Long) request.getSession().getAttribute("JUGADOR_ID");

    if (jugadorId == null) {
      return new ModelAndView("redirect:/login");
    }

    ModelMap modelo = new ModelMap();
    List<Carta> todasLasCartas = servicioCarta.obtenerTodas();
    List<ItemInventario> miInventario = repositorioInventario.listarInventarioDeJugador(jugadorId);

    Set<Long> cartasQueTengo = miInventario
      .stream()
      .map(item -> item.getCarta().getId())
      .collect(Collectors.toSet());

    modelo.put("todasLasCartas", todasLasCartas);
    modelo.put("cartasQueTengo", cartasQueTengo);

    return new ModelAndView("coleccion", modelo);
  }
}
