package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ServicioMazoTest {

  @Test
  public void siElMazoNoTiene15CartasDebeLanzarExcepcion() {
    // Given
    RepositorioMazo repoMock = mock(RepositorioMazo.class);
    ServicioMazo servicio = new ServicioMazoImpl(repoMock);
    Mazo mazo = new Mazo(); // Tiene 0 cartas

    // When / Then
    try {
      servicio.validarYGuardarMazo(mazo);
    } catch (Exception e) {
      assertThat(e.getMessage(), containsString("exactamente 15 cartas"));
    }
  }

  @Test
  public void siElMazoTieneCartasRepetidasDebeLanzarExcepcion() {
    RepositorioMazo repoM = mock(RepositorioMazo.class);
    ServicioMazo servicio = new ServicioMazoImpl(repoM);
    Mazo mazo = new Mazo();

    List<Carta> cartas = new ArrayList<>();

    // Creamos 14 cartas únicas
    for (long i = 1; i <= 14; i++) {
      Carta carta = new Carta();
      carta.setId(i);
      carta.setNombre("Carta " + i);
      cartas.add(carta);
    }

    // Agregamos una repetida
    Carta repetida = new Carta();
    repetida.setId(1L);
    repetida.setNombre("Carta Repetida");

    cartas.add(repetida);

    mazo.setCartas(cartas);

    // When / Then
    try {
      servicio.validarYGuardarMazo(mazo);
    } catch (Exception e) {
      assertThat(e.getMessage(), containsString("repetidas"));
    }
  }

  @Test
  public void siElMazoEsValidoDebeGuardarseCorrectamente() throws Exception {
    RepositorioMazo repoM1 = mock(RepositorioMazo.class);
    ServicioMazo servicio = new ServicioMazoImpl(repoM1);

    Mazo mazo = new Mazo();
    List<Carta> cartas = new ArrayList<>();

    // Creamos 15 cartas únicas
    for (long i = 1; i <= 15; i++) {
      Carta carta = new Carta();
      carta.setId(i);
      carta.setNombre("Carta " + i);
      cartas.add(carta);
    }

    mazo.setCartas(cartas);

    // When
    servicio.validarYGuardarMazo(mazo);

    // Then
    verify(repoM1, times(1)).guardar(mazo);
  }
}
