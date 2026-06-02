package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Carta;
import com.tallerwebi.dominio.ItemInventario;
import com.tallerwebi.dominio.ServicioIntercambio;
import java.util.List;
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

  // Aquí tienes tu método actual que renderiza la vista
  @GetMapping("/contrato-mejora")
  public ModelAndView verContratoMejora() {
    ModelMap modelo = new ModelMap();
    Long jugadorId = 1L; // ID de prueba
    List<ItemInventario> inventario = servicioIntercambio.obtenerInventario(jugadorId);
    modelo.put("inventario", inventario);
    return new ModelAndView("contrato-mejora", modelo);
  }

  // =========================================================================
  // ¡AQUÍ EXACTAMENTE VA EL PUNTO 3! (Justo abajo del método de arriba)
  // =========================================================================
  @PostMapping("/contrato-mejora/intercambiar")
  public ModelAndView intercambiarCartas(
    @RequestParam(value = "ids", required = false) List<Long> ids,
    RedirectAttributes redirectAttributes
  ) {
    // Validación en caso de que el usuario presione el botón sin marcar nada
    if (ids == null || ids.isEmpty()) {
      redirectAttributes.addFlashAttribute(
        "error",
        "Debes seleccionar exactamente 4 cartas para firmar el contrato."
      );
      return new ModelAndView("redirect:/contrato-mejora");
    }

    try {
      // ID temporal de pruebas (En el futuro lo sacarás de 'request.getSession().getAttribute("userId")')
      Long jugadorId = 1L;

      // LLAMADA AL SERVICIO: Le pasamos el jugador y los dados seleccionados
      Carta cartaGanada = servicioIntercambio.realizarMejora(jugadorId, ids);

      // Guardamos el premio para mostrarlo en la siguiente pantalla
      redirectAttributes.addFlashAttribute("cartaPremio", cartaGanada);
      return new ModelAndView("redirect:/contrato-mejora/resultado");
    } catch (Exception e) {
      // Si el servicio tira excepción (ej: rarezas distintas), el error vuelve a la pantalla principal
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return new ModelAndView("redirect:/contrato-mejora");
    }
  }

  // El método que simplemente muestra la vista del premio final
  @GetMapping("/contrato-mejora/resultado")
  public ModelAndView verResultado() {
    return new ModelAndView("resultado-mejora");
  }
}
