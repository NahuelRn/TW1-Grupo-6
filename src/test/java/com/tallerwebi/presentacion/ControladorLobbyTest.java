package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ModelMap;

public class ControladorLobbyTest {

  private ControladorLobby controladorLobby;

  @BeforeEach
  public void init() {
    controladorLobby = new ControladorLobby();
  }

  @Test
  public void alEntrarAlLobbySeMuestraLaVistaLobby() {
    ModelMap modelo = new ModelMap();

    String vista = controladorLobby.irAlLobby(modelo);

    assertThat(vista, equalTo("lobby"));
  }

  @Test
  public void alEntrarAlLobbyElModeloEstaDisponible() {
    ModelMap modelo = new ModelMap();
    modelo.put("dato", "valor");

    String vista = controladorLobby.irAlLobby(modelo);

    assertThat(vista, equalTo("lobby"));
    assertThat(modelo.containsKey("dato"), is(true));
  }
}
