package com.tallerwebi.presentacion;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ModelMap;
import static org.junit.Assert.assertEquals;

public class ControladorLobbyTest {

    private ControladorLobby controladorLobby;

    @Before
    public void init() {
        controladorLobby = new ControladorLobby();
    }

    @Test
    public void alEntrarAlLobbySeMuestraLaVistaLobby() {
        // 1. Preparamos un ModelMap vacío
        ModelMap modelo = new ModelMap();

        // 2. Ejecución
        String vista = controladorLobby.irAlLobby(modelo);

        // 3. Verificación
        assertEquals("lobby", vista);
    }
}