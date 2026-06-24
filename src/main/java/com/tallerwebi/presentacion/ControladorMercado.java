package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Carta;
import com.tallerwebi.dominio.PropuestaIntercambio;
import com.tallerwebi.dominio.RepositorioMercado;
import com.tallerwebi.dominio.ServicioMercado;
import com.tallerwebi.dominio.Usuario;
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

  // Constantes PARA EL CONTROLADOR (¡Acá solucionamos el problema de PMD!)
  private static final String JUGADOR_ID_KEY = "jugadorId";
  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String ATRIBUTO_ERROR = "error";
  private static final String VISTA_INTERCAMBIO = "intercambio";

  private final ServicioMercado servicioMercado;
  private final RepositorioMercado repositorioMercado;

  public ControladorMercado(
    ServicioMercado servicioMercado,
    RepositorioMercado repositorioMercado
  ) {
    this.servicioMercado = servicioMercado;
    this.repositorioMercado = repositorioMercado;
  }

  @RequestMapping(value = "/mercado", method = RequestMethod.GET)
  public ModelAndView verMercadoComunidad(HttpSession session) {
    Long jugadorId = (Long) session.getAttribute(JUGADOR_ID_KEY);
    if (jugadorId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    Usuario usuario = repositorioMercado.buscarUsuarioPorId(jugadorId);
    List<PropuestaIntercambio> ofertas = servicioMercado.obtenerOfertasCompatibles(usuario);
    List<Carta> faltantes = servicioMercado.obtenerCartasFaltantes(usuario);

    // Obtenemos tus trades actuales
    List<PropuestaIntercambio> misTrades = servicioMercado.obtenerMisTrades(usuario);
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
    mav.addObject("misTrades", misTrades); // NUEVO: Pasamos la lista a la vista
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

    Usuario usuario = repositorioMercado.buscarUsuarioPorId(jugadorId);
    try {
      servicioMercado.publicarSolicitud(usuario, idCartaBuscada);
      return new ModelAndView("redirect:/mercado/mis-trades");
    } catch (Exception e) {
      ModelAndView mav = new ModelAndView(VISTA_INTERCAMBIO);
      mav.addObject(ATRIBUTO_ERROR, e.getMessage());

      mav.addObject("ofertas", servicioMercado.obtenerOfertasCompatibles(usuario));
      mav.addObject("cartasFaltantes", servicioMercado.obtenerCartasFaltantes(usuario));
      return mav;
    }
  }

  @RequestMapping(value = "/mercado/mis-trades", method = RequestMethod.GET)
  public ModelAndView verMisTrades(HttpSession session) {
    Long jugadorId = (Long) session.getAttribute(JUGADOR_ID_KEY);
    if (jugadorId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    Usuario usuario = repositorioMercado.buscarUsuarioPorId(jugadorId);
    List<PropuestaIntercambio> misTrades = servicioMercado.obtenerMisTrades(usuario);

    // Inicialización explícita de proxies Lazy para evitar la excepción y los warnings del IDE
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

    Usuario receptor = repositorioMercado.buscarUsuarioPorId(jugadorId);
    try {
      servicioMercado.finalizarIntercambio(receptor, idPropuesta, idCartaRecompensa);
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

    Usuario usuario = repositorioMercado.buscarUsuarioPorId(jugadorId);
    try {
      servicioMercado.eliminarMiTrade(usuario, idPropuesta);
      return new ModelAndView("redirect:/mercado/mis-trades");
    } catch (Exception e) {
      return new ModelAndView("redirect:/mercado/mis-trades?error=" + e.getMessage());
    }
  }
}
