package com.tallerwebi.dominio;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServicioMercadoImpl implements ServicioMercado {

  private static final String ESTADO_ACTIVA = "ACTIVA";
  private static final String ESTADO_FINALIZADA = "FINALIZADA";
  private static final int CANTIDAD_MINIMA_DUPLICADO = 1;

  private final RepositorioMercado repoMercado;
  private final RepositorioCarta repoCarta;

  public ServicioMercadoImpl(RepositorioMercado repoMercado, RepositorioCarta repoCarta) {
    this.repoMercado = repoMercado;
    this.repoCarta = repoCarta;
  }

  @Override
  public void publicarSolicitud(Usuario usuario, Long idCartaBuscada) throws Exception {
    boolean yaLaTiene = usuario
      .getInventario()
      .stream()
      .anyMatch(item -> item.getCarta().getId().equals(idCartaBuscada));
    if (yaLaTiene) {
      throw new Exception("No puedes solicitar una carta que ya posees");
    }

    List<PropuestaIntercambio> misTrades = repoMercado.listarMisTrades(usuario);
    boolean yaSolicitada = misTrades
      .stream()
      .anyMatch(t ->
        ESTADO_ACTIVA.equals(t.getEstado()) && t.getCartaBuscada().getId().equals(idCartaBuscada)
      );
    if (yaSolicitada) {
      throw new Exception("Ya tienes una solicitud activa para esta carta");
    }

    Carta cartaBuscada = repoCarta.buscarPorId(idCartaBuscada);
    PropuestaIntercambio nueva = new PropuestaIntercambio();
    nueva.setUsuarioEmisor(usuario);
    nueva.setCartaBuscada(cartaBuscada);
    nueva.setEstado(ESTADO_ACTIVA);
    repoMercado.guardar(nueva);
  }

  @Override
  public List<PropuestaIntercambio> obtenerOfertasCompatibles(Usuario usuario) {
    List<PropuestaIntercambio> activas = repoMercado.listarTodasLasActivas();
    List<PropuestaIntercambio> compatibles = new ArrayList<>();

    for (PropuestaIntercambio propuesta : activas) {
      if (
        !propuesta.getUsuarioEmisor().getId().equals(usuario.getId()) &&
        tieneCartaRepetida(usuario, propuesta.getCartaBuscada().getId())
      ) {
        compatibles.add(propuesta);
      }
    }
    return compatibles;
  }

  private boolean tieneCartaRepetida(Usuario usuario, Long idCarta) {
    for (ItemInventario item : usuario.getInventario()) {
      if (
        item.getCarta().getId().equals(idCarta) && item.getCantidad() > CANTIDAD_MINIMA_DUPLICADO
      ) {
        return true;
      }
    }
    return false;
  }

  @Override
  public List<Carta> obtenerCartasFaltantes(Usuario usuario) {
    List<Long> idsPoseidas = new ArrayList<>();
    for (ItemInventario item : usuario.getInventario()) {
      idsPoseidas.add(item.getCarta().getId());
    }
    return repoCarta.listarCartasFaltantes(idsPoseidas);
  }

  @Override
  public List<PropuestaIntercambio> obtenerMisTrades(Usuario usuario) {
    return repoMercado.listarMisTrades(usuario);
  }

  @Override
  public PropuestaIntercambio buscarPorId(Long id) {
    return repoMercado.buscarPorId(id);
  }

  @Override
  public List<Carta> obtenerOpcionesRecompensa(PropuestaIntercambio propuesta) {
    Usuario emisor = propuesta.getUsuarioEmisor();
    List<Carta> opciones = new ArrayList<>();
    for (ItemInventario item : emisor.getInventario()) {
      if (item.getCantidad() > CANTIDAD_MINIMA_DUPLICADO) {
        opciones.add(item.getCarta());
      }
    }
    return opciones;
  }

  @Override
  public void finalizarIntercambio(Usuario receptor, Long idPropuesta, Long idCartaRecompensa)
    throws Exception {
    PropuestaIntercambio propuesta = repoMercado.buscarPorId(idPropuesta);
    validarPropuestaBasica(propuesta);

    ItemInventario itemReceptorEntrega = buscarItemEnInventario(
      receptor,
      propuesta.getCartaBuscada().getId()
    );
    if (
      itemReceptorEntrega == null || itemReceptorEntrega.getCantidad() <= CANTIDAD_MINIMA_DUPLICADO
    ) {
      throw new Exception("No tienes la carta solicitada repetida");
    }

    ItemInventario itemEmisorOfrece = buscarItemEnInventario(
      propuesta.getUsuarioEmisor(),
      idCartaRecompensa
    );
    if (itemEmisorOfrece == null || itemEmisorOfrece.getCantidad() <= CANTIDAD_MINIMA_DUPLICADO) {
      throw new Exception("El emisor ya no dispone de esa recompensa");
    }

    Carta cartaRecompensa = repoCarta.buscarPorId(idCartaRecompensa);
    validarCompatibilidadRareza(
      propuesta.getCartaBuscada().getRareza(),
      cartaRecompensa.getRareza()
    );

    actualizarStocksDeIntercambio(
      propuesta.getUsuarioEmisor(),
      receptor,
      itemEmisorOfrece,
      itemReceptorEntrega
    );

    propuesta.setEstado(ESTADO_FINALIZADA);
    repoMercado.guardar(propuesta);
  }

  private void validarPropuestaBasica(PropuestaIntercambio propuesta) throws Exception {
    if (propuesta == null || !ESTADO_ACTIVA.equals(propuesta.getEstado())) {
      throw new Exception("La propuesta ya no esta disponible");
    }
  }

  private ItemInventario buscarItemEnInventario(Usuario usuario, Long idCarta) {
    for (ItemInventario item : usuario.getInventario()) {
      if (item.getCarta().getId().equals(idCarta)) {
        return item;
      }
    }
    return null;
  }

  private void actualizarStocksDeIntercambio(
    Usuario emisor,
    Usuario receptor,
    ItemInventario itemEmisorOfrece,
    ItemInventario itemReceptorEntrega
  ) {
    itemEmisorOfrece.setCantidad(itemEmisorOfrece.getCantidad() - 1);
    itemReceptorEntrega.setCantidad(itemReceptorEntrega.getCantidad() - 1);

    ItemInventario destinoEmisor = buscarItemEnInventario(
      emisor,
      itemReceptorEntrega.getCarta().getId()
    );
    if (destinoEmisor == null) {
      ItemInventario nuevo = new ItemInventario();
      nuevo.setCarta(itemReceptorEntrega.getCarta());
      nuevo.setCantidad(1);
      emisor.getInventario().add(nuevo);
    } else {
      destinoEmisor.setCantidad(destinoEmisor.getCantidad() + 1);
    }

    ItemInventario destinoReceptor = buscarItemEnInventario(
      receptor,
      itemEmisorOfrece.getCarta().getId()
    );
    if (destinoReceptor == null) {
      ItemInventario nuevo = new ItemInventario();
      nuevo.setCarta(itemEmisorOfrece.getCarta());
      nuevo.setCantidad(1);
      receptor.getInventario().add(nuevo);
    } else {
      destinoReceptor.setCantidad(destinoReceptor.getCantidad() + 1);
    }
  }

  @Override
  public void eliminarMiTrade(Usuario usuario, Long idPropuesta) throws Exception {
    PropuestaIntercambio propuesta = repoMercado.buscarPorId(idPropuesta);
    if (
      propuesta != null &&
      ESTADO_ACTIVA.equals(propuesta.getEstado()) &&
      propuesta.getUsuarioEmisor().getId().equals(usuario.getId())
    ) {
      repoMercado.eliminar(propuesta);
    } else {
      throw new Exception("No puedes eliminar este trade");
    }
  }

  private void validarCompatibilidadRareza(String rarezaEntregada, String rarezaRecompensa)
    throws Exception {
    int jerarquiaEntregada = obtenerValorRareza(rarezaEntregada);
    int jerarquiaRecompensa = obtenerValorRareza(rarezaRecompensa);

    if (jerarquiaRecompensa > jerarquiaEntregada) {
      throw new Exception(
        "La regla del mercado no permite una recompensa de rareza superior a la entregada"
      );
    }
  }

  private int obtenerValorRareza(String rareza) {
    switch (rareza.toUpperCase(Locale.ROOT)) {
      case "LEGENDARIA":
        return 4;
      case "EPICA":
        return 3;
      case "RARA":
        return 2;
      default:
        return 1;
    }
  }
}
