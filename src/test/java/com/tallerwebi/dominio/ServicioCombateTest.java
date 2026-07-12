package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioCombateTest {

  private RepositorioPartida repositorioPartidaMock;
  private RepositorioCarta repositorioCartaMock;
  private RepositorioHistorialPartida repositorioHistorialPartidaMock;
  private ServicioUsuario servicioUsuarioMock;
  private ServicioCalculoRecompensa servicioCalculoRecompensaMock;
  private ServicioCombateImpl servicioCombate;

  @BeforeEach
  public void init() {
    repositorioPartidaMock = mock(RepositorioPartida.class);
    repositorioCartaMock = mock(RepositorioCarta.class);
    repositorioHistorialPartidaMock = mock(RepositorioHistorialPartida.class);
    servicioUsuarioMock = mock(ServicioUsuario.class);
    servicioCalculoRecompensaMock = mock(ServicioCalculoRecompensa.class);

    RecompensaDTO recompensaFake = new RecompensaDTO();
    recompensaFake.setOro(10);
    recompensaFake.setExperiencia(20);
    when(servicioCalculoRecompensaMock.obtenerRecompensa(any())).thenReturn(recompensaFake);

    servicioCombate =
      new ServicioCombateImpl(
        repositorioPartidaMock,
        repositorioCartaMock,
        repositorioHistorialPartidaMock,
        servicioUsuarioMock,
        servicioCalculoRecompensaMock
      );
  }

  private Partida crearPartidaBase() {
    Partida partida = new Partida();
    partida.setHpJugador(100);
    partida.setHpEnemigo(100);
    Enemigo enemigo = new Enemigo();
    enemigo.setNombre("Enemigo de Prueba");
    enemigo.setDanoMin(0);
    enemigo.setDanoMax(0);
    partida.setEnemigo(enemigo);
    return partida;
  }

  private Carta crearCartaHechizo(
    String efecto,
    Integer danoMin,
    Integer danoMax,
    Integer defMin,
    Integer defMax
  ) {
    Carta carta = new Carta();
    carta.setNombre("Carta de prueba");
    carta.setTipo("HECHIZO");
    carta.setEfecto(efecto);
    carta.setDanoMin(danoMin);
    carta.setDanoMax(danoMax);
    carta.setDefensaMin(defMin);
    carta.setDefensaMax(defMax);
    return carta;
  }

  // -------------------- ATAQUE / DEFENSA --------------------

  @Test
  public void alJugarUnaCartaDeAtaqueElEnemigoPierdeVidaYElJugadorRecibeContraataque() {
    Partida partida = crearPartidaBase();
    partida.getEnemigo().setDanoMin(5);
    partida.getEnemigo().setDanoMax(5);
    Carta carta = new Carta();
    carta.setNombre("Golpe Básico");
    carta.setTipo("ATAQUE");
    carta.setDanoMin(15);
    carta.setDanoMax(15);

    when(repositorioPartidaMock.buscarPartidaPorIdentificador(1L)).thenReturn(partida);
    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(carta);

    servicioCombate.jugarTurno(1L, 1L);

    assertThat(partida.getHpEnemigo(), is(85));
    assertThat(partida.getHpJugador(), is(95));
  }

  @Test
  public void alJugarUnaCartaDeDefensaSeReduceElDanioRecibido() {
    Partida partida = crearPartidaBase();
    partida.getEnemigo().setDanoMin(10);
    partida.getEnemigo().setDanoMax(10);
    Carta carta = new Carta();
    carta.setNombre("Escudo");
    carta.setTipo("DEFENSA");
    carta.setDefensaMin(10);
    carta.setDefensaMax(10);

    when(repositorioPartidaMock.buscarPartidaPorIdentificador(1L)).thenReturn(partida);
    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(carta);

    servicioCombate.jugarTurno(1L, 1L);

    assertThat(partida.getHpJugador(), is(100));
  }

  @Test
  public void alMatarAlEnemigoSeRegistraLaVictoriaYSeGuardaElHistorial() {
    Partida partida = crearPartidaBase();
    partida.setHpEnemigo(10);
    Usuario usuario = new Usuario();
    partida.setUsuario(usuario);
    Carta carta = new Carta();
    carta.setNombre("Golpe Letal");
    carta.setTipo("ATAQUE");
    carta.setDanoMin(15);
    carta.setDanoMax(15);

    when(repositorioPartidaMock.buscarPartidaPorIdentificador(1L)).thenReturn(partida);
    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(carta);

    String resultado = servicioCombate.jugarTurno(1L, 1L);

    assertThat(partida.getEnumEstadoPartida(), is(EnumEstadoPartida.GANADOR_JUGADOR));
    assertTrue(resultado.contains("DERROTADO"));
    verify(servicioUsuarioMock, times(1)).aplicarRecompensa(usuario, partida);
    verify(repositorioHistorialPartidaMock, times(1))
      .guardarHistorialPartidaRepositorio(org.mockito.ArgumentMatchers.any(HistorialPartida.class));
  }

  @Test
  public void siElJugadorMuereSeRegistraLaDerrota() {
    Partida partida = crearPartidaBase();
    partida.setHpJugador(5);
    partida.getEnemigo().setDanoMin(50);
    partida.getEnemigo().setDanoMax(50);
    partida.setUsuario(new Usuario());
    Carta carta = new Carta();
    carta.setNombre("Golpe Debil");
    carta.setTipo("ATAQUE");
    carta.setDanoMin(1);
    carta.setDanoMax(1);

    when(repositorioPartidaMock.buscarPartidaPorIdentificador(1L)).thenReturn(partida);
    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(carta);

    String resultado = servicioCombate.jugarTurno(1L, 1L);

    assertThat(partida.getEnumEstadoPartida(), is(EnumEstadoPartida.GANADOR_ENEMIGO));
    assertTrue(resultado.contains("FIN DE LA PARTIDA"));
  }

  // -------------------- Cartas de utilidad --------------------

  @Test
  public void alUsarUnaCartaConEfectoCuraElJugadorRecuperaVida() {
    Partida partida = crearPartidaBase();
    partida.setHpJugador(50);
    Carta carta = crearCartaHechizo("CURA", 0, 0, 15, 15);

    when(repositorioPartidaMock.buscarPartidaPorIdentificador(1L)).thenReturn(partida);
    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(carta);

    servicioCombate.jugarTurno(1L, 1L);

    assertThat(partida.getHpJugador(), is(65));
  }

  @Test
  public void alUsarUnaCartaConEfectoDanioDirectoElEnemigoPierdeVida() {
    Partida partida = crearPartidaBase();
    Carta carta = crearCartaHechizo("DANIO_DIRECTO", 15, 15, 0, 0);

    when(repositorioPartidaMock.buscarPartidaPorIdentificador(1L)).thenReturn(partida);
    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(carta);

    servicioCombate.jugarTurno(1L, 1L);

    assertThat(partida.getHpEnemigo(), is(85));
  }

  @Test
  public void alUsarUnaCartaDeBuffDeDanioElProximoAtaqueHaceMasDanio() {
    Partida partida = crearPartidaBase();
    Carta buffCarta = crearCartaHechizo("BUFF_DANIO", 10, 10, 0, 0);

    Carta ataqueCarta = new Carta();
    ataqueCarta.setNombre("Golpe");
    ataqueCarta.setTipo("ATAQUE");
    ataqueCarta.setDanoMin(10);
    ataqueCarta.setDanoMax(10);

    when(repositorioPartidaMock.buscarPartidaPorIdentificador(1L)).thenReturn(partida);
    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(buffCarta);
    when(repositorioCartaMock.buscarPorId(2L)).thenReturn(ataqueCarta);

    servicioCombate.jugarTurno(1L, 1L);
    assertThat(partida.getBuffDanioProximoAtaque(), is(10));

    servicioCombate.jugarTurno(1L, 2L);

    assertThat(partida.getHpEnemigo(), is(80)); // 10 base + 10 buff
    assertThat(partida.getBuffDanioProximoAtaque(), is(0));
  }

  @Test
  public void alUsarUnaCartaMultiplicadoraElProximoAtaqueHaceElDobleDeDanio() {
    Partida partida = crearPartidaBase();
    Carta multiplicadorCarta = crearCartaHechizo("MULTIPLICADOR_DANIO", 2, 2, 0, 0);
    Carta ataqueCarta = new Carta();
    ataqueCarta.setNombre("Golpe");
    ataqueCarta.setTipo("ATAQUE");
    ataqueCarta.setDanoMin(10);
    ataqueCarta.setDanoMax(10);

    when(repositorioPartidaMock.buscarPartidaPorIdentificador(1L)).thenReturn(partida);
    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(multiplicadorCarta);
    when(repositorioCartaMock.buscarPorId(2L)).thenReturn(ataqueCarta);

    servicioCombate.jugarTurno(1L, 1L);
    servicioCombate.jugarTurno(1L, 2L);

    assertThat(partida.getHpEnemigo(), is(80)); // 10 * 2
    assertThat(partida.getMultiplicadorDanioProximoAtaque(), is(1));
  }

  @Test
  public void alUsarUnaCartaDeEvasionElEnemigoNoHaceDanioEseTurno() {
    Partida partida = crearPartidaBase();
    partida.getEnemigo().setDanoMin(50);
    partida.getEnemigo().setDanoMax(50);
    Carta carta = crearCartaHechizo("EVASION", 0, 0, 0, 0);

    when(repositorioPartidaMock.buscarPartidaPorIdentificador(1L)).thenReturn(partida);
    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(carta);

    servicioCombate.jugarTurno(1L, 1L);

    assertThat(partida.getHpJugador(), is(100));
  }

  @Test
  public void alUsarTrampaParaOsosElEnemigoPierdeVidaYNoAtaca() {
    Partida partida = crearPartidaBase();
    partida.getEnemigo().setDanoMin(50);
    partida.getEnemigo().setDanoMax(50);
    Carta carta = crearCartaHechizo("DANIO_Y_ATURDIR", 10, 10, 0, 0);

    when(repositorioPartidaMock.buscarPartidaPorIdentificador(1L)).thenReturn(partida);
    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(carta);

    servicioCombate.jugarTurno(1L, 1L);

    assertThat(partida.getHpEnemigo(), is(90));
    assertThat(partida.getHpJugador(), is(100));
  }

  @Test
  public void alUsarFrascoDeAcidoLaVidaDelEnemigoSeReduceALaMitad() {
    Partida partida = crearPartidaBase();
    partida.setHpEnemigo(90);
    Carta carta = crearCartaHechizo("MITAD_VIDA_ENEMIGO", 0, 0, 0, 0);

    when(repositorioPartidaMock.buscarPartidaPorIdentificador(1L)).thenReturn(partida);
    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(carta);

    servicioCombate.jugarTurno(1L, 1L);

    assertThat(partida.getHpEnemigo(), is(45));
  }

  @Test
  public void alUsarContratoDemoniacoElJugadorYElEnemigoPierdenVida() {
    Partida partida = crearPartidaBase();
    Carta carta = crearCartaHechizo("SACRIFICIO", 40, 40, 20, 20);

    when(repositorioPartidaMock.buscarPartidaPorIdentificador(1L)).thenReturn(partida);
    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(carta);

    servicioCombate.jugarTurno(1L, 1L);

    assertThat(partida.getHpEnemigo(), is(60));
    assertThat(partida.getHpJugador(), is(80));
  }

  @Test
  public void alUsarRitualOscuroElJugadorPierdeVidaYGanaUnBuff() {
    Partida partida = crearPartidaBase();
    Carta carta = crearCartaHechizo("SACRIFICIO_BUFF", 20, 20, 10, 10);

    when(repositorioPartidaMock.buscarPartidaPorIdentificador(1L)).thenReturn(partida);
    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(carta);

    servicioCombate.jugarTurno(1L, 1L);

    assertThat(partida.getHpJugador(), is(90));
    assertThat(partida.getBuffDanioProximoAtaque(), is(20));
  }

  @Test
  public void alUsarCatalizadorDeSangreElJugadorDrenaVidaDelEnemigo() {
    Partida partida = crearPartidaBase();
    partida.setHpJugador(50);
    Carta carta = crearCartaHechizo("VAMPIRISMO", 15, 15, 15, 15);

    when(repositorioPartidaMock.buscarPartidaPorIdentificador(1L)).thenReturn(partida);
    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(carta);

    servicioCombate.jugarTurno(1L, 1L);

    assertThat(partida.getHpEnemigo(), is(85));
    assertThat(partida.getHpJugador(), is(65));
  }

  @Test
  public void alUsarAdrenalinaElJugadorSeCuraYGanaUnBuff() {
    Partida partida = crearPartidaBase();
    partida.setHpJugador(50);
    Carta carta = crearCartaHechizo("HIBRIDO_CURA_BUFF", 10, 10, 10, 10);

    when(repositorioPartidaMock.buscarPartidaPorIdentificador(1L)).thenReturn(partida);
    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(carta);

    servicioCombate.jugarTurno(1L, 1L);

    assertThat(partida.getHpJugador(), is(60));
    assertThat(partida.getBuffDanioProximoAtaque(), is(10));
  }

  @Test
  public void alUsarPreparacionElJugadorGanaEscudoEsteTurnoYBuffParaElProximoAtaque() {
    Partida partida = crearPartidaBase();
    partida.getEnemigo().setDanoMin(15);
    partida.getEnemigo().setDanoMax(15);
    Carta carta = crearCartaHechizo("HIBRIDO_ESCUDO_BUFF", 5, 5, 10, 10);

    when(repositorioPartidaMock.buscarPartidaPorIdentificador(1L)).thenReturn(partida);
    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(carta);

    servicioCombate.jugarTurno(1L, 1L);

    assertThat(partida.getHpJugador(), is(95)); // 15 de ataque - 10 de escudo = 5
    assertThat(partida.getBuffDanioProximoAtaque(), is(5));
  }

  @Test
  public void alUsarConcentracionElDanioAlEnemigoEstaDentroDelRangoEsperado() {
    Partida partida = crearPartidaBase();
    Carta carta = crearCartaHechizo("DANIO_ALEATORIO", 1, 30, 0, 0);

    when(repositorioPartidaMock.buscarPartidaPorIdentificador(1L)).thenReturn(partida);
    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(carta);

    servicioCombate.jugarTurno(1L, 1L);

    int danioInfligido = 100 - partida.getHpEnemigo();
    assertTrue(danioInfligido >= 1 && danioInfligido <= 30);
  }

  @Test
  public void alUsarReciclarElJugadorSeCuraYElEnemigoRecuperaVida() {
    Partida partida = crearPartidaBase();
    partida.setHpJugador(50);
    partida.setHpEnemigo(50);
    Carta carta = crearCartaHechizo("RECICLAR", 5, 5, 15, 15);

    when(repositorioPartidaMock.buscarPartidaPorIdentificador(1L)).thenReturn(partida);
    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(carta);

    servicioCombate.jugarTurno(1L, 1L);

    assertThat(partida.getHpJugador(), is(65));
    assertThat(partida.getHpEnemigo(), is(55));
  }

  @Test
  public void alUsarPlanificacionElJugadorSeCuraYElEnemigoNoAtaca() {
    Partida partida = crearPartidaBase();
    partida.setHpJugador(50);
    partida.getEnemigo().setDanoMin(50);
    partida.getEnemigo().setDanoMax(50);
    Carta carta = crearCartaHechizo("ATURDIR_Y_CURA", 0, 0, 5, 5);

    when(repositorioPartidaMock.buscarPartidaPorIdentificador(1L)).thenReturn(partida);
    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(carta);

    servicioCombate.jugarTurno(1L, 1L);

    assertThat(partida.getHpJugador(), is(55));
  }

  @Test
  public void alUsarPielDeObsidianaElJugadorSobreviveConUnHpSiElGolpeEsMortal() {
    Partida partida = crearPartidaBase();
    partida.setHpJugador(5);
    partida.getEnemigo().setDanoMin(50);
    partida.getEnemigo().setDanoMax(50);
    Carta carta = crearCartaHechizo("INMORTALIDAD", 0, 0, 0, 0);

    when(repositorioPartidaMock.buscarPartidaPorIdentificador(1L)).thenReturn(partida);
    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(carta);

    servicioCombate.jugarTurno(1L, 1L);

    assertThat(partida.getHpJugador(), is(1));
    assertThat(partida.getEnumEstadoPartida(), is(not(EnumEstadoPartida.GANADOR_ENEMIGO)));
    verify(repositorioHistorialPartidaMock, never()).guardarHistorialPartidaRepositorio(any());
  }

  @Test
  public void siLaCartaHechizoNoTieneEfectoAsignadoSeAplicaCuraPorDefecto() {
    Partida partida = crearPartidaBase();
    partida.setHpJugador(50);
    Carta carta = crearCartaHechizo(null, 0, 0, 8, 8);

    when(repositorioPartidaMock.buscarPartidaPorIdentificador(1L)).thenReturn(partida);
    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(carta);

    servicioCombate.jugarTurno(1L, 1L);

    assertThat(partida.getHpJugador(), is(58));
  }

  @Test
  public void obtenerConfiguracionZonaDevuelveConfiguracionDeBosquePorDefecto() {
    java.util.Map<String, String> config = servicioCombate.obtenerConfiguracionZona(null);

    assertThat(config.get("nombreZona"), is("BOSQUE"));
    assertThat(config.get("archivoFondo"), is("Bosque.jpg"));
  }

  @Test
  public void obtenerPartidaPorIdentificadorDelegaEnElRepositorio() {
    Partida partida = new Partida();
    when(repositorioPartidaMock.buscarPartidaPorIdentificador(5L)).thenReturn(partida);

    Partida resultado = servicioCombate.obtenerPartidaPorIdentificador(5L);

    assertThat(resultado, is(partida));
  }
}
