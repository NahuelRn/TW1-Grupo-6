package com.tallerwebi.dominio;

import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ServicioUsuarioImpl implements ServicioUsuario {

  private final RepositorioUsuario REPOSITORIO_USUARIO;
  private final RepositorioCarta REPOSITORIO_CARTA;
  private final RepositorioInventario REPOSITORIO_INVENTARIO;

  private ServicioCalculoRecompensa servicioCalculoRecompensa;

  public ServicioUsuarioImpl(
    RepositorioUsuario REPOSITORIO_USUARIO,
    RepositorioCarta REPOSITORIO_CARTA,
    RepositorioInventario REPOSITORIO_INVENTARIO,
    ServicioCalculoRecompensa servicioCalculoRecompensa
  ) {
    this.REPOSITORIO_USUARIO = REPOSITORIO_USUARIO;
    this.REPOSITORIO_CARTA = REPOSITORIO_CARTA;
    this.REPOSITORIO_INVENTARIO = REPOSITORIO_INVENTARIO;
    this.servicioCalculoRecompensa = servicioCalculoRecompensa;
  }

  @Override
  public Usuario buscarPorId(Long id) {
    return REPOSITORIO_USUARIO.buscarPorId(id);
  }

  @Override
  public void aplicarRecompensa(Usuario usuario, Partida partida) {
    if (usuario == null) {
      throw new RuntimeException("Error, usuario inválido.");
    }

    if (partida == null) {
      throw new RuntimeException("Error, partida inválida.");
    }

    RecompensaDTO recompensaDTO = this.servicioCalculoRecompensa.obtenerRecompensa(partida); // esto me lo tiro la ia
    aplicarStatsUsuario(usuario, recompensaDTO);
    procesarCarta(usuario, recompensaDTO);
  }

  private void aplicarStatsUsuario(Usuario usuario, RecompensaDTO dto) {
    usuario.sumarOro(dto.getOro());
    usuario.sumarExperiencia(dto.getExperiencia());
    this.REPOSITORIO_USUARIO.modificar(usuario);
  }

  private void procesarCarta(Usuario usuario, RecompensaDTO dto) {
    if (dto.getIdCarta() == null) {
      return;
    }

    Carta carta = REPOSITORIO_CARTA.buscarPorId(dto.getIdCarta());
    if (carta == null) {
      throw new RuntimeException("Error, carta no encontrada.");
    }

    ItemInventario itemInventario = REPOSITORIO_INVENTARIO.buscarItemDeJugador(
      usuario.getId(),
      carta.getId()
    );
    if (itemInventario != null) {
      itemInventario.setCantidad(itemInventario.getCantidad() + 1);
      this.REPOSITORIO_INVENTARIO.actualizar(itemInventario);
    } else {
      ItemInventario nuevo = new ItemInventario();
      nuevo.setCarta(carta);
      nuevo.setCantidad(1);
      this.REPOSITORIO_INVENTARIO.guardar(nuevo);
    }
  }
}
