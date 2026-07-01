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

  // Ahora el controlador atiende el filtro enviado por parámetro de URL
  @GetMapping("/contrato-mejora")
  public ModelAndView verContratoMejora(
    @RequestParam(value = "rareza", required = false, defaultValue = "all") String rareza,
    HttpSession session
  ) {
    Long jugadorId = (Long) session.getAttribute("jugadorId");
    if (jugadorId == null) {
      return new ModelAndView("redirect:/login");
    }

    List<ItemInventario> inventario = servicioIntercambio.obtenerInventarioFiltrado(
      jugadorId,
      rareza
    );
    ModelMap modelo = new ModelMap();
    modelo.put("inventario", inventario);
    modelo.put("rarezaSeleccionada", rareza); // Guardamos cuál se filtró para la UI
    return new ModelAndView("contrato-mejora", modelo);
  }

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

    if (ids == null || ids.isEmpty()) {
      redirectAttributes.addFlashAttribute(
        "error",
        "Debes seleccionar exactamente 4 cartas para firmar el contrato."
      );
      return new ModelAndView("redirect:/contrato-mejora");
    }

    try {
      Carta cartaGanada = servicioIntercambio.realizarMejora(jugadorId, ids);
      redirectAttributes.addFlashAttribute("cartaPremio", cartaGanada);
      return new ModelAndView("redirect:/contrato-mejora/resultado");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return new ModelAndView("redirect:/contrato-mejora");
    }
  }

  @GetMapping("/contrato-mejora/resultado")
  public ModelAndView verResultado(HttpSession session) {
    Long jugadorId = (Long) session.getAttribute("jugadorId");
    if (jugadorId == null) {
      return new ModelAndView("redirect:/login");
    }
    return new ModelAndView("resultado-mejora");
  }
}
