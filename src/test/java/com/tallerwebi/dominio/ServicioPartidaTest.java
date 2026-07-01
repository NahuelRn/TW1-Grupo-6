package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioPartidaTest {

  // Declarás el mock original y el nuevo
  private RepositorioEnemigo repoEnemigoMock;
  private RepositorioPartida repoPartidaMock; // <-- AGREGAR
  private ServicioPartida servicioPartida;
  private Usuario usuario;
  private Mazo mazoActivo;

  @BeforeEach
  public void setUp() {
    repoEnemigoMock = mock(RepositorioEnemigo.class);
    repoPartidaMock = mock(RepositorioPartida.class); // <-- INICIALIZAR

    // 2. Pasar los dos parámetros al constructor
    servicioPartida = new ServicioPartidaImpl(repoEnemigoMock, repoPartidaMock);

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
  }

  // COBERTURA DE RAMA 2: Cuando no se encuentra un enemigo en la zona (Validación del operador ternario)
  @Test
  public void alIniciarPartidaSiNoHayEnemigoDisponibleEnLaZonaDebeAsignarHpPorDefecto() {
    String zona = "ZonaVacia";
    // Forzamos al repositorio a devolver null
    when(repoEnemigoMock.obtenerEnemigoAleatorioPorZona(zona)).thenReturn(null);

    mazoActivo.setCartas(List.of()); // Mazo vacío

    Partida partida = servicioPartida.iniciarPartida(usuario, zona);

    assertThat(partida.getEnemigo(), nullValue());
    assertThat(partida.getHpEnemigo(), is(100)); // El 'else' del operador ternario
  }

  // COBERTURA DE RAMA 3: Mazo incompleto (Prueba el bloque 'else' de la cantidad de cartas)
  @Test
  public void alIniciarPartidaSiElMazoTieneMenosDeCincoCartasDebeAsignarTodasLasDisponiblesALaMano() {
    String zona = "Pantano";
    Enemigo enemigoSimulado = new Enemigo();
    enemigoSimulado.setHpBase(120); // Inicializado para evitar el NullPointerException en el unboxing

    when(repoEnemigoMock.obtenerEnemigoAleatorioPorZona(zona)).thenReturn(enemigoSimulado);

    // Usamos una ArrayList tradicional para asegurar mutabilidad limpia
    List<Carta> cartasPocas = new ArrayList<>();
    cartasPocas.add(new Carta());
    cartasPocas.add(new Carta());
    mazoActivo.setCartas(cartasPocas);

    Partida partida = servicioPartida.iniciarPartida(usuario, zona);

    // Verifica que ejecutó el bloque 'else' asignando las cartas directamente
    assertThat(partida.getManoJugador(), hasSize(2));
  }

  // COBERTURA DE RAMA 4: Mazo o Lista de Cartas Nulas
  @Test
  public void alIniciarPartidaSiElUsuarioNoTieneMazoActivoOResultaNuloDebeIniciarConManoVaciaSinRomper() {
    usuario.setMazoActivo(null); // Caso límite: sin mazo configurado

    Partida partida = servicioPartida.iniciarPartida(usuario, "Bosque");

    assertThat(partida.getManoJugador(), is(empty()));
  }
}
