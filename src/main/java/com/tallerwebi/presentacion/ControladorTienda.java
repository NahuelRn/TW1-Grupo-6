package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.RepositorioCarta;
import com.tallerwebi.dominio.ServicioTienda;
import com.tallerwebi.dominio.Usuario;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorTienda {

  private ServicioTienda servicioTienda;
  private RepositorioCarta repositorioCarta;

  @Autowired
  public ControladorTienda(ServicioTienda servicioTienda, RepositorioCarta repositorioCarta) {
    this.servicioTienda = servicioTienda;
    this.repositorioCarta = repositorioCarta;
  }

  @RequestMapping(path = "/tienda", method = RequestMethod.GET)
  public ModelAndView verTienda(HttpServletRequest httpServletRequest) {
    Usuario usuario = (Usuario) httpServletRequest.getSession().getAttribute("USUARIO");

    if (usuario == null) {
      return new ModelAndView("redirect:/login");
    }

    ModelMap modelMap = new ModelMap();
    modelMap.put("cartas", this.servicioTienda.listarCartas());

    return new ModelAndView("tienda", modelMap);
  }

  @RequestMapping(path = "/comprar-carta/{identificadorCarta}", method = RequestMethod.POST)
  public ModelAndView realizarCompra(
    @PathVariable("identificadorCarta") Long identificadorCarta,
    HttpServletRequest httpServletRequest
  ) {
    Usuario usuario = (Usuario) httpServletRequest.getSession().getAttribute("USUARIO");

    if (usuario == null) {
      return new ModelAndView("redirect:/login");
    }

    ModelMap modelMap = new ModelMap();
    try {
      this.servicioTienda.comprarCarta(usuario, identificadorCarta);
      modelMap.put("mensaje", "¡Carta comprada con éxito!");
      httpServletRequest.getSession().setAttribute("USUARIO", usuario);
    } catch (Exception exception) {
      modelMap.put("error", exception.getMessage());
    }

    return new ModelAndView("tienda", modelMap);
  }
}
