package com.tallerwebi.dominio;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ServicioCartaTest {

    private ServicioCarta servicioCarta;
    private RepositorioCarta repositorioCarta;

    @Before
    public void init() {
        repositorioCarta = mock(RepositorioCarta.class);
        servicioCarta = new ServicioCartaImpl(repositorioCarta);
    }

    @Test
    public void queAlPedirTodasLasCartasSeLlameAlRepositorio() {
        // Preparación
        List<Carta> cartasSimuladas = new ArrayList<>();
        cartasSimuladas.add(new Carta());
        when(repositorioCarta.listarTodas()).thenReturn(cartasSimuladas);

        // Ejecución
        List<Carta> resultado = servicioCarta.obtenerTodas();

        // Verificación
        assertEquals(1, resultado.size());
        verify(repositorioCarta, times(1)).listarTodas();
    }
}