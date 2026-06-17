package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioMazoTest {

  private RepositorioMazo repoMazoMock;
  private RepositorioCarta repoCartaMock;
  private RepositorioInventario repoInventarioMock;
  private ServicioMazo servicio;

  @BeforeEach
  public void setUp() {
    repoMazoMock = mock(RepositorioMazo.class);
    repoCartaMock = mock(RepositorioCarta.class);
    repoInventarioMock = mock(RepositorioInventario.class);

    servicio = new ServicioMazoImpl(repoMazoMock, repoCartaMock, repoInventarioMock);
  }

  @Test
  public void siElMazoNoTiene15CartasDebeLanzarExcepcion() {
    Mazo mazo = new Mazo(); // Nace con 0 cartas en su lista de MazoCarta

    Exception ex = assertThrows(Exception.class, () -> servicio.validarYGuardarMazo(mazo));
    assertThat(ex.getMessage(), containsString("exactamente 15 cartas"));
  }

  @Test
  public void siElMazoTieneCartasRepetidasDebeLanzarExcepcion() {
    Mazo mazo = new Mazo();

    for (long i = 1; i <= 14; i++) {
      mazo.getMazoCartas().add(crearNexo(mazo, i, "Carta " + i));
    }
    mazo.getMazoCartas().add(crearNexo(mazo, 1L, "Carta Repetida"));

    Exception ex = assertThrows(Exception.class, () -> servicio.validarYGuardarMazo(mazo));
    assertThat(ex.getMessage(), containsString("repetidas"));
  }

  @Test
  public void siElMazoEsValidoDebeGuardarseCorrectamente() throws Exception {
    Mazo mazo = new Mazo();
    for (long i = 1; i <= 15; i++) {
      mazo.getMazoCartas().add(crearNexo(mazo, i, "Carta " + i));
    }

    servicio.validarYGuardarMazo(mazo);
    verify(repoMazoMock, times(1)).guardar(mazo);
  }

  private MazoCarta crearNexo(Mazo mazo, Long idCarta, String nombreCarta) {
    Carta carta = new Carta();
    carta.setId(idCarta);
    carta.setNombre(nombreCarta);

    MazoCarta nexo = new MazoCarta();
    nexo.setMazo(mazo);
    nexo.setCarta(carta);

    return nexo;
  }
}
