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
    // 1. Creamos los mocks para TODAS las dependencias que pide el nuevo constructor
    repoMazoMock = mock(RepositorioMazo.class);
    repoCartaMock = mock(RepositorioCarta.class);
    repoInventarioMock = mock(RepositorioInventario.class);

    // 2. Se los pasamos ordenadamente al servicio
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

    // Cargamos 14 cartas normales usando la relación intermedia MazoCarta
    for (long i = 1; i <= 14; i++) {
      mazo.getMazoCartas().add(crearNexo(mazo, i, "Carta " + i));
    }

    // 3. Añadimos intencionalmente una carta repetida (Mismo ID 1L que la primera)
    mazo.getMazoCartas().add(crearNexo(mazo, 1L, "Carta Repetida"));

    Exception ex = assertThrows(Exception.class, () -> servicio.validarYGuardarMazo(mazo));
    assertThat(ex.getMessage(), containsString("repetidas"));
  }

  @Test
  public void siElMazoEsValidoDebeGuardarseCorrectamente() throws Exception {
    Mazo mazo = new Mazo();

    // 4. Cargamos exactamente 15 cartas diferentes para que el mazo sea completamente válido
    for (long i = 1; i <= 15; i++) {
      mazo.getMazoCartas().add(crearNexo(mazo, i, "Carta " + i));
    }

    servicio.validarYGuardarMazo(mazo);

    // Verificamos que se haya llamado al repositorio de mazos para persistir
    verify(repoMazoMock, times(1)).guardar(mazo);
  }

  /**
   * Método helper auxiliar para construir la entidad intermedia MazoCarta
   * emulando el comportamiento real del flujo de la aplicación.
   */
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
