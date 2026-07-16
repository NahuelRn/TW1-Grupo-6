package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ServicioUsuarioRecompensaTest {

  @Mock
  private RepositorioUsuario repositorioUsuarioMock;

  @Mock
  private RepositorioCarta repositorioCartaMock;

  @Mock
  private RepositorioInventario repositorioInventarioMock;

  @Mock
  private ServicioCalculoRecompensa servicioCalculoRecompensaMock;

  @InjectMocks
  private ServicioUsuarioImpl servicioUsuario;

  @BeforeEach
  public void inicializarMocks() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void deberiaSumarOroYExperienciaAlUsuario() {
    Usuario usuario = new Usuario();
    usuario.setOro(100);
    usuario.setExperiencia(200);

    Partida partida = new Partida();

    RecompensaDTO recompensaDTO = new RecompensaDTO();
    recompensaDTO.setOro(50);
    recompensaDTO.setExperiencia(100);
    when(servicioCalculoRecompensaMock.obtenerRecompensa(partida)).thenReturn(recompensaDTO);

    servicioUsuario.aplicarRecompensa(usuario, partida);

    assertEquals(150, usuario.getOro());
    assertEquals(300, usuario.getExperiencia());
  }

  @Test
  public void deberiaAgregarCartaAlInventarioSiNoExisteDelUsuario() {
    Usuario usuario = new Usuario();
    usuario.setId(1L);

    Carta carta = new Carta();
    carta.setId(10L);

    Partida partida = new Partida();

    RecompensaDTO recompensaDTO = new RecompensaDTO();
    recompensaDTO.setIdCarta(10L);
    when(servicioCalculoRecompensaMock.obtenerRecompensa(partida)).thenReturn(recompensaDTO);

    when(repositorioCartaMock.buscarPorId(10L)).thenReturn(carta);
    when(repositorioInventarioMock.buscarItemDeJugador(1L, 10L)).thenReturn(null);

    servicioUsuario.aplicarRecompensa(usuario, partida);

    verify(repositorioInventarioMock, times(1)).guardar(any(ItemInventario.class));
  }

  @Test
  public void deberiaLanzarExcepcionSiElUsuarioNoExiste() {
    Partida partida = new Partida();

    RuntimeException excepcion = assertThrows(
      RuntimeException.class,
      () -> {
        servicioUsuario.aplicarRecompensa(null, partida);
      }
    );

    assertEquals("Error, usuario inválido.", excepcion.getMessage());
  }

  @Test
  public void deberiaLanzarExcepcionSiLaCartaNoExiste() {
    Usuario usuario = new Usuario();
    Partida partida = new Partida();

    RecompensaDTO recompensaDTO = new RecompensaDTO();
    recompensaDTO.setIdCarta(10L);
    when(servicioCalculoRecompensaMock.obtenerRecompensa(partida)).thenReturn(recompensaDTO);

    when(repositorioCartaMock.buscarPorId(10L)).thenReturn(null);

    RuntimeException excepcion = assertThrows(
      RuntimeException.class,
      () -> {
        servicioUsuario.aplicarRecompensa(usuario, partida);
      }
    );

    assertEquals("Error, carta no encontrada.", excepcion.getMessage());
  }

  @Test
  public void deberiaSumarUnaCantidadSiElUsuarioYaPoseeLaCarta() {
    Usuario usuario = new Usuario();
    usuario.setId(1L);

    Carta carta = new Carta();
    carta.setId(10L);

    ItemInventario itemInventario = new ItemInventario();
    itemInventario.setCantidad(1);

    Partida partida = new Partida();

    RecompensaDTO recompensaDTO = new RecompensaDTO();
    recompensaDTO.setIdCarta(10L);
    when(servicioCalculoRecompensaMock.obtenerRecompensa(partida)).thenReturn(recompensaDTO);

    when(repositorioCartaMock.buscarPorId(10L)).thenReturn(carta);
    when(repositorioInventarioMock.buscarItemDeJugador(1L, 10L)).thenReturn(itemInventario);

    servicioUsuario.aplicarRecompensa(usuario, partida);

    assertEquals(2, itemInventario.getCantidad());
  }
}
