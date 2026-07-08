package com.tallerwebi.dominio;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ServicioTiendaImpl implements ServicioTienda {

  private final RepositorioUsuario REPOSITORIO_USUARIO;
  private final RepositorioCarta REPOSITORIO_CARTA;
  private final RepositorioInventario REPOSITORIO_INVENTARIO;

  public ServicioTiendaImpl(
    RepositorioUsuario repositorioUsuario,
    RepositorioCarta repositorioCarta,
    RepositorioInventario repositorioInventario
  ) {
    this.REPOSITORIO_USUARIO = repositorioUsuario;
    this.REPOSITORIO_CARTA = repositorioCarta;
    this.REPOSITORIO_INVENTARIO = repositorioInventario;
  }

  @Override
  public void comprarCarta(Usuario usuario, Long identificadorCarta) {
    Carta carta = this.REPOSITORIO_CARTA.buscarPorId(identificadorCarta);

    if (usuario == null || carta == null) {
      throw new RuntimeException("Error, usuario o carta no encontrados.");
    }

    if (usuario.getNivel() < carta.getNivelDesbloqueo()) {
      throw new RuntimeException(
        "Error, no tienes el nivel necesario para desbloquear esta carta."
      );
    }

    Integer costoOroCarta = carta.getValorOroBase();
    if (usuario.getOro() < costoOroCarta) {
      throw new RuntimeException("Error, oro insuficiente para realizar la compra.");
    }

    // Descontar oro al usuario y modificar/actualizar el usuario.
    usuario.setOro(usuario.getOro() - costoOroCarta);
    this.REPOSITORIO_USUARIO.modificar(usuario);

    // Agregar la carta al inventario, si el usuario ya tiene esa carta, suma esa cantidad, si no la tiene, la agrega.
    ItemInventario itemInventario =
      this.REPOSITORIO_INVENTARIO.buscarItemDeJugador(usuario.getId(), carta.getId());

    if (itemInventario != null) {
      itemInventario.setCantidad(itemInventario.getCantidad() + 1);
      this.REPOSITORIO_INVENTARIO.actualizar(itemInventario);
    } else {
      ItemInventario itemInventarioNuevo = new ItemInventario();
      itemInventarioNuevo.setJugador(usuario.getJugador());
      itemInventarioNuevo.setCarta(carta);
      itemInventarioNuevo.setCantidad(1);
      this.REPOSITORIO_INVENTARIO.guardar(itemInventarioNuevo);
    }
  }

  @Override
  public List<Carta> listarCartas() {
    return this.REPOSITORIO_CARTA.listarTodas();
  }
}
