package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ControladorColeccionTest {

    private ControladorColeccion controladorColeccion;
    private ServicioCarta servicioCarta;
    private RepositorioInventario repositorioInventario;
    private HttpServletRequest request;
    private HttpSession session;

    @Before
    public void init() {
        servicioCarta = mock(ServicioCarta.class);
        repositorioInventario = mock(RepositorioInventario.class);
        request = mock(HttpServletRequest.class);
        session = mock(HttpSession.class);

        controladorColeccion = new ControladorColeccion(servicioCarta, repositorioInventario);
    }

    @Test
    public void alIrACollecionSeCarganLasCartasEnElModelo() {
        // Preparación
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("JUGADOR_ID")).thenReturn(1L);
        when(servicioCarta.obtenerTodas()).thenReturn(new ArrayList<>());
        when(repositorioInventario.listarInventarioDeJugador(1L)).thenReturn(new ArrayList<>());

        // Ejecución
        ModelAndView mav = controladorColeccion.verColeccion(request);

        // Verificación
        assertEquals("coleccion", mav.getViewName());
        assertEquals(true, mav.getModel().containsKey("todasLasCartas"));
    }
}