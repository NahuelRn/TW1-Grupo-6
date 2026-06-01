package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioMazoTest {

  private RepositorioMazo repoMock;
  private ServicioMazo servicio;

  @BeforeEach
  public void setUp() {
    repoMock = mock(RepositorioMazo.class);
    servicio = new ServicioMazoImpl(repoMock);
  }

  @Test
  public void siElMazoNoTiene15CartasDebeLanzarExcepcion() {
    Mazo mazo = new Mazo(); // 0 cartas

    Exception ex = assertThrows(Exception.class, () -> servicio.validarYGuardarMazo(mazo));
    assertThat(ex.getMessage(), containsString("exactamente 15 cartas"));
  }

  @Test
  public void siElMazoTieneCartasRepetidasDebeLanzarExcepcion() {
    Mazo mazo = new Mazo();
    List<Carta> cartas = new ArrayList<>();

    for (long i = 1; i <= 14; i++) {
      Carta carta = new Carta();
      carta.setId(i);
      carta.setNombre("Carta " + i);
      cartas.add(carta);
    }

    // Carta repetida (mismo id que la primera)
    Carta repetida = new Carta();
    repetida.setId(1L);
    repetida.setNombre("Carta Repetida");
    cartas.add(repetida);

    mazo.setCartas(cartas);

    Exception ex = assertThrows(Exception.class, () -> servicio.validarYGuardarMazo(mazo));
    assertThat(ex.getMessage(), containsString("repetidas"));
  }

  @Test
  public void siElMazoEsValidoDebeGuardarseCorrectamente() throws Exception {
    Mazo mazo = new Mazo();
    List<Carta> cartas = new ArrayList<>();

    for (long i = 1; i <= 15; i++) {
      Carta carta = new Carta();
      carta.setId(i);
      carta.setNombre("Carta " + i);
      cartas.add(carta);
    }

    mazo.setCartas(cartas);

    servicio.validarYGuardarMazo(mazo);

    verify(repoMock, times(1)).guardar(mazo);
  }
}
