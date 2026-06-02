package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Carta;
import com.tallerwebi.dominio.ItemInventario;
import com.tallerwebi.dominio.ServicioIntercambio;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ControladorIntercambio {

  private final ServicioIntercambio servicioIntercambio;

  public ControladorIntercambio(ServicioIntercambio servicioIntercambio) {
    this.servicioIntercambio = servicioIntercambio;
  }

  @RequestMapping(path = "/contrato-mejora", method = RequestMethod.GET)
  public ModelAndView irAIntercambio(HttpSession session) {
    Long jugadorId = (Long) session.getAttribute("jugadorId");
    if (jugadorId == null) {
      return new ModelAndView("redirect:/login");
    }

    List<ItemInventario> inventario = servicioIntercambio.obtenerInventario(jugadorId);
    ModelMap modelo = new ModelMap();
    modelo.put("inventario", inventario);
    return new ModelAndView("contrato-mejora", modelo);
  }

  @RequestMapping(path = "/contrato-mejora/intercambiar", method = RequestMethod.POST)
  public ModelAndView intercambiar(
    @RequestParam(value = "ids", required = false) List<Long> ids,
    HttpSession session,
    RedirectAttributes redirectAttributes // Lo sumamos para pasar el premio a la siguiente pantalla sin perderlo
  ) {
    Long jugadorId = (Long) session.getAttribute("jugadorId");
    if (jugadorId == null) {
      return new ModelAndView("redirect:/login");
    }

    List<Long> idsSeleccionados = (ids != null) ? ids : new ArrayList<>();

    try {
      // SOLUCIÓN: Pasamos 'jugadorId' como pide el servicio y guardamos la carta obtenida
      Carta cartaGanada = servicioIntercambio.realizarMejora(jugadorId, idsSeleccionados);

      // Enviamos la carta ganada de forma segura a la vista de éxito
      redirectAttributes.addFlashAttribute("cartaPremio", cartaGanada);

      // Redirigimos al endpoint de resultados en vez de mandarlo directo al lobby
      return new ModelAndView("redirect:/contrato-mejora/resultado");
    } catch (Exception e) {
      // Si falla, se mantiene tu lógica original que recarga el inventario y muestra el error
      List<ItemInventario> inventario = servicioIntercambio.obtenerInventario(jugadorId);
      ModelMap modelo = new ModelMap();
      modelo.put("inventario", inventario);
      modelo.put("error", e.getMessage());
      return new ModelAndView("contrato-mejora", modelo);
    }
  }

  // Agregamos este endpoint para renderizar la vista de la recompensa (resultado-mejora.html)
  @RequestMapping(path = "/contrato-mejora/resultado", method = RequestMethod.GET)
  public ModelAndView verResultado(HttpSession session) {
    Long jugadorId = (Long) session.getAttribute("jugadorId");
    if (jugadorId == null) {
      return new ModelAndView("redirect:/login");
    }
    return new ModelAndView("resultado-mejora");
  }
}
