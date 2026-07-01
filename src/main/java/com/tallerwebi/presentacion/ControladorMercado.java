package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Carta;
import com.tallerwebi.dominio.PropuestaIntercambio;
import com.tallerwebi.dominio.ServicioMercado;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Transactional
public class ControladorMercado {

  private static final String JUGADOR_ID_KEY = "jugadorId";
  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String ATRIBUTO_ERROR = "error";
  private static final String VISTA_INTERCAMBIO = "intercambio";

  private final ServicioMercado servicioMercado;

  public ControladorMercado(ServicioMercado servicioMercado) {
    this.servicioMercado = servicioMercado;
  }

  @RequestMapping(value = "/mercado", method = RequestMethod.GET)
  public ModelAndView verMercadoComunidad(HttpSession session) {
    Long jugadorId = (Long) session.getAttribute(JUGADOR_ID_KEY);
    if (jugadorId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    List<PropuestaIntercambio> ofertas = servicioMercado.obtenerOfertasCompatibles(jugadorId);
    List<Carta> faltantes = servicioMercado.obtenerCartasFaltantes(jugadorId);
    List<PropuestaIntercambio> misTrades = servicioMercado.obtenerMisTrades(jugadorId);

    if (misTrades != null) {
      for (PropuestaIntercambio trade : misTrades) {
        if (trade.getCartaBuscada() != null) {
          org.hibernate.Hibernate.initialize(trade.getCartaBuscada());
        }
      }
    }

    ModelAndView mav = new ModelAndView(VISTA_INTERCAMBIO);
    mav.addObject("ofertas", ofertas);
    mav.addObject("cartasFaltantes", faltantes);
    mav.addObject("misTrades", misTrades);
    return mav;
  }

  @RequestMapping(value = "/mercado/publicar", method = RequestMethod.POST)
  public ModelAndView procesarPublicarTrade(
    @RequestParam Long idCartaBuscada,
    HttpSession session
  ) {
    Long jugadorId = (Long) session.getAttribute(JUGADOR_ID_KEY);
    if (jugadorId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      servicioMercado.publicarSolicitud(jugadorId, idCartaBuscada);
      return new ModelAndView("redirect:/mercado/mis-trades");
    } catch (Exception e) {
      ModelAndView mav = new ModelAndView(VISTA_INTERCAMBIO);
      mav.addObject(ATRIBUTO_ERROR, e.getMessage());

      mav.addObject("ofertas", servicioMercado.obtenerOfertasCompatibles(jugadorId));
      mav.addObject("cartasFaltantes", servicioMercado.obtenerCartasFaltantes(jugadorId));
      mav.addObject("misTrades", servicioMercado.obtenerMisTrades(jugadorId));
      return mav;
    }
  }

  @RequestMapping(value = "/mercado/mis-trades", method = RequestMethod.GET)
  public ModelAndView verMisTrades(HttpSession session) {
    Long jugadorId = (Long) session.getAttribute(JUGADOR_ID_KEY);
    if (jugadorId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    List<PropuestaIntercambio> misTrades = servicioMercado.obtenerMisTrades(jugadorId);

    if (misTrades != null) {
      for (PropuestaIntercambio trade : misTrades) {
        if (trade.getCartaBuscada() != null) {
          org.hibernate.Hibernate.initialize(trade.getCartaBuscada());
        }
        if (trade.getCartaOfrecida() != null) {
          org.hibernate.Hibernate.initialize(trade.getCartaOfrecida());
        }
      }
    }

    ModelAndView mav = new ModelAndView("mis-trades");
    mav.addObject("misTrades", misTrades);
    return mav;
  }

  @RequestMapping(value = "/mercado/aceptar-paso-1", method = RequestMethod.POST)
  public ModelAndView iniciarAceptarTrade(@RequestParam Long idPropuesta, HttpSession session) {
    Long jugadorId = (Long) session.getAttribute(JUGADOR_ID_KEY);
    if (jugadorId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      PropuestaIntercambio propuesta = servicioMercado.buscarPorId(idPropuesta);
      List<Carta> opciones = servicioMercado.obtenerOpcionesRecompensa(propuesta);

      ModelAndView mav = new ModelAndView("elegir-recompensa");
      mav.addObject("propuesta", propuesta);
      mav.addObject("opciones", opciones);
      return mav;
    } catch (Exception e) {
      return new ModelAndView("redirect:/mercado?error=" + e.getMessage());
    }
  }

  @RequestMapping(value = "/mercado/confirmar", method = RequestMethod.POST)
  public ModelAndView confirmarTrade(
    @RequestParam Long idPropuesta,
    @RequestParam Long idCartaRecompensa,
    HttpSession session
  ) {
    Long jugadorId = (Long) session.getAttribute(JUGADOR_ID_KEY);
    if (jugadorId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      servicioMercado.finalizarIntercambio(jugadorId, idPropuesta, idCartaRecompensa);
      return new ModelAndView("redirect:/mercado");
    } catch (Exception e) {
      return new ModelAndView("redirect:/mercado?error=" + e.getMessage());
    }
  }

  @RequestMapping(value = "/mercado/eliminar", method = RequestMethod.POST)
  public ModelAndView eliminarTrade(@RequestParam Long idPropuesta, HttpSession session) {
    Long jugadorId = (Long) session.getAttribute(JUGADOR_ID_KEY);
    if (jugadorId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      servicioMercado.eliminarMiTrade(jugadorId, idPropuesta);
      return new ModelAndView("redirect:/mercado/mis-trades");
    } catch (Exception e) {
      return new ModelAndView("redirect:/mercado/mis-trades?error=" + e.getMessage());
    }
  }
}
