package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Carta;
import com.tallerwebi.dominio.PropuestaIntercambio;
import com.tallerwebi.dominio.ServicioMercado;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorMercado {

  private final ServicioMercado servicioMercado;

  // Constantes descriptivas para evitar literals repetidos y hardcodeados
  private static final String ATTRIBUTE_JUGADOR_ID = "jugadorId";
  private static final Long ID_FILTRO_DETALLE = 1L;
  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String VISTA_INTERCAMBIO = "intercambio";
  private static final String VISTA_MIS_TRADES = "mis-trades";
  private static final String VISTA_DETALLE = "mercado-detalle-trade";
  private static final String VISTA_ELEGIR_RECOMPENSA = "elegir-recompensa";

  @Autowired
  public ControladorMercado(ServicioMercado servicioMercado) {
    this.servicioMercado = servicioMercado;
  }

  @RequestMapping(path = "/mercado", method = RequestMethod.GET)
  public ModelAndView verMercadoComunidad(HttpSession session) {
    Long jugadorId = (Long) session.getAttribute(ATTRIBUTE_JUGADOR_ID);
    if (jugadorId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    ModelAndView mav = new ModelAndView(VISTA_INTERCAMBIO);
    cargarModeloMercado(mav, jugadorId);
    return mav;
  }

  @RequestMapping(path = "/mercado/publicar", method = RequestMethod.POST)
  public ModelAndView procesarPublicarTrade(
    @RequestParam("cartaBuscadaId") Long cartaBuscadaId,
    HttpSession session
  ) {
    Long jugadorId = (Long) session.getAttribute(ATTRIBUTE_JUGADOR_ID);
    if (jugadorId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      servicioMercado.publicarSolicitud(jugadorId, cartaBuscadaId);
      return new ModelAndView("redirect:/mercado/mis-trades");
    } catch (Exception e) {
      ModelAndView mav = new ModelAndView(VISTA_INTERCAMBIO);
      cargarModeloMercado(mav, jugadorId);
      mav.addObject("error", e.getMessage());
      return mav;
    }
  }

  @RequestMapping(path = "/mercado/mis-trades", method = RequestMethod.GET)
  public ModelAndView verMisTrades(HttpSession session) {
    Long jugadorId = (Long) session.getAttribute(ATTRIBUTE_JUGADOR_ID);
    if (jugadorId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    List<PropuestaIntercambio> misTrades = servicioMercado.obtenerMisTrades(jugadorId);
    ModelAndView mav = new ModelAndView(VISTA_MIS_TRADES);
    mav.addObject("misTrades", misTrades);
    return mav;
  }

  @RequestMapping(path = "/mercado/aceptar", method = RequestMethod.GET)
  public ModelAndView iniciarAceptarTrade(@RequestParam("id") Long idTrade, HttpSession session) {
    Long jugadorId = (Long) session.getAttribute(ATTRIBUTE_JUGADOR_ID);
    if (jugadorId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      PropuestaIntercambio propuesta = servicioMercado.buscarPorId(idTrade);
      List<Carta> opciones = servicioMercado.obtenerOpcionesRecompensa(propuesta);

      ModelAndView mav = new ModelAndView(VISTA_ELEGIR_RECOMPENSA);
      mav.addObject("trade", propuesta);
      mav.addObject("opciones", opciones);
      return mav;
    } catch (Exception e) {
      return new ModelAndView("redirect:/mercado?error=" + e.getMessage());
    }
  }

  @RequestMapping(path = "/mercado/confirmar", method = RequestMethod.POST)
  public ModelAndView confirmarTrade(
    @RequestParam("idTrade") Long idTrade,
    @RequestParam("idCartaRecompensa") Long idCartaRecompensa,
    HttpSession session
  ) {
    Long jugadorId = (Long) session.getAttribute(ATTRIBUTE_JUGADOR_ID);
    if (jugadorId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      servicioMercado.finalizarIntercambio(jugadorId, idTrade, idCartaRecompensa);
      return new ModelAndView("redirect:/mercado");
    } catch (Exception e) {
      return new ModelAndView("redirect:/mercado?error=" + e.getMessage());
    }
  }

  @RequestMapping(path = "/mercado/eliminar", method = RequestMethod.POST)
  public ModelAndView eliminarTrade(@RequestParam("id") Long idTrade, HttpSession session) {
    Long jugadorId = (Long) session.getAttribute(ATTRIBUTE_JUGADOR_ID);
    if (jugadorId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      servicioMercado.eliminarMiTrade(jugadorId, idTrade);
      return new ModelAndView("redirect:/mercado/mis-trades");
    } catch (Exception e) {
      return new ModelAndView("redirect:/mercado/mis-trades?error=" + e.getMessage());
    }
  }

  @RequestMapping(path = "/mercado/detalle", method = RequestMethod.GET)
  public ModelAndView verDetalleTrade(@RequestParam("id") Long idTrade, HttpSession session) {
    Long jugadorId = (Long) session.getAttribute(ATTRIBUTE_JUGADOR_ID);
    if (jugadorId == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    try {
      PropuestaIntercambio propuesta = servicioMercado.buscarPorId(idTrade);
      ModelAndView mav = new ModelAndView(VISTA_DETALLE);
      mav.addObject("trade", propuesta);

      if (
        propuesta.getUsuarioReceptor() != null &&
        propuesta.getUsuarioReceptor().getId().equals(ID_FILTRO_DETALLE)
      ) {
        mav.addObject("usuarioCoincidencia", propuesta.getUsuarioEmisor().getEmail());
      } else {
        mav.addObject("usuarioCoincidencia", propuesta.getUsuarioReceptor().getEmail());
      }

      return mav;
    } catch (Exception e) {
      return new ModelAndView("redirect:/mercado?error=" + e.getMessage());
    }
  }

  private void cargarModeloMercado(ModelAndView mav, Long jugadorId) {
    List<PropuestaIntercambio> ofertas = servicioMercado.obtenerOfertasCompatibles(jugadorId);
    List<Carta> faltantes = servicioMercado.obtenerCartasFaltantes(jugadorId);
    List<PropuestaIntercambio> misTrades = servicioMercado.obtenerMisTrades(jugadorId);

    List<Long> idsCartasQuePuedoDar = new ArrayList<>();
    for (PropuestaIntercambio o : ofertas) {
      if (
        o.getCartaBuscada() != null &&
        servicioMercado.usuarioTieneCartaRepetida(jugadorId, o.getCartaBuscada().getId())
      ) {
        idsCartasQuePuedoDar.add(o.getCartaBuscada().getId());
      }
    }

    mav.addObject("ofertas", ofertas);
    mav.addObject("faltantes", faltantes);
    mav.addObject("misTrades", misTrades);
    mav.addObject("idsCartasQuePuedoDar", idsCartasQuePuedoDar);
  }
}
