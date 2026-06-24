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
    this.repositorioPartidaMock = mock(RepositorioPartida.class);
    this.repositorioCartaMock = mock(RepositorioCarta.class);
    this.servicioCombate = new ServicioCombateImpl(repositorioPartidaMock, repositorioCartaMock);
  }

  @Test
  public void queAlJugarTurnoAtaqueSeResteVidaAlEnemigoYElZombiContraataque() {
    Partida partida = new Partida(100, 50, 1);
    Carta carta = new Carta();
    carta.setNombre("Golpe Básico");
    carta.setDano(15);
    carta.setDefensa(0);

    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(carta);

    String resultado = servicioCombate.jugarTurno(partida, 1L);

    assertEquals(35, partida.getHpEnemigo()); // El Infectado tenía 50, pierde 15 -> queda en 35
    assertEquals(95, partida.getHpJugador()); // El Jugador tenía 100, el Infectado le saca 5 fijos -> queda 95
    assertTrue(resultado.contains("Hiciste 15 de Daño"));
  }

  @Test
  public void queAlMatarAlZombiElEstadoSeaGanadorJugador() {
    Partida partida = new Partida(100, 10, 1); // Infectado a punto de morir (10 HP)
    Carta carta = new Carta();
    carta.setNombre("Golpe Letal");
    carta.setDano(15);
    carta.setDefensa(0);

    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(carta);

    String resultado = servicioCombate.jugarTurno(partida, 1L);

    assertEquals(EnumEstadoPartida.GANADOR_JUGADOR, partida.getEnumEstadoPartida());
    assertTrue(resultado.contains("HA SIDO DESTRUIDO"));
  }

  @Test
  public void queAlMorirPorContraataqueElEstadoSeaGanadorEnemigo() {
    Partida partida = new Partida(4, 50, 1); // Jugador a un golpe de morir (el Infectado saca 5 fijos)
    Carta carta = new Carta();
    carta.setNombre("Golpe Flojo");
    carta.setDano(10); // Golpe que no llega a matar al Infectado
    carta.setDefensa(0);

    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(carta);

    String resultado = servicioCombate.jugarTurno(partida, 1L);

    assertEquals(EnumEstadoPartida.GANADOR_ENEMIGO, partida.getEnumEstadoPartida());
    assertTrue(resultado.contains("HAS MUERTO"));
  }

  @Test
  public void queAlJugarCartaDefensaNoRecibaDano() {
    Partida partida = new Partida(100, 50, 1);
    Carta carta = new Carta();
    carta.setNombre("Súper Escudo");
    carta.setDano(0);
    carta.setDefensa(10); // Defensa alta para bloquear los 5 del Infectado

    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(carta);

    servicioCombate.jugarTurno(partida, 1L);

    assertEquals(50, partida.getHpEnemigo()); // No le pegamos
    assertEquals(100, partida.getHpJugador()); // Bloqueamos el golpe y no nos bajó vida
  }
}
