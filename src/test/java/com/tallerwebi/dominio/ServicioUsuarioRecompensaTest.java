//package com.tallerwebi.dominio;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//import org.junit.Test;
//
//public class ServicioUsuarioRecompensaTest {
//
//  @Test
//  public void deberiaSumarOroYExperienciaAlUsuario() {
//    RepositorioUsuario repositorioUsuario = mock(RepositorioUsuario.class);
//    RepositorioCarta repositorioCarta = mock(RepositorioCarta.class);
//    RepositorioInventario repositorioInventario = mock(RepositorioInventario.class);
//
//    ServicioUsuario servicioUsuario = new ServicioUsuarioImpl(
//      repositorioUsuario,
//      repositorioCarta,
//      repositorioInventario
//    );
//
//    Usuario usuario = new Usuario();
//    usuario.setOro(100);
//    usuario.setExperiencia(200);
//
//    when(repositorioUsuario.buscarUsuario("test@gmail.com", "1234")).thenReturn(usuario);
//
//    RecompensaDTO recompensaDTO = new RecompensaDTO();
//    recompensaDTO.setOro(50);
//    recompensaDTO.setExperiencia(100);
//
//    servicioUsuario.aplicarRecompensa("test@gmail.com", "1234", recompensaDTO);
//
//    assertEquals(150, usuario.getOro());
//    assertEquals(300, usuario.getExperiencia());
//  }
//
//  @Test
//  public void deberiaAgregarCartaAlInventarioSiNoExisteDelUsuario() {
//    RepositorioUsuario repositorioUsuario = mock(RepositorioUsuario.class);
//    RepositorioCarta repositorioCarta = mock(RepositorioCarta.class);
//    RepositorioInventario repositorioInventario = mock(RepositorioInventario.class);
//
//    ServicioUsuario servicioUsuario = new ServicioUsuarioImpl(
//      repositorioUsuario,
//      repositorioCarta,
//      repositorioInventario
//    );
//
//    Usuario usuario = new Usuario();
//    usuario.setId(1L);
//
//    Carta carta = new Carta();
//    carta.setId(10L);
//
//    when(repositorioUsuario.buscarUsuario("test@gmail.com", "1234")).thenReturn(usuario);
//    when(repositorioCarta.buscarPorId(10L)).thenReturn(carta);
//    when(repositorioInventario.buscarItemDeJugador(1L, 10L)).thenReturn(null);
//
//    RecompensaDTO recompensaDTO = new RecompensaDTO();
//    recompensaDTO.setIdCarta(10L);
//
//    servicioUsuario.aplicarRecompensa("test@gmail.com", "1234", recompensaDTO);
//
//    verify(repositorioInventario).guardar(any(ItemInventario.class)); // L
//  }
//
//  @Test
//  public void deberiaLanzarExcepcionSiElUsuarioNoExiste() {
//    RepositorioUsuario repositorioUsuario = mock(RepositorioUsuario.class);
//    RepositorioCarta repositorioCarta = mock(RepositorioCarta.class);
//    RepositorioInventario repositorioInventario = mock(RepositorioInventario.class);
//
//    ServicioUsuario servicioUsuario = new ServicioUsuarioImpl(
//      repositorioUsuario,
//      repositorioCarta,
//      repositorioInventario
//    );
//
//    when(repositorioUsuario.buscarUsuario("test@gmail.com", "1234")).thenReturn(null);
//
//    RecompensaDTO recompensaDTO = new RecompensaDTO();
//    try {
//      servicioUsuario.aplicarRecompensa("test@gmail.com", "1234", recompensaDTO);
//    } catch (RuntimeException runtimeException) {
//      assertEquals("Error, usuario no encontrado.", runtimeException.getMessage());
//    }
//  }
//
//  @Test
//  public void deberiaLanzarExcepcionSiLaCartaNoExiste() {
//    RepositorioUsuario repositorioUsuario = mock(RepositorioUsuario.class);
//    RepositorioCarta repositorioCarta = mock(RepositorioCarta.class);
//    RepositorioInventario repositorioInventario = mock(RepositorioInventario.class);
//
//    ServicioUsuario servicioUsuario = new ServicioUsuarioImpl(
//      repositorioUsuario,
//      repositorioCarta,
//      repositorioInventario
//    );
//
//    Usuario usuario = new Usuario();
//    when(repositorioUsuario.buscarUsuario("test@gmail.com", "1234")).thenReturn(usuario);
//    when(repositorioCarta.buscarPorId(10L)).thenReturn(null);
//
//    RecompensaDTO recompensaDTO = new RecompensaDTO();
//    recompensaDTO.setIdCarta(10L);
//
//    try {
//      servicioUsuario.aplicarRecompensa("test@gmail.com", "1234", recompensaDTO);
//    } catch (RuntimeException runtimeException) {
//      assertEquals("Error, carta no encontrada.", runtimeException.getMessage());
//    }
//  }
//
//  @Test
//  public void deberiaSumarUnaCantidadSiElUsuarioYaPoseeLaCarta() {
//    RepositorioUsuario repositorioUsuario = mock(RepositorioUsuario.class);
//    RepositorioCarta repositorioCarta = mock(RepositorioCarta.class);
//    RepositorioInventario repositorioInventario = mock(RepositorioInventario.class);
//
//    ServicioUsuario servicioUsuario = new ServicioUsuarioImpl(
//      repositorioUsuario,
//      repositorioCarta,
//      repositorioInventario
//    );
//
//    Usuario usuario = new Usuario();
//    usuario.setId(1L);
//
//    Carta carta = new Carta();
//    carta.setId(10L);
//
//    ItemInventario itemInventario = new ItemInventario();
//    itemInventario.setCantidad(1);
//
//    when(repositorioUsuario.buscarUsuario("test@gmail.com", "1234")).thenReturn(usuario);
//    when(repositorioCarta.buscarPorId(10L)).thenReturn(carta);
//    when(repositorioInventario.buscarItemDeJugador(1L, 10L)).thenReturn(itemInventario);
//
//    RecompensaDTO recompensaDTO = new RecompensaDTO();
//    recompensaDTO.setIdCarta(10L);
//
//    servicioUsuario.aplicarRecompensa("test@gmail.com", "1234", recompensaDTO);
//    assertEquals(2, itemInventario.getCantidad());
//  }
//}
