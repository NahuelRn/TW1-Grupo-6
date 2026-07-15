package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Carta;
import com.tallerwebi.dominio.HistorialPartida;
import com.tallerwebi.dominio.Partida;
import com.tallerwebi.dominio.RecompensaDTO;
import com.tallerwebi.dominio.ServicioCalculoRecompensa;
import com.tallerwebi.dominio.ServicioCarta;
import com.tallerwebi.dominio.ServicioCombate;
import com.tallerwebi.dominio.ServicioHistorial;
import com.tallerwebi.dominio.ServicioPartida;
import com.tallerwebi.dominio.ServicioUsuario;
import com.tallerwebi.dominio.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorCombate {

    private final ServicioCombate servicioCombate;
    private final ServicioPartida servicioPartida;
    private final ServicioUsuario servicioUsuario;
    private final ServicioCalculoRecompensa servicioCalculoRecompensa;
    private final ServicioHistorial servicioHistorial;
    private ServicioCarta servicioCarta;

    private static final String SESSION_IDS_MANO = "idsMano";
    private static final String SESSION_MAZO_ROBO = "idsMazoRobo";
    private static final String SESSION_ID_PARTIDA = "idPartidaActiva";
    private static final String SESSION_USUARIO_ID = "USUARIO_ID";

    @Autowired
    public ControladorCombate(ServicioCombate servicioCombate, ServicioPartida servicioPartida, ServicioUsuario servicioUsuario, ServicioCalculoRecompensa servicioCalculoRecompensa, ServicioHistorial servicioHistorial, ServicioCarta servicioCarta) {
        this.servicioCombate = servicioCombate;
        this.servicioPartida = servicioPartida;
        this.servicioUsuario = servicioUsuario;
        this.servicioCalculoRecompensa = servicioCalculoRecompensa;
        this.servicioHistorial = servicioHistorial;
        this.servicioCarta = servicioCarta;
    }

    @RequestMapping(path = "/combate", method = RequestMethod.GET)
    public ModelAndView iniciarCombate(@RequestParam(value = "zona", required = false, defaultValue = "bosque") String zona, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Long idUsuario = (Long) session.getAttribute(SESSION_USUARIO_ID);

        if (idUsuario == null) {
            return new ModelAndView("redirect:/login");
        }

        Usuario usuarioReal = servicioUsuario.buscarPorId(idUsuario);
        if (usuarioReal.getMazoActivo() == null || usuarioReal.getMazoActivo().getCartas().isEmpty()) {
            return new ModelAndView("lobby", new ModelMap("error", "Debes equipar un Mazo Activo."));
        }

        Long idPartidaActiva = (Long) session.getAttribute(SESSION_ID_PARTIDA);

        if (idPartidaActiva != null) {
            return retomarCombate(idPartidaActiva, usuarioReal, zona, session);
        } else {
            return crearNuevoCombate(usuarioReal, zona, session);
        }
    }

    @RequestMapping(path = "/jugar-carta", method = RequestMethod.POST)
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public ModelAndView jugarCarta(@RequestParam Long idCarta, @RequestParam Long idPartida, @RequestParam String zona, HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Long idUsuario = (Long) session.getAttribute(SESSION_USUARIO_ID);

        String logCombate = servicioCombate.jugarTurno(idPartida, idCarta);
        Partida partida = servicioCombate.obtenerPartidaPorIdentificador(idPartida);
        Usuario usuarioReal = servicioUsuario.buscarPorId(idUsuario);

        List<Long> idsMano = (List<Long>) session.getAttribute(SESSION_IDS_MANO);
        List<Long> idsMazoRobo = (List<Long>) session.getAttribute(SESSION_MAZO_ROBO);
        List<Carta> manoActual;

        if (idsMano != null) {
            servicioPartida.gestionarRoboDeCarta(idCarta, idsMano, idsMazoRobo);

            session.setAttribute(SESSION_IDS_MANO, idsMano);
            session.setAttribute(SESSION_MAZO_ROBO, idsMazoRobo);

            manoActual = usuarioReal.getMazoActivo().getCartas().stream().filter(c -> idsMano.contains(c.getId())).collect(Collectors.toList());
            partida.setManoJugador(manoActual);
        } else {
            manoActual = new ArrayList<>();
        }

        Boolean sinCartas = manoActual.isEmpty() && (idsMazoRobo == null || idsMazoRobo.isEmpty());

        if (partida.getHpJugador() <= 0 || partida.getHpEnemigo() <= 0 || sinCartas) {
            //      session.removeAttribute(SESSION_ID_PARTIDA);
            session.removeAttribute(SESSION_IDS_MANO);
            session.removeAttribute(SESSION_MAZO_ROBO);

            ModelMap modelMap = new ModelMap();
            modelMap.put("partida", partida);

            if (partida.getHpEnemigo() <= 0) {
                RecompensaDTO recompensaDTO = this.servicioCalculoRecompensa.obtenerRecompensa(partida);
                guardarPartidaEnHistorial(partida, usuarioReal, "VICTORIA", recompensaDTO);

                modelMap.put("recompensa", recompensaDTO);
                return new ModelAndView("recompensas", modelMap);
            } else {
                RecompensaDTO recompensaDTO = this.servicioCalculoRecompensa.obtenerRecompensa(partida);
                guardarPartidaEnHistorial(partida, usuarioReal, "DERROTA", recompensaDTO);

                String motivoDerrota;
                if (sinCartas) {
                    motivoDerrota = "¡Te quedaste sin cartas y el enemigo resistió!";
                } else {
                    motivoDerrota = "¡Tus puntos de vida llegaron a cero!";
                }

                Carta ultimaCartaJugada = this.servicioCarta.buscarPorId(idCarta);

                modelMap.put("motivoDerrota", motivoDerrota);
                modelMap.put("logCombate", logCombate);
                modelMap.put("ultimaCartaJugada", ultimaCartaJugada);

                return new ModelAndView("game-over", modelMap);
            }
        }

        return armarVistaCombate(partida, manoActual, idsMazoRobo, zona, logCombate);
    }

    private ModelAndView retomarCombate(Long idPartidaActiva, Usuario usuarioReal, String zona, HttpSession session) {
        Partida partida = servicioCombate.obtenerPartidaPorIdentificador(idPartidaActiva);
        List<Long> idsMano = (List<Long>) session.getAttribute(SESSION_IDS_MANO);
        List<Long> idsMazoRobo = (List<Long>) session.getAttribute(SESSION_MAZO_ROBO);

        List<Carta> manoActual = (idsMano != null) ? usuarioReal.getMazoActivo().getCartas().stream().filter(c -> idsMano.contains(c.getId())).collect(Collectors.toList()) : new ArrayList<>();

        partida.setManoJugador(manoActual);

        return armarVistaCombate(partida, manoActual, idsMazoRobo, zona, "Retomas el combate.");
    }

    private ModelAndView crearNuevoCombate(Usuario usuarioReal, String zona, HttpSession session) {
        Partida partida = servicioPartida.iniciarPartida(usuarioReal, zona);
        session.setAttribute(SESSION_ID_PARTIDA, partida.getId());

        List<Carta> manoActual = partida.getManoJugador();
        List<Carta> mazoRestante = partida.getMazoRestante();

        List<Long> idsMano = manoActual.stream().map(Carta::getId).collect(Collectors.toList());
        List<Long> idsMazoRobo = (mazoRestante != null) ? mazoRestante.stream().map(Carta::getId).collect(Collectors.toList()) : new ArrayList<>();

        session.setAttribute(SESSION_IDS_MANO, idsMano);
        session.setAttribute(SESSION_MAZO_ROBO, idsMazoRobo);

        return armarVistaCombate(partida, manoActual, idsMazoRobo, zona, "¡Comienza el combate!");
    }

    private ModelAndView armarVistaCombate(Partida partida, List<Carta> manoActual, List<Long> idsMazoRobo, String zona, String logCombate) {
        ModelMap modelo = new ModelMap();
        modelo.put("mano", manoActual);
        modelo.put("cartasEnMazo", idsMazoRobo != null ? idsMazoRobo.size() : 0);
        modelo.put("partida", partida);
        modelo.put("logCombate", logCombate);
        modelo.put("configZona", servicioCombate.obtenerConfiguracionZona(zona));
        modelo.put("zona", zona);
        return new ModelAndView("combate", modelo);
    }

    @SuppressWarnings("PMD.UnusedFormalParameter")
    private void guardarPartidaEnHistorial(Partida partida, Usuario usuario, String resultado, RecompensaDTO recompensaDTO) {
        HistorialPartida historialPartida = new HistorialPartida();
        historialPartida.setUsuario(usuario);
        historialPartida.setResultado(resultado);

        if (recompensaDTO != null) {
            historialPartida.setOroGanado(recompensaDTO.getOro());
            historialPartida.setExperienciaGanada(recompensaDTO.getExperiencia());
        } else {
            historialPartida.setOroGanado(0);
            historialPartida.setExperienciaGanada(0);
        }

        this.servicioHistorial.guardarHistorialPartidaServicio(historialPartida);
    }
}
