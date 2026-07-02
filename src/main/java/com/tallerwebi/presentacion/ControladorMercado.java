package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Carta;
import com.tallerwebi.dominio.PropuestaIntercambio;
import com.tallerwebi.dominio.ServicioMercado;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorMercado {

  private final ServicioMercado servicioMercado;

  private static final String ATTRIBUTE_JUGADOR_ID = "jugadorId";
  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String VISTA_INTERCAMBIO = "intercambio";
  private static final String VISTA_MIS_TRADES = "mis-trades";
  private static final String VISTA_ELEGIR_RECOMPENSA = "elegir-recompensa";
  private static final String VISTA_DETALLE = "mercado-detalle-trade";

  @Autowired
  public ControladorMercado(ServicioMercado servicioMercado) {
    this.servicioMercado = servicioMercado;
  }

  // -----------------------------------------------------------------------
  // Pantalla principal: Ofertas de la Comunidad
  // -----------------------------------------------------------------------

  @RequestMapping(path = "/mercado", method = RequestMethod.GET)
  public ModelAndView verMercadoComunidad(HttpSession session) {
    final Long jugadorId = (Long) session.getAttribute(ATTRIBUTE_JUGADOR_ID);
    if (jugadorId == null) return new ModelAndView(REDIRECT_LOGIN);

    final ModelAndView mav = new ModelAndView(VISTA_INTERCAMBIO);
    cargarModeloMercado(mav, jugadorId);
    return mav;
  }

  // -----------------------------------------------------------------------
  // Publicar trade
  // -----------------------------------------------------------------------

  @RequestMapping(path = "/mercado/publicar", method = RequestMethod.POST)
  public ModelAndView procesarPublicarTrade(
    @RequestParam("idCartaBuscada") Long idCartaBuscada,
    HttpSession session
  ) {
    final Long jugadorId = (Long) session.getAttribute(ATTRIBUTE_JUGADOR_ID);
    if (jugadorId == null) return new ModelAndView(REDIRECT_LOGIN);

    try {
      servicioMercado.publicarSolicitud(jugadorId, idCartaBuscada);
      return new ModelAndView("redirect:/mercado/mis-trades");
    } catch (Exception e) {
      final ModelAndView mav = new ModelAndView(VISTA_INTERCAMBIO);
      cargarModeloMercado(mav, jugadorId);
      mav.addObject("error", e.getMessage());
      return mav;
    }
  }

  // -----------------------------------------------------------------------
  // Mis Trades: muestra tanto los trades publicados como los aceptados
  // -----------------------------------------------------------------------

  @RequestMapping(path = "/mercado/mis-trades", method = RequestMethod.GET)
  public ModelAndView verMisTrades(HttpSession session) {
    final Long jugadorId = (Long) session.getAttribute(ATTRIBUTE_JUGADOR_ID);
    if (jugadorId == null) return new ModelAndView(REDIRECT_LOGIN);

    final ModelAndView mav = new ModelAndView(VISTA_MIS_TRADES);
    // FIX: incluye propuestas donde soy emisor Y donde soy receptor
    mav.addObject("misTrades", servicioMercado.obtenerMisTrades(jugadorId));
    mav.addObject("tradesAceptados", servicioMercado.obtenerTradesAceptados(jugadorId));
    return mav;
  }

  // -----------------------------------------------------------------------
  // Aceptar trade: elegir recompensa
  // -----------------------------------------------------------------------

  @RequestMapping(path = "/mercado/aceptar", method = RequestMethod.GET)
  public ModelAndView iniciarAceptarTrade(@RequestParam("id") Long idTrade, HttpSession session) {
    final Long jugadorId = (Long) session.getAttribute(ATTRIBUTE_JUGADOR_ID);
    if (jugadorId == null) return new ModelAndView(REDIRECT_LOGIN);

    try {
      final PropuestaIntercambio propuesta = servicioMercado.buscarPorId(idTrade);
      final List<Carta> opciones = servicioMercado.obtenerOpcionesRecompensa(propuesta);

      final ModelAndView mav = new ModelAndView(VISTA_ELEGIR_RECOMPENSA);
      mav.addObject("trade", propuesta);
      mav.addObject("opciones", opciones != null ? opciones : new ArrayList<>());
      return mav;
    } catch (Exception e) {
      return new ModelAndView("redirect:/mercado?error=" + e.getMessage());
    }
  }

  // -----------------------------------------------------------------------
  // Confirmar intercambio
  // -----------------------------------------------------------------------

  @RequestMapping(path = "/mercado/confirmar", method = RequestMethod.POST)
  public ModelAndView confirmarTrade(
    @RequestParam("idTrade") Long idTrade,
    @RequestParam("idCartaRecompensa") Long idCartaRecompensa,
    HttpSession session
  ) {
    final Long jugadorId = (Long) session.getAttribute(ATTRIBUTE_JUGADOR_ID);
    if (jugadorId == null) return new ModelAndView(REDIRECT_LOGIN);

    try {
      servicioMercado.finalizarIntercambio(jugadorId, idTrade, idCartaRecompensa);
      return new ModelAndView("redirect:/mercado");
    } catch (Exception e) {
      return new ModelAndView("redirect:/mercado?error=" + e.getMessage());
    }
  }

  // -----------------------------------------------------------------------
  // Eliminar trade
  // -----------------------------------------------------------------------

  @RequestMapping(path = "/mercado/eliminar", method = RequestMethod.POST)
  public ModelAndView eliminarTrade(@RequestParam("id") Long idTrade, HttpSession session) {
    final Long jugadorId = (Long) session.getAttribute(ATTRIBUTE_JUGADOR_ID);
    if (jugadorId == null) return new ModelAndView(REDIRECT_LOGIN);

    try {
      servicioMercado.eliminarMiTrade(jugadorId, idTrade);
      return new ModelAndView("redirect:/mercado/mis-trades");
    } catch (Exception e) {
      return new ModelAndView("redirect:/mercado/mis-trades?error=" + e.getMessage());
    }
  }

  // -----------------------------------------------------------------------
  // FIX: Detalle de trade — ruta alineada con PathVariable que usa la vista
  // La vista genera: /mercado/mis-trades/detalle/{id}
  // -----------------------------------------------------------------------

  @RequestMapping(path = "/mercado/mis-trades/detalle/{id}", method = RequestMethod.GET)
  public ModelAndView verDetalleTrade(@PathVariable("id") Long idTrade, HttpSession session) {
    final Long jugadorId = (Long) session.getAttribute(ATTRIBUTE_JUGADOR_ID);
    if (jugadorId == null) return new ModelAndView(REDIRECT_LOGIN);

    try {
      final PropuestaIntercambio propuesta = servicioMercado.buscarPorId(idTrade);

      if (propuesta == null) {
        return new ModelAndView("redirect:/mercado/mis-trades?error=Trade no encontrado");
      }

      final ModelAndView mav = new ModelAndView(VISTA_DETALLE);

      // Determinar si el jugador logueado es el emisor o el receptor
      final boolean soyEmisor =
        propuesta.getUsuarioEmisor() != null && servicioMercado.esEmisor(jugadorId, propuesta);

      if (soyEmisor) {
        // Soy el que publicó: di la cartaBuscada, recibí la cartaOfrecida
        mav.addObject("cartaEntregada", propuesta.getCartaBuscada());
        mav.addObject("cartaRecibida", propuesta.getCartaOfrecida());
        mav.addObject(
          "usuarioCoincidencia",
          propuesta.getUsuarioReceptor() != null ? propuesta.getUsuarioReceptor().getEmail() : ""
        );
      } else {
        // Soy el receptor: di la cartaOfrecida, recibí la cartaBuscada
        mav.addObject("cartaEntregada", propuesta.getCartaOfrecida());
        mav.addObject("cartaRecibida", propuesta.getCartaBuscada());
        mav.addObject("usuarioCoincidencia", propuesta.getUsuarioEmisor().getEmail());
      }

      return mav;
    } catch (Exception e) {
      return new ModelAndView("redirect:/mercado/mis-trades?error=" + e.getMessage());
    }
  }

  // -----------------------------------------------------------------------
  // Helper privado
  // -----------------------------------------------------------------------

  private void cargarModeloMercado(ModelAndView mav, Long jugadorId) {
    final List<PropuestaIntercambio> ofertas = servicioMercado.obtenerOfertasCompatibles(jugadorId);
    final List<Carta> faltantes = servicioMercado.obtenerCartasFaltantes(jugadorId);
    final List<PropuestaIntercambio> misTrades = servicioMercado.obtenerMisTrades(jugadorId);

    final List<Long> idsCartasQuePuedoDar = new ArrayList<>();
    if (ofertas != null) {
      for (PropuestaIntercambio o : ofertas) {
        if (o.getCartaBuscada() != null) {
          final Long idCarta = o.getCartaBuscada().getId();
          if (servicioMercado.usuarioTieneCartaRepetida(jugadorId, idCarta)) {
            idsCartasQuePuedoDar.add(idCarta);
          }
        }
      }
    }

    mav.addObject("ofertas", ofertas != null ? ofertas : new ArrayList<>());
    mav.addObject("faltantes", faltantes != null ? faltantes : new ArrayList<>());
    mav.addObject("misTrades", misTrades != null ? misTrades : new ArrayList<>());
    mav.addObject("idsCartasQuePuedoDar", idsCartasQuePuedoDar);
  }
}
