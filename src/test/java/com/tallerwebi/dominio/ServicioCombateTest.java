package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioCombateTest {

  private RepositorioPartida repositorioPartidaMock;
  private RepositorioCarta repositorioCartaMock;
  private ServicioCombateImpl servicioCombate;

  @BeforeEach
  public void init() {
    // 1. Mockeamos los DOS repositorios que usa el nuevo MVP
    this.repositorioPartidaMock = mock(RepositorioPartida.class);
    this.repositorioCartaMock = mock(RepositorioCarta.class);

    // 2. Instanciamos el servicio pasándole los mocks
    this.servicioCombate = new ServicioCombateImpl(repositorioPartidaMock, repositorioCartaMock);
  }

  @Test
  public void queAlJugarCartaSeResteVidaAlEnemigoYElZombiContraataque() {
    // Preparación
    Partida partida = new Partida(100, 50, 1);
    Carta carta = new Carta();
    carta.setDano(15);

    when(repositorioPartidaMock.buscarPartidaPorIdentificador(1L)).thenReturn(partida);
    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(carta);

    // Ejecución
    Integer danioRealizado = servicioCombate.jugarCarta(1L, 1L);

    // Validación
    assertEquals(15, danioRealizado); // El daño devuelto debe ser el de la carta
    assertEquals(35, partida.getHpEnemigo()); // El Zombi tenía 50, pierde 15 -> queda en 35
    assertEquals(95, partida.getHpJugador()); // El Jugador tenía 100, el Zombi le saca 5 fijos -> queda 95
    verify(repositorioPartidaMock, times(1)); // Verifica que se haya guardado
  }

  @Test
  public void queAlMatarAlZombiElEstadoSeaGanadorJugador() {
    // Preparación
    Partida partida = new Partida(100, 10, 1); // Zombi a punto de morir (10 HP)
    Carta carta = new Carta();
    carta.setDano(15); // Golpe letal

    when(repositorioPartidaMock.buscarPartidaPorIdentificador(1L)).thenReturn(partida);
    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(carta);

    // Ejecución
    servicioCombate.jugarCarta(1L, 1L);

    // Validación
    assertEquals(EnumEstadoPartida.GANADOR_JUGADOR, partida.getEnumEstadoPartida());
  }

  @Test
  public void queAlMorirPorContraataqueElEstadoSeaGanadorEnemigo() {
    // Preparación
    Partida partida = new Partida(4, 50, 1); // Jugador a un golpe de morir (el Zombi saca 5 fijos)
    Carta carta = new Carta();
    carta.setDano(10); // Golpe que no llega a matar al Zombi

    when(repositorioPartidaMock.buscarPartidaPorIdentificador(1L)).thenReturn(partida);
    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(carta);

    // Ejecución
    servicioCombate.jugarCarta(1L, 1L);

    // Validación
    assertEquals(EnumEstadoPartida.GANADOR_ENEMIGO, partida.getEnumEstadoPartida());
  }

  @Test
  public void deberiaPoderObtenerPartidaPorSuIdentificador() {
    Partida partida = new Partida(100, 100, 1);
    when(repositorioPartidaMock.buscarPartidaPorIdentificador(1L)).thenReturn(partida);

    Partida resultado = servicioCombate.obtenerPartidaPorIdentificador(1L);
    assertEquals(partida, resultado);
  }
}
