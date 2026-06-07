package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Carta;
import com.tallerwebi.dominio.ItemInventario;
import com.tallerwebi.dominio.ServicioIntercambio;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ControladorContratoMejora {

  private final ServicioIntercambio servicioIntercambio;

  @Autowired
  public ControladorContratoMejora(ServicioIntercambio servicioIntercambio) {
    this.servicioIntercambio = servicioIntercambio;
  }

  // Muestra la pantalla del Contrato con el inventario del jugador en sesión
  @GetMapping("/contrato-mejora")
  public ModelAndView verContratoMejora(HttpSession session) {
    Long jugadorId = (Long) session.getAttribute("jugadorId");
    if (jugadorId == null) {
      return new ModelAndView("redirect:/login");
    }

    List<ItemInventario> inventario = servicioIntercambio.obtenerInventario(jugadorId);
    ModelMap modelo = new ModelMap();
    modelo.put("inventario", inventario);
    return new ModelAndView("contrato-mejora", modelo);
  }

  // Procesa el intercambio al presionar "FIRMAR CONTRATO"
  @PostMapping("/contrato-mejora/intercambiar")
  public ModelAndView intercambiarCartas(
    @RequestParam(value = "ids", required = false) List<Long> ids,
    HttpSession session,
    RedirectAttributes redirectAttributes
  ) {
    Long jugadorId = (Long) session.getAttribute("jugadorId");
    if (jugadorId == null) {
      return new ModelAndView("redirect:/login");
    }

    // Validación temprana por si mandan el formulario vacío
    if (ids == null || ids.isEmpty()) {
      redirectAttributes.addFlashAttribute(
        "error",
        "Debes seleccionar exactamente 4 cartas para firmar el contrato."
      );
      return new ModelAndView("redirect:/contrato-mejora");
    }

    try {
      // LLAMADA AL SERVICIO (quita las 4 viejas, genera la nueva)
      Carta cartaGanada = servicioIntercambio.realizarMejora(jugadorId, ids);

      // Envío del premio
      redirectAttributes.addFlashAttribute("cartaPremio", cartaGanada);

      // Redirige al flujo de éxito
      return new ModelAndView("redirect:/contrato-mejora/resultado");
    } catch (Exception e) {
      // Si el servicio tira error (ej: menos de 4 cartas o raras mezcladas), vuelve atrás mostrando el porqué
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return new ModelAndView("redirect:/contrato-mejora");
    }
  }

  // Muestra la pantalla "resultado-mejora" con la nueva carta obtenida
  @GetMapping("/contrato-mejora/resultado")
  public ModelAndView verResultado(HttpSession session) {
    Long jugadorId = (Long) session.getAttribute("jugadorId");
    if (jugadorId == null) {
      return new ModelAndView("redirect:/login");
    }
    return new ModelAndView("resultado-mejora");
  }
}
