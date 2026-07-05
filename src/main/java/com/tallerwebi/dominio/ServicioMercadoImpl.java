package com.tallerwebi.dominio;

import java.util.ArrayList;
import java.util.List;
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
  private final RepositorioInventario repoInventario;

  public ServicioMercadoImpl(
    RepositorioMercado repoMercado,
    RepositorioCarta repoCarta,
    RepositorioInventario repoInventario
  ) {
    this.repoMercado = repoMercado;
    this.repoCarta = repoCarta;
    this.repoInventario = repoInventario;
  }

  // -----------------------------------------------------------------------
  // Helpers privados
  // -----------------------------------------------------------------------

  private List<ItemInventario> inventarioDe(Usuario usuario) {
    if (usuario == null || usuario.getJugador() == null) return new ArrayList<>();
    final List<ItemInventario> resultado = repoInventario.listarInventarioDeJugador(
      usuario.getJugador().getId()
    );
    return resultado != null ? resultado : new ArrayList<>();
  }

  private Usuario resolverUsuario(Long jugadorId) {
    return repoMercado.buscarUsuarioPorJugadorId(jugadorId);
  }

  private int nivelRareza(String rareza) {
    if (rareza == null) return 0;
    switch (rareza.trim().toUpperCase(java.util.Locale.ROOT)) {
      case "LEGENDARIA":
        return 5;
      case "EXOTICA":
        return 4;
      case "RARA":
        return 3;
      case "POCO COMUN":
      case "POCO_COMUN":
        return 2;
      case "COMUN":
        return 1;
      default:
        return 0;
    }
  }

  // -----------------------------------------------------------------------
  // Publicar solicitud
  // -----------------------------------------------------------------------

  @Override
  public void publicarSolicitud(Long jugadorId, Long idCartaBuscada) throws Exception {
    final Usuario usuario = resolverUsuario(jugadorId);
    if (usuario == null) throw new Exception("El usuario no existe en el sistema");

    final boolean yaLaTiene = inventarioDe(usuario)
      .stream()
      .anyMatch(item ->
        item.getCantidad() != null &&
        item.getCantidad() > 0 &&
        item.getCarta().getId().equals(idCartaBuscada)
      );
    if (yaLaTiene) throw new Exception("No puedes solicitar una carta que ya posees");

    final boolean yaSolicitada = repoMercado
      .listarMisTrades(usuario)
      .stream()
      .anyMatch(t ->
        ESTADO_ACTIVA.equals(t.getEstado()) && t.getCartaBuscada().getId().equals(idCartaBuscada)
      );
    if (yaSolicitada) throw new Exception("Ya tienes una solicitud activa para esta carta");

    final Carta cartaBuscada = repoCarta.buscarPorId(idCartaBuscada);
    final PropuestaIntercambio nueva = new PropuestaIntercambio();
    nueva.setUsuarioEmisor(usuario);
    nueva.setCartaBuscada(cartaBuscada);
    nueva.setEstado(ESTADO_ACTIVA);
    repoMercado.guardar(nueva);
  }

  // -----------------------------------------------------------------------
  // Ofertas compatibles
  // -----------------------------------------------------------------------

  @Override
  public List<PropuestaIntercambio> obtenerOfertasCompatibles(Long jugadorId) {
    final Usuario usuario = resolverUsuario(jugadorId);
    if (usuario == null) return new ArrayList<>();

    final List<PropuestaIntercambio> compatibles = new ArrayList<>();
    for (PropuestaIntercambio p : repoMercado.listarTodasLasActivas()) {
      if (!p.getUsuarioEmisor().getId().equals(usuario.getId())) {
        compatibles.add(p);
      }
    }
    return compatibles;
  }

  @Override
  public boolean usuarioTieneCartaRepetida(Long jugadorId, Long idCarta) {
    final Usuario usuario = resolverUsuario(jugadorId);
    if (usuario == null) return false;
    for (ItemInventario item : inventarioDe(usuario)) {
      if (
        item.getCarta().getId().equals(idCarta) &&
        item.getCantidad() != null &&
        item.getCantidad() > CANTIDAD_MINIMA_DUPLICADO
      ) {
        return true;
      }
    }
    return false;
  }

  // -----------------------------------------------------------------------
  // Cartas faltantes
  // -----------------------------------------------------------------------

  @Override
  public List<Carta> obtenerCartasFaltantes(Long jugadorId) {
    final Usuario usuario = resolverUsuario(jugadorId);
    final List<Long> idsPoseidas = new ArrayList<>();
    if (usuario != null) {
      for (ItemInventario item : inventarioDe(usuario)) {
        if (item.getCantidad() != null && item.getCantidad() > 0) {
          idsPoseidas.add(item.getCarta().getId());
        }
      }
    }
    return repoCarta.listarCartasFaltantes(idsPoseidas);
  }

  // -----------------------------------------------------------------------
  // Mis Trades y Trades Aceptados
  // -----------------------------------------------------------------------

  @Override
  public List<PropuestaIntercambio> obtenerMisTrades(Long jugadorId) {
    final Usuario usuario = resolverUsuario(jugadorId);
    if (usuario == null) return new ArrayList<>();
    return repoMercado.listarMisTrades(usuario);
  }

  @Override
  public List<PropuestaIntercambio> obtenerTradesAceptados(Long jugadorId) {
    final Usuario usuario = resolverUsuario(jugadorId);
    if (usuario == null) return new ArrayList<>();
    return repoMercado.listarTradesAceptados(usuario);
  }

  @Override
  public PropuestaIntercambio buscarPorId(Long id) {
    return repoMercado.buscarPorId(id);
  }

  @Override
  public boolean esEmisor(Long jugadorId, PropuestaIntercambio propuesta) {
    if (
      propuesta == null ||
      propuesta.getUsuarioEmisor() == null ||
      propuesta.getUsuarioEmisor().getJugador() == null
    ) {
      return false;
    }
    return propuesta.getUsuarioEmisor().getJugador().getId().equals(jugadorId);
  }

  @Override
  public List<Carta> obtenerOpcionesRecompensa(PropuestaIntercambio propuesta) {
    final Usuario emisor = propuesta.getUsuarioEmisor();
    final List<Carta> opciones = new ArrayList<>();
    for (ItemInventario item : inventarioDe(emisor)) {
      if (esOpcionValida(item, propuesta.getCartaBuscada().getRareza())) {
        opciones.add(item.getCarta());
      }
    }
    return opciones;
  }

  /**
   * Una opción es válida si:
   * - El emisor tiene más de 1 copia (puede cederla)
   * - Su rareza es IGUAL O MAYOR a la carta que entrega el receptor
   */
  private boolean esOpcionValida(ItemInventario item, String rarezaMinima) {
    return (
      item.getCantidad() != null &&
      item.getCantidad() > CANTIDAD_MINIMA_DUPLICADO &&
      nivelRareza(item.getCarta().getRareza()) >= nivelRareza(rarezaMinima)
    );
  }

  // -----------------------------------------------------------------------
  // Finalizar intercambio con nueva regla de rareza
  // -----------------------------------------------------------------------

  @Override
  public void finalizarIntercambio(Long jugadorId, Long idPropuesta, Long idCartaRecompensa)
    throws Exception {
    final Usuario receptor = resolverUsuario(jugadorId);
    if (receptor == null) throw new Exception("El usuario receptor no existe");

    final PropuestaIntercambio propuesta = repoMercado.buscarPorId(idPropuesta);
    validarPropuestaActiva(propuesta);

    final Carta cartaRecompensa = repoCarta.buscarPorId(idCartaRecompensa);
    validarRareza(propuesta.getCartaBuscada(), cartaRecompensa);

    final ItemInventario itemReceptorEntrega = obtenerItemValidado(
      receptor.getJugador().getId(),
      propuesta.getCartaBuscada().getId(),
      "No tienes la carta solicitada repetida"
    );

    final ItemInventario itemEmisorOfrece = obtenerItemValidado(
      propuesta.getUsuarioEmisor().getJugador().getId(),
      idCartaRecompensa,
      "El emisor ya no dispone de esa recompensa"
    );

    ejecutarTransferencia(
      propuesta.getUsuarioEmisor(),
      receptor,
      itemEmisorOfrece,
      itemReceptorEntrega,
      cartaRecompensa,
      propuesta
    );
  }

  private void validarPropuestaActiva(PropuestaIntercambio propuesta) throws Exception {
    if (propuesta == null || !ESTADO_ACTIVA.equals(propuesta.getEstado())) {
      throw new Exception("La propuesta ya no esta disponible");
    }
  }

  /**
   * NUEVA REGLA: la recompensa que elige el receptor debe ser de rareza
   * IGUAL O MAYOR a la carta que él entrega (cartaBuscada).
   *
   * Ejemplo: entrego RARA (3) → puedo pedir RARA(3), EXOTICA(4) o LEGENDARIA(5).
   */
  private void validarRareza(Carta cartaEntregada, Carta cartaRecompensa) throws Exception {
    if (nivelRareza(cartaRecompensa.getRareza()) < nivelRareza(cartaEntregada.getRareza())) {
      throw new Exception(
        "La regla del mercado no permite una recompensa de rareza inferior a la entregada"
      );
    }
  }

  private ItemInventario obtenerItemValidado(Long jugadorId, Long cartaId, String mensajeError)
    throws Exception {
    final ItemInventario item = repoInventario.buscarItemDeJugador(jugadorId, cartaId);
    if (item == null || item.getCantidad() <= CANTIDAD_MINIMA_DUPLICADO) {
      throw new Exception(mensajeError);
    }
    return item;
  }

  private void ejecutarTransferencia(
    Usuario emisor,
    Usuario receptor,
    ItemInventario itemEmisorOfrece,
    ItemInventario itemReceptorEntrega,
    Carta cartaRecompensa,
    PropuestaIntercambio propuesta
  ) {
    itemReceptorEntrega.setCantidad(itemReceptorEntrega.getCantidad() - 1);
    repoInventario.actualizar(itemReceptorEntrega);

    itemEmisorOfrece.setCantidad(itemEmisorOfrece.getCantidad() - 1);
    repoInventario.actualizar(itemEmisorOfrece);

    acreditarCarta(emisor, propuesta.getCartaBuscada());
    acreditarCarta(receptor, cartaRecompensa);

    propuesta.setCartaOfrecida(cartaRecompensa);
    propuesta.setUsuarioReceptor(receptor);
    propuesta.setEstado(ESTADO_FINALIZADA);
    repoMercado.guardar(propuesta);
  }

  private void acreditarCarta(Usuario usuario, Carta carta) {
    final Long jugadorId = usuario.getJugador().getId();
    final ItemInventario existente = repoInventario.buscarItemDeJugador(jugadorId, carta.getId());
    if (existente == null) {
      final ItemInventario nuevo = new ItemInventario();
      nuevo.setJugador(usuario.getJugador());
      nuevo.setCarta(carta);
      nuevo.setCantidad(1);
      repoInventario.guardar(nuevo);
    } else {
      existente.setCantidad(existente.getCantidad() + 1);
      repoInventario.actualizar(existente);
    }
  }

  // -----------------------------------------------------------------------
  // Eliminar mi trade
  // -----------------------------------------------------------------------

  @Override
  public void eliminarMiTrade(Long jugadorId, Long idPropuesta) throws Exception {
    final Usuario usuario = resolverUsuario(jugadorId);
    final PropuestaIntercambio propuesta = repoMercado.buscarPorId(idPropuesta);

    if (
      propuesta != null &&
      ESTADO_ACTIVA.equals(propuesta.getEstado()) &&
      usuario != null &&
      propuesta.getUsuarioEmisor().getId().equals(usuario.getId())
    ) {
      repoMercado.eliminar(propuesta);
    } else {
      throw new Exception("No puedes eliminar este trade");
    }
  }
}
