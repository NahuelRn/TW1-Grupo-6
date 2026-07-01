package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Partida;
import com.tallerwebi.dominio.RecompensaDTO;
import com.tallerwebi.dominio.ServicioCalculoRecompensa;
import com.tallerwebi.dominio.ServicioUsuario;
import com.tallerwebi.dominio.Usuario;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorRecompensas {

    @Autowired
    private ServicioUsuario servicioUsuario;

    @Autowired
    private ServicioCalculoRecompensa servicioCalculoRecompensa;

    @RequestMapping("/recompensas")
    public ModelAndView recompensas(HttpServletRequest request) {
        Partida partida = (Partida) request.getSession().getAttribute("PARTIDA_ACTUAL");

        if (partida == null) {
            return new ModelAndView("redirect:/combate");
        }

        ModelMap modelMap = new ModelMap();

        RecompensaDTO recompensa = this.servicioCalculoRecompensa.obtenerRecompensa(partida);

        modelMap.put("recompensa", recompensa);

        return new ModelAndView("recompensas", modelMap);
    }

    @RequestMapping(value = "/reclamar-recompensa", method = RequestMethod.POST)
    public ModelAndView reclamar(HttpServletRequest httpServletRequest) {
        Usuario usuario = (Usuario) httpServletRequest.getSession().getAttribute("USUARIO");

        if (usuario == null) {
            return new ModelAndView("redirect:/login");
        }

        Partida partida = (Partida) httpServletRequest.getSession().getAttribute("PARTIDA_ACTUAL");
        this.servicioUsuario.aplicarRecompensa(usuario, partida);

        httpServletRequest.getSession().setAttribute("USUARIO", usuario);

        return new ModelAndView("redirect:/lobby");
    }
}
