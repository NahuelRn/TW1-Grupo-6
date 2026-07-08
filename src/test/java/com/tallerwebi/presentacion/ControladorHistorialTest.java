package com.tallerwebi.presentacion;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ControladorHistorialTest {

    private RepositorioHistorialPartida repositorioHistorialPartida = mock(RepositorioHistorialPartida.class);
    private ServicioHistorial servicioHistorial = new ServicioHistorialImpl(repositorioHistorialPartida);

    @Test
    public void deberiaMostrarVistaHistorial() {
        ControladorHistorialPartida controladorHistorialPartida = new ControladorHistorialPartida(this.servicioHistorial);

        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        HttpSession sessionMock = mock(HttpSession.class);

        when(requestMock.getSession()).thenReturn(sessionMock);
        when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(1L);

        List<HistorialPartida> lista = new ArrayList<>();
        when(this.servicioHistorial.listarHistorialPorUsuario(1L)).thenReturn(lista);

        ModelAndView modelAndView = controladorHistorialPartida.mostrarHistorialPartida(requestMock);

        assertEquals("historial", modelAndView.getViewName());
    }
}