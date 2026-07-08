//<<<<<<< HEAD
//package com.tallerwebi.dominio;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//public class ServicioCombateTest {
//
//  private RepositorioPartida repositorioPartidaMock;
//  private RepositorioCarta repositorioCartaMock;
//  private ServicioCombate servicioCombate;
//
//  @BeforeEach
//  public void init() {
//    repositorioPartidaMock = mock(RepositorioPartida.class);
//    repositorioCartaMock = mock(RepositorioCarta.class);
//    servicioCombate = new ServicioCombateImpl(repositorioPartidaMock, repositorioCartaMock);
//  }
//
//  @Test
//  public void queAlJugarTurnoAtaqueSeResteVidaAlEnemigoYElEnemigoContraataque() {
//    Partida partida = new Partida();
//    partida.setHpJugador(100);
//    partida.setHpEnemigo(50);
//
//    Enemigo enemigo = new Enemigo();
//    enemigo.setNombre("Infectado");
//    // Forzamos el daño del enemigo para saber exactamente que va a pegar
//    enemigo.setDanoMin(5);
//    enemigo.setDanoMax(10);
//    partida.setEnemigo(enemigo);
//
//    Carta carta = new Carta();
//    carta.setNombre("Golpe Básico");
//    carta.setDanoMin(15);
//    carta.setDanoMax(15);
//    carta.setTipo("ATAQUE");
//
//    when(repositorioPartidaMock.buscarPartidaPorIdentificador(1L)).thenReturn(partida);
//    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(carta);
//
//    String resultado = servicioCombate.jugarTurno(1L, 1L);
//
//    // Assert flexible por el azar, pero seguro lógicamente
//    assertTrue(partida.getHpEnemigo() <= 43, "El enemigo debió recibir al menos 7 de daño");
//    assertTrue(
//      partida.getHpJugador() <= 95 && partida.getHpJugador() >= 90,
//      "El jugador debió recibir entre 5 y 10 de daño"
//    );
//    assertTrue(resultado.contains("Atacas con"));
//  }
//
//  @Test
//  public void queAlMatarAlEnemigoElEstadoSeaGanadorJugador() {
//    Partida partida = new Partida();
//    partida.setHpJugador(100);
//    partida.setHpEnemigo(10); // HP bajo
//
//    Enemigo enemigo = new Enemigo();
//    enemigo.setNombre("Orco");
//    partida.setEnemigo(enemigo);
//
//    Carta carta = new Carta();
//    carta.setNombre("Golpe Letal");
//    carta.setDanoMin(15);
//    carta.setDanoMax(15);
//    carta.setTipo("ATAQUE");
//
//    when(repositorioPartidaMock.buscarPartidaPorIdentificador(1L)).thenReturn(partida);
//    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(carta);
//
//    servicioCombate.jugarTurno(1L, 1L);
//
//    assertEquals(EnumEstadoPartida.GANADOR_JUGADOR, partida.getEnumEstadoPartida());
//    assertTrue(partida.getHpEnemigo() <= 0);
//  }
//
//  @Test
//  public void queAlJugarCartaDefensaFuerteNoSeRecibaDanoDelEnemigo() {
//    Partida partida = new Partida();
//    partida.setHpJugador(100);
//    partida.setHpEnemigo(50);
//
//    Enemigo enemigo = new Enemigo();
//    enemigo.setNombre("Orco");
//    enemigo.setDanoMin(3);
//    enemigo.setDanoMax(8); // Ataque máximo de 8
//    partida.setEnemigo(enemigo);
//
//    Carta carta = new Carta();
//    carta.setNombre("Muro de Piedra");
//    carta.setDefensaMin(10);
//    carta.setDefensaMax(10);
//    carta.setTipo("DEFENSA");
//
//    when(repositorioPartidaMock.buscarPartidaPorIdentificador(1L)).thenReturn(partida);
//    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(carta);
//
//    servicioCombate.jugarTurno(1L, 1L);
//
//    assertEquals(
//      100,
//      partida.getHpJugador(),
//      "El jugador no debió perder vida porque el escudo absorbió todo"
//    );
//  }
//
//  @Test
//  public void queAlRecibirDanoMortalElEstadoSeaGanadorEnemigo() {
//    Partida partida = new Partida();
//    partida.setHpJugador(10); // Jugador a punto de morir
//    partida.setHpEnemigo(100);
//
//    Enemigo dragon = new Enemigo();
//    dragon.setNombre("Dragón Ancestral");
//    dragon.setDanoMin(15); // El ataque mínimo (15) ya es mayor a la vida del jugador (10)
//    dragon.setDanoMax(25);
//    partida.setEnemigo(dragon);
//
//    Carta carta = new Carta();
//    carta.setNombre("Tajo");
//    carta.setDanoMin(15);
//    carta.setDanoMax(15);
//    carta.setTipo("ATAQUE");
//
//    when(repositorioPartidaMock.buscarPartidaPorIdentificador(1L)).thenReturn(partida);
//    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(carta);
//
//    servicioCombate.jugarTurno(1L, 1L);
//
//    assertEquals(EnumEstadoPartida.GANADOR_ENEMIGO, partida.getEnumEstadoPartida());
//    assertTrue(partida.getHpJugador() <= 0);
//  }
//
//  @Test
//  public void queAlUsarHechizoElJugadorRecupereVida() {
//    Partida partida = new Partida();
//    partida.setHpJugador(50); // Empieza por la mitad
//    partida.setHpEnemigo(100);
//
//    Enemigo enemigo = new Enemigo();
//    enemigo.setNombre("Tronco Inerte");
//    enemigo.setDanoMin(0); // Le sacamos el ataque para que no interfiera con la matemática de la curación
//    enemigo.setDanoMax(0);
//    partida.setEnemigo(enemigo);
//
//    Carta carta = new Carta();
//    carta.setNombre("Poción Curativa");
//    carta.setDefensaMin(10);
//    carta.setDefensaMax(10);
//    carta.setTipo("HECHIZO");
//
//    when(repositorioPartidaMock.buscarPartidaPorIdentificador(1L)).thenReturn(partida);
//    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(carta);
//
//    servicioCombate.jugarTurno(1L, 1L);
//
//    assertTrue(
//      partida.getHpJugador() >= 60 && partida.getHpJugador() <= 70,
//      "El jugador debió curarse entre 10 y 20 HP"
//    );
//  }
//}
//=======
////package com.tallerwebi.dominio;
////
////import static org.junit.jupiter.api.Assertions.*;
////import static org.mockito.Mockito.*;
////
////import org.junit.jupiter.api.BeforeEach;
////import org.junit.jupiter.api.Test;
////
////public class ServicioCombateTest {
////
////  private RepositorioPartida repositorioPartidaMock;
////  private RepositorioCarta repositorioCartaMock;
////  private ServicioCombateImpl servicioCombate;
////
////  @BeforeEach
////  public void init() {
////    this.repositorioPartidaMock = mock(RepositorioPartida.class);
////    this.repositorioCartaMock = mock(RepositorioCarta.class);
////    this.servicioCombate = new ServicioCombateImpl(repositorioPartidaMock, repositorioCartaMock);
////  }
////
////  @Test
////  public void queAlJugarTurnoAtaqueSeResteVidaAlEnemigoYElZombiContraataque() {
////    Partida partida = new Partida(100, 50, 1);
////    Carta carta = new Carta();
////    carta.setNombre("Golpe Básico");
////    carta.setDano(15);
////    carta.setDefensa(0);
////
////    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(carta);
////
////    String resultado = servicioCombate.jugarTurno(partida, 1L);
////
////    assertEquals(35, partida.getHpEnemigo()); // El Infectado tenía 50, pierde 15 -> queda en 35
////    assertEquals(95, partida.getHpJugador()); // El Jugador tenía 100, el Infectado le saca 5 fijos -> queda 95
////    assertTrue(resultado.contains("Hiciste 15 de Daño"));
////  }
////
////  @Test
////  public void queAlMatarAlZombiElEstadoSeaGanadorJugador() {
////    Partida partida = new Partida(100, 10, 1); // Infectado a punto de morir (10 HP)
////    Carta carta = new Carta();
////    carta.setNombre("Golpe Letal");
////    carta.setDano(15);
////    carta.setDefensa(0);
////
////    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(carta);
////
////    String resultado = servicioCombate.jugarTurno(partida, 1L);
////
////    assertEquals(EnumEstadoPartida.GANADOR_JUGADOR, partida.getEnumEstadoPartida());
////    assertTrue(resultado.contains("HA SIDO DESTRUIDO"));
////  }
////
////  @Test
////  public void queAlMorirPorContraataqueElEstadoSeaGanadorEnemigo() {
////    Partida partida = new Partida(4, 50, 1); // Jugador a un golpe de morir (el Infectado saca 5 fijos)
////    Carta carta = new Carta();
////    carta.setNombre("Golpe Flojo");
////    carta.setDano(10); // Golpe que no llega a matar al Infectado
////    carta.setDefensa(0);
////
////    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(carta);
////
////    String resultado = servicioCombate.jugarTurno(partida, 1L);
////
////    assertEquals(EnumEstadoPartida.GANADOR_ENEMIGO, partida.getEnumEstadoPartida());
////    assertTrue(resultado.contains("HAS MUERTO"));
////  }
////
////  @Test
////  public void queAlJugarCartaDefensaNoRecibaDano() {
////    Partida partida = new Partida(100, 50, 1);
////    Carta carta = new Carta();
////    carta.setNombre("Súper Escudo");
////    carta.setDano(0);
////    carta.setDefensa(10); // Defensa alta para bloquear los 5 del Infectado
////
////    when(repositorioCartaMock.buscarPorId(1L)).thenReturn(carta);
////
////    servicioCombate.jugarTurno(partida, 1L);
////
////    assertEquals(50, partida.getHpEnemigo()); // No le pegamos
////    assertEquals(100, partida.getHpJugador()); // Bloqueamos el golpe y no nos bajó vida
////  }
////}
//>>>>>>> origin/MikaRama7
