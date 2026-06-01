package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ItemInventario;
import com.tallerwebi.dominio.ServicioIntercambio;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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

    // ✅ fix DataflowAnomalyAnalysis en 'modelo': se construye y se usa sin ramas muertas
    List<ItemInventario> inventario = servicioIntercambio.obtenerInventario(jugadorId);
    ModelMap modelo = new ModelMap();
    modelo.put("inventario", inventario);
    return new ModelAndView("contrato-mejora", modelo);
  }

  @RequestMapping(path = "/contrato-mejora/intercambiar", method = RequestMethod.POST)
  public ModelAndView intercambiar(@RequestParam("ids") List<Long> ids, HttpSession session) {
    Long jugadorId = (Long) session.getAttribute("jugadorId");
    if (jugadorId == null) {
      return new ModelAndView("redirect:/login");
    }

    try {
      // ✅ fix UnusedLocalVariable 'premio': no guardamos el resultado, solo lo ejecutamos
      servicioIntercambio.realizarMejora(ids);
      return new ModelAndView("redirect:/lobby");
    } catch (Exception e) {
      List<ItemInventario> inventario = servicioIntercambio.obtenerInventario(jugadorId);
      // ✅ fix DataflowAnomalyAnalysis en 'modelo': se construye solo en el catch
      ModelMap modelo = new ModelMap();
      modelo.put("inventario", inventario);
      modelo.put("error", e.getMessage());
      return new ModelAndView("contrato-mejora", modelo);
    }
  }
}
