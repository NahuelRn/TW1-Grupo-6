package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.RepositorioMercado;
import com.tallerwebi.dominio.ServicioMercado;
import com.tallerwebi.dominio.Usuario;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Transactional
public class ControladorMercado {

  private static final String REDIRECT_LOGIN = "redirect:/login";
  // CORRECCIÓN: El string que retorna la vista ahora coincide exactamente con tu archivo intercambio.html
  private static final String VISTA_MERCADO = "intercambio";

  private final ServicioMercado servicioMercado;
  private final RepositorioMercado repositorioMercado;

  public ControladorMercado(
    ServicioMercado servicioMercado,
    RepositorioMercado repositorioMercado
  ) {
    this.servicioMercado = servicioMercado;
    this.repositorioMercado = repositorioMercado;
  }

  @RequestMapping(path = "/mercado", method = RequestMethod.GET)
  public ModelAndView verMercado(HttpSession session) {
    Long jugadorId = (Long) session.getAttribute("jugadorId");

    if (jugadorId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    Usuario usuarioLogueado = repositorioMercado.buscarUsuarioPorId(jugadorId);
    if (usuarioLogueado == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    ModelMap modelo = new ModelMap();
    modelo.put("ofertas", servicioMercado.obtenerMercado(usuarioLogueado));
    return new ModelAndView(VISTA_MERCADO, modelo); // Usa "intercambio"
  }

  @RequestMapping(path = "/mercado/aceptar", method = RequestMethod.POST)
  public ModelAndView aceptarTrade(@RequestParam("idOferta") Long idOferta, HttpSession session) {
    Long jugadorId = (Long) session.getAttribute("jugadorId");

    if (jugadorId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    Usuario usuarioLogueado = repositorioMercado.buscarUsuarioPorId(jugadorId);
    if (usuarioLogueado == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      servicioMercado.aceptarOferta(usuarioLogueado, idOferta);
      return new ModelAndView("redirect:/mercado");
    } catch (Exception e) {
      ModelMap modeloError = new ModelMap();
      modeloError.put("error", e.getMessage());
      modeloError.put("ofertas", servicioMercado.obtenerMercado(usuarioLogueado));
      return new ModelAndView(VISTA_MERCADO, modeloError); // Usa "intercambio"
    }
  }
}
