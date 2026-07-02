package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class ServicioPartidaTest {

  private RepositorioEnemigo repoEnemigoMock;
  private RepositorioPartida repoPartidaMock;
  private ServicioPartida servicioPartida;
  private Usuario usuario;
  private Mazo mazoActivo;

  @BeforeEach
  public void setUp() {
    repoEnemigoMock = mock(RepositorioEnemigo.class);
    repoPartidaMock = mock(RepositorioPartida.class);

    servicioPartida = new ServicioPartidaImpl(repoEnemigoMock, repoPartidaMock);

    ReflectionTestUtils.setField(servicioPartida, "cartasIniciales", 5);

    usuario = new Usuario();
    mazoActivo = new Mazo();
    usuario.setMazoActivo(mazoActivo);
  }

  @Test
  public void alIniciarPartidaConMazoCompletoYEnemigoValidoDebeEstructurarLaManoYElHpCorrectamente() {
    String zona = "Bosque";
    Enemigo enemigoSimulado = new Enemigo();
    enemigoSimulado.setHpBase(150);
    enemigoSimulado.setProbabilidad(10);

    // Mockeamos la lista para que no entre en cortocircuito en determinarEnemigoParaZona
    List<Enemigo> listaEnemigos = List.of(enemigoSimulado);
    when(repoEnemigoMock.buscarPorZona(zona)).thenReturn(listaEnemigos);
    when(repoEnemigoMock.obtenerEnemigoAleatorioPorZona(zona)).thenReturn(enemigoSimulado);

    List<Carta> cartas = new ArrayList<>();
    for (long i = 1; i <= 6; i++) {
      Carta c = new Carta();
      c.setId(i);
      cartas.add(c);
    }
    mazoActivo.setCartas(cartas);

    Partida partida = servicioPartida.iniciarPartida(usuario, zona);

    assertThat(partida, notNullValue());
    assertThat(partida.getUsuario(), is(usuario));
    assertThat(partida.getHpEnemigo(), is(150));
    assertThat(partida.getHpJugador(), is(100));
    assertThat(partida.getManoJugador(), hasSize(5));
  }

  @Test
  public void alIniciarPartidaSiNoHayEnemigoDisponibleEnLaZonaDebeAsignarHpPorDefecto() {
    String zona = "ZonaVacia";
    when(repoEnemigoMock.buscarPorZona(zona)).thenReturn(null);
    when(repoEnemigoMock.obtenerEnemigoAleatorioPorZona(zona)).thenReturn(null);

    mazoActivo.setCartas(List.of());

    Partida partida = servicioPartida.iniciarPartida(usuario, zona);

    assertThat(partida.getEnemigo(), nullValue());
    assertThat(partida.getHpEnemigo(), is(100));
  }

  @Test
  public void alIniciarPartidaSiElMazoTieneMenosDeCincoCartasDebeAsignarTodasLasDisponiblesALaMano() {
    String zona = "Pantano";
    Enemigo enemigoSimulado = new Enemigo();
    enemigoSimulado.setHpBase(120);
    when(repoEnemigoMock.obtenerEnemigoAleatorioPorZona(zona)).thenReturn(enemigoSimulado);

    // 1. Instanciá objetos REALES
    Usuario usuarioReal = new Usuario();
    Mazo mazoReal = new Mazo();
    List<Carta> cartasPocas = new ArrayList<>();
    cartasPocas.add(new Carta());
    cartasPocas.add(new Carta());

    // 2. Cargá los datos como lo harías en la vida real
    mazoReal.setCartas(cartasPocas);
    usuarioReal.setMazoActivo(mazoReal);

    // 3. Ejecutá con el usuario real
    Partida partida = servicioPartida.iniciarPartida(usuarioReal, zona);

    // 4. Assert
    assertThat(partida.getManoJugador(), hasSize(2));
  }

  @Test
  public void alIniciarPartidaSiElUsuarioNoTieneMazoActivoOResultaNuloDebeIniciarConManoVaciaSinRomper() {
    usuario.setMazoActivo(null);

    Partida partida = servicioPartida.iniciarPartida(usuario, "Bosque");

    assertThat(partida.getManoJugador(), is(empty()));
  }
}
