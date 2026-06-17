package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.HistorialPartida;
import com.tallerwebi.dominio.ServicioHistorial;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorHistorialPartida {

  private ServicioHistorial servicioHistorial;

  @Autowired
  public ControladorHistorialPartida(ServicioHistorial servicioHistorial) {
    this.servicioHistorial = servicioHistorial;
  }

  @RequestMapping(path = "/historialPartida", method = RequestMethod.GET)
  public ModelAndView mostrarHistorialPartida(@RequestParam Long identificadorUsuario) {
    List<HistorialPartida> historialPartidas =
      this.servicioHistorial.listarHistorialPorUsuario(identificadorUsuario);

    ModelMap modelMap = new ModelMap();
    modelMap.put("historialPartidas", historialPartidas);

    return new ModelAndView("historial", modelMap);
  }
}
