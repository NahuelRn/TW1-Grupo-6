package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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

  // CAMINO EFECTIVO 1: Mazo completo y Enemigo existente (Camino feliz)
  @Test
  public void alIniciarPartidaConMazoCompletoYEnemigoValidoDebeEstructurarLaManoYElHpCorrectamente() {
    String zona = "Bosque";
    Enemigo enemigoSimulado = new Enemigo();
    enemigoSimulado.setHpBase(150);

    when(repoEnemigoMock.obtenerEnemigoAleatorioPorZona(zona)).thenReturn(enemigoSimulado);

    // Creamos un mazo con 6 cartas (mayor al mínimo de 5)
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
    assertThat(partida.getEnemigo(), is(enemigoSimulado));
    assertThat(partida.getHpEnemigo(), is(150));
    assertThat(partida.getHpJugador(), is(100));
    assertThat(partida.getManoJugador(), hasSize(5)); // Validamos que cortó exactamente en 5 cartas

    // NUEVO: el servicio también debe devolver el mazo restante (1 carta de 6)
    assertThat(partida.getMazoRestante(), hasSize(1));

    // NUEVO: mano + mazo restante deben sumar el total del mazo, sin duplicados
    List<Long> idsMano = partida
      .getManoJugador()
      .stream()
      .map(Carta::getId)
      .collect(Collectors.toList());
    List<Long> idsMazoRestante = partida
      .getMazoRestante()
      .stream()
      .map(Carta::getId)
      .collect(Collectors.toList());

    assertThat(idsMano.size() + idsMazoRestante.size(), is(6));
    assertThat(java.util.Collections.disjoint(idsMano, idsMazoRestante), is(true));
  }

  // COBERTURA DE RAMA 2: Cuando no se encuentra un enemigo en la zona (Validación del operador ternario)
  @Test
  public void alIniciarPartidaSiNoHayEnemigoDisponibleEnLaZonaDebeAsignarHpPorDefecto() {
    String zona = "ZonaVacia";
    when(repoEnemigoMock.obtenerEnemigoAleatorioPorZona(zona)).thenReturn(null);

    mazoActivo.setCartas(List.of()); // Mazo vacío

    Partida partida = servicioPartida.iniciarPartida(usuario, zona);

    assertThat(partida.getEnemigo(), nullValue());
    assertThat(partida.getHpEnemigo(), is(100)); // El 'else' del operador ternario
    assertThat(partida.getManoJugador(), is(empty()));
    assertThat(partida.getMazoRestante(), is(empty())); // NUEVO
  }

  // COBERTURA DE RAMA 3: Mazo incompleto (Prueba el bloque 'else' de la cantidad de cartas)
  @Test
  public void alIniciarPartidaSiElMazoTieneMenosDeCincoCartasDebeAsignarTodasLasDisponiblesALaMano() {
    String zona = "Pantano";
    Enemigo enemigoSimulado = new Enemigo();
    enemigoSimulado.setHpBase(120);
    when(repoEnemigoMock.obtenerEnemigoAleatorioPorZona(zona)).thenReturn(enemigoSimulado);

    Usuario usuarioReal = new Usuario();
    Mazo mazoReal = new Mazo();
    List<Carta> cartasPocas = new ArrayList<>();
    cartasPocas.add(new Carta());
    cartasPocas.add(new Carta());

    mazoReal.setCartas(cartasPocas);
    usuarioReal.setMazoActivo(mazoReal);

    Partida partida = servicioPartida.iniciarPartida(usuarioReal, zona);

    assertThat(partida.getManoJugador(), hasSize(2));
    assertThat(partida.getMazoRestante(), is(empty())); // NUEVO: no sobró nada para robar
  }

  // COBERTURA DE RAMA 4: Mazo o Lista de Cartas Nulas
  @Test
  public void alIniciarPartidaSiElUsuarioNoTieneMazoActivoOResultaNuloDebeIniciarConManoVaciaSinRomper() {
    usuario.setMazoActivo(null); // Caso límite: sin mazo configurado

    Partida partida = servicioPartida.iniciarPartida(usuario, "Bosque");

    assertThat(partida.getManoJugador(), is(empty()));
    assertThat(partida.getMazoRestante(), is(empty())); // NUEVO
  }
}
