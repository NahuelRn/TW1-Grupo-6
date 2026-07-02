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
    if (usuario == null || usuario.getJugador() == null) {
      return new ArrayList<>();
    }

    List<ItemInventario> resultado = repoInventario.listarInventarioDeJugador(
      usuario.getJugador().getId()
    );

    return resultado != null ? resultado : new ArrayList<>();
  }

  private Usuario resolverUsuario(Long jugadorId) {
    return repoMercado.buscarUsuarioPorJugadorId(jugadorId);
  }

  private int nivelRareza(String rareza) {
    if (rareza == null) {
      return 0;
    }

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
    Usuario usuario = resolverUsuario(jugadorId);

    if (usuario == null) {
      throw new Exception("El usuario no existe en el sistema");
    }

    boolean yaLaTiene = inventarioDe(usuario)
      .stream()
      .anyMatch(item ->
        item.getCantidad() != null &&
        item.getCantidad() > 0 &&
        item.getCarta().getId().equals(idCartaBuscada)
      );

    if (yaLaTiene) {
      throw new Exception("No puedes solicitar una carta que ya posees");
    }

    boolean yaSolicitada = repoMercado
      .listarMisTrades(usuario)
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

  // -----------------------------------------------------------------------
  // Ofertas compatibles
  // -----------------------------------------------------------------------

  @Override
  public List<PropuestaIntercambio> obtenerOfertasCompatibles(Long jugadorId) {
    Usuario usuario = resolverUsuario(jugadorId);

    if (usuario == null) {
      return new ArrayList<>();
    }

    List<PropuestaIntercambio> compatibles = new ArrayList<>();

    for (PropuestaIntercambio p : repoMercado.listarTodasLasActivas()) {
      if (!p.getUsuarioEmisor().getId().equals(usuario.getId())) {
        compatibles.add(p);
      }
    }

    return compatibles;
  }

  @Override
  public boolean usuarioTieneCartaRepetida(Long jugadorId, Long idCarta) {
    Usuario usuario = resolverUsuario(jugadorId);

    if (usuario == null) {
      return false;
    }

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
    Usuario usuario = resolverUsuario(jugadorId);

    List<Long> idsPoseidas = new ArrayList<>();

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
  // Mis Trades
  // -----------------------------------------------------------------------

  @Override
  public List<PropuestaIntercambio> obtenerMisTrades(Long jugadorId) {
    Usuario usuario = resolverUsuario(jugadorId);

    if (usuario == null) {
      return new ArrayList<>();
    }

    return repoMercado.listarMisTrades(usuario);
  }

  @Override
  public List<PropuestaIntercambio> obtenerTradesAceptados(Long jugadorId) {
    Usuario usuario = resolverUsuario(jugadorId);

    if (usuario == null) {
      return new ArrayList<>();
    }

    return repoMercado.listarTradesAceptados(usuario);
  }

  @Override
  public PropuestaIntercambio buscarPorId(Long id) {
    return repoMercado.buscarPorId(id);
  }

  @Override
  public boolean esEmisor(Long jugadorId, PropuestaIntercambio propuesta) {
    Usuario usuario = resolverUsuario(jugadorId);

    return (
      usuario != null &&
      propuesta.getUsuarioEmisor() != null &&
      propuesta.getUsuarioEmisor().getId().equals(usuario.getId())
    );
  }

  // -----------------------------------------------------------------------
  // Opciones de recompensa
  // -----------------------------------------------------------------------

  @Override
  public List<Carta> obtenerOpcionesRecompensa(PropuestaIntercambio propuesta) {
    Usuario emisor = propuesta.getUsuarioEmisor();

    List<Carta> opciones = new ArrayList<>();

    for (ItemInventario item : inventarioDe(emisor)) {
      if (
        item.getCantidad() != null &&
        item.getCantidad() > CANTIDAD_MINIMA_DUPLICADO &&
        nivelRareza(item.getCarta().getRareza()) <=
          nivelRareza(propuesta.getCartaBuscada().getRareza())
      ) {
        opciones.add(item.getCarta());
      }
    }

    return opciones;
  }

  // -----------------------------------------------------------------------
  // Finalizar intercambio
  // -----------------------------------------------------------------------

  @Override
  public void finalizarIntercambio(Long jugadorId, Long idPropuesta, Long idCartaRecompensa)
    throws Exception {
    Usuario receptor = resolverUsuario(jugadorId);

    if (receptor == null) {
      throw new Exception("El usuario receptor no existe");
    }

    PropuestaIntercambio propuesta = repoMercado.buscarPorId(idPropuesta);

    validarPropuestaActiva(propuesta);

    Carta cartaRecompensa = repoCarta.buscarPorId(idCartaRecompensa);

    validarRareza(propuesta.getCartaBuscada(), cartaRecompensa);

    ItemInventario itemReceptorEntrega = obtenerItemValidado(
      receptor.getJugador().getId(),
      propuesta.getCartaBuscada().getId(),
      "No tienes la carta solicitada repetida"
    );

    ItemInventario itemEmisorOfrece = obtenerItemValidado(
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

  private void validarRareza(Carta cartaEntregada, Carta cartaRecompensa) throws Exception {
    if (nivelRareza(cartaRecompensa.getRareza()) > nivelRareza(cartaEntregada.getRareza())) {
      throw new Exception(
        "La regla del mercado no permite una recompensa de rareza superior a la entregada"
      );
    }
  }

  private ItemInventario obtenerItemValidado(Long jugadorId, Long cartaId, String mensajeError)
    throws Exception {
    ItemInventario item = repoInventario.buscarItemDeJugador(jugadorId, cartaId);

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
    Long jugadorId = usuario.getJugador().getId();

    ItemInventario existente = repoInventario.buscarItemDeJugador(jugadorId, carta.getId());

    if (existente == null) {
      ItemInventario nuevo = new ItemInventario();
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
  // Eliminar trade
  // -----------------------------------------------------------------------

  @Override
  public void eliminarMiTrade(Long jugadorId, Long idPropuesta) throws Exception {
    Usuario usuario = resolverUsuario(jugadorId);

    PropuestaIntercambio propuesta = repoMercado.buscarPorId(idPropuesta);

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
