package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioCalculoRecompensaTest {

  private RepositorioConfiguracionJuego repositorioConfiguracionJuegoMock;
  private ServicioCalculoRecompensa servicioCalculoRecompensa;

  private static final int ORO_BASE = 20;
  private static final int EXPERIENCIA_BASE = 50;

  @BeforeEach
  public void init() {
    repositorioConfiguracionJuegoMock = mock(RepositorioConfiguracionJuego.class);
    when(repositorioConfiguracionJuegoMock.obtenerValor(ConfiguracionJuego.ORO_BASE))
      .thenReturn(ORO_BASE);
    when(repositorioConfiguracionJuegoMock.obtenerValor(ConfiguracionJuego.EXPERIENCIA_BASE))
      .thenReturn(EXPERIENCIA_BASE);

    servicioCalculoRecompensa =
      new ServicioCalculoRecompensaImpl(repositorioConfiguracionJuegoMock);
  }

  @Test
  public void siElEnemigoTieneHpMayorACeroDebeDevolverRecompensaDeDerrota() {
    Partida partida = new Partida();
    partida.setHpEnemigo(30);
    partida.setHpJugador(90);

    RecompensaDTO recompensa = servicioCalculoRecompensa.obtenerRecompensa(partida);

    assertThat(recompensa.getOro(), is(0));
    assertThat(recompensa.getExperiencia(), is(EXPERIENCIA_BASE / 2));
  }

  @Test
  public void siElHpDelEnemigoEsNuloDebeAsumirCienYDevolverDerrota() {
    Partida partida = new Partida();
    partida.setHpEnemigo(null);

    RecompensaDTO recompensa = servicioCalculoRecompensa.obtenerRecompensa(partida);

    assertThat(recompensa.getOro(), is(0));
    assertThat(recompensa.getExperiencia(), is(EXPERIENCIA_BASE / 2));
  }

  @Test
  public void siElEnemigoMuereYElJugadorTieneHpAltoDebeDarBonusCompleto() {
    Partida partida = new Partida();
    partida.setHpEnemigo(0);
    partida.setHpJugador(90);

    RecompensaDTO recompensa = servicioCalculoRecompensa.obtenerRecompensa(partida);

    assertThat(recompensa.getOro(), is(ORO_BASE + 10));
    assertThat(recompensa.getExperiencia(), is(EXPERIENCIA_BASE + 5));
  }

  @Test
  public void siElEnemigoMuereYElJugadorTieneHpMedioDebeDarBonusDeOroSolamente() {
    Partida partida = new Partida();
    partida.setHpEnemigo(-5);
    partida.setHpJugador(60);

    RecompensaDTO recompensa = servicioCalculoRecompensa.obtenerRecompensa(partida);

    assertThat(recompensa.getOro(), is(ORO_BASE + 5));
    assertThat(recompensa.getExperiencia(), is(EXPERIENCIA_BASE));
  }

  @Test
  public void siElEnemigoMuereYElJugadorTieneHpBajoNoDebeDarBonus() {
    Partida partida = new Partida();
    partida.setHpEnemigo(0);
    partida.setHpJugador(40);

    RecompensaDTO recompensa = servicioCalculoRecompensa.obtenerRecompensa(partida);

    assertThat(recompensa.getOro(), is(ORO_BASE));
    assertThat(recompensa.getExperiencia(), is(EXPERIENCIA_BASE));
  }

  @Test
  public void siElHpDelJugadorEsNuloDebeTratarloComoCero() {
    Partida partida = new Partida();
    partida.setHpEnemigo(0);
    partida.setHpJugador(null);

    RecompensaDTO recompensa = servicioCalculoRecompensa.obtenerRecompensa(partida);

    assertThat(recompensa.getOro(), is(ORO_BASE));
    assertThat(recompensa.getExperiencia(), is(EXPERIENCIA_BASE));
  }
}
