package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Partida;
import com.tallerwebi.dominio.ServicioPartida;
import com.tallerwebi.dominio.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ControladorPartida {

    private ServicioPartida servicioPartida;

    @Autowired
    public ControladorPartida(ServicioPartida servicioPartida) {
        this.servicioPartida = servicioPartida;
    }

    @GetMapping("/seleccionar-zona")
    public ModelAndView irASeleccionDeZona() {
        ModelMap modelo = new ModelMap();
        return new ModelAndView("seleccion-zona", modelo);
    }

    @PostMapping("/iniciar-combate")
    public ModelAndView iniciarCombate(@RequestParam("zona") String zona, HttpServletRequest request) {
        // Simulamos agarrar al usuario de la sesión
        Usuario usuarioLogueado = (Usuario) request.getSession().getAttribute("USUARIO");

        // Si no hay usuario logueado, por ahora creamos uno vacío para que no explote
        if (usuarioLogueado == null) {
            usuarioLogueado = new Usuario();
        }

        Partida partida = servicioPartida.iniciarPartida(usuarioLogueado, zona);
        request.getSession().setAttribute("PARTIDA_ACTUAL", partida);

        // Por ahora redirigimos al home, después lo cambian a la vista de combate real
        return new ModelAndView("redirect:/home");
    }
}