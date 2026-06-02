package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Partida;
import com.tallerwebi.dominio.ServicioPartida;
import com.tallerwebi.dominio.Usuario;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorPartida {

  private ServicioPartida servicioPartida;

  @Autowired
  public ControladorPartida(ServicioPartida servicioPartida) {
    this.servicioPartida = servicioPartida;
  }

  @GetMapping("/seleccionar-zona")
  public ModelAndView irASeleccionDeZona() {
    return new ModelAndView("seleccion-zona");
  }

  @PostMapping("/iniciar-combate")
  public ModelAndView iniciarCombate(
    @RequestParam("zona") String zona,
    HttpServletRequest request
  ) {
    Usuario usuarioLogueado = (Usuario) request.getSession().getAttribute("USUARIO");

    if (usuarioLogueado == null) {
      usuarioLogueado = new Usuario();
    }

    Partida partida = servicioPartida.iniciarPartida(usuarioLogueado, zona);
    request.getSession().setAttribute("PARTIDA_ACTUAL", partida);

    return new ModelAndView("redirect:/home");
  }

  @RequestMapping(path = "/seleccion-zona", method = RequestMethod.GET)
  public ModelAndView elegirZona() {
    return new ModelAndView("seleccion-zona");
  }
}
