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
    ModelMap modelo = new ModelMap();

    // 1. Obtenemos el ID del jugador desde la sesion (asumiendo que lo guardaste al loguear)
    Long jugadorId = (Long) request.getSession().getAttribute("JUGADOR_ID");

    // 2. Traemos todas las cartas existentes en el juego
    List<Carta> todasLasCartas = servicioCarta.obtenerTodas();

    // 3. Traemos el inventario del jugador
    List<ItemInventario> miInventario = repositorioInventario.listarInventarioDeJugador(jugadorId);

    // 4. Creamos una lista de IDs de cartas que el jugador YA tiene
    Set<Long> cartasQueTengo = miInventario
      .stream()
      .map(item -> item.getCarta().getId())
      .collect(Collectors.toSet());

    // 5. Mandamos ambos datos a la vista
    modelo.put("todasLasCartas", todasLasCartas);
    modelo.put("cartasQueTengo", cartasQueTengo);

    return new ModelAndView("coleccion", modelo);
  }
}
