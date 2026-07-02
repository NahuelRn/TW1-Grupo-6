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
  // Helper: obtiene el inventario real del jugador usando repoInventario
  // -----------------------------------------------------------------------

  /**
   * Usa repoInventario (que consulta por jugador_id en BD) para obtener
   * el inventario real. Esto evita el bug de LazyLoading y garantiza que
   * los datos sean frescos dentro de la transacción.
   */
  private List<ItemInventario> inventarioDe(Usuario usuario) {
    if (usuario == null || usuario.getJugador() == null) {
      return new ArrayList<>();
    }
    List<ItemInventario> resultado = repoInventario.listarInventarioDeJugador(
      usuario.getJugador().getId()
    );
    return resultado != null ? resultado : new ArrayList<>();
  }

  /**
   * Resuelve el Usuario a partir del jugadorId que viene de la sesión.
   * jugadorId en sesión = ID de la tabla 'jugador', NO de 'usuario'.
   */
  private Usuario resolverUsuario(Long jugadorId) {
    return repoMercado.buscarUsuarioPorJugadorId(jugadorId);
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

    // No puede pedir una carta que ya tiene
    List<ItemInventario> inventario = inventarioDe(usuario);
    boolean yaLaTiene = inventario
      .stream()
      .anyMatch(item ->
        item.getCantidad() != null &&
        item.getCantidad() > 0 &&
        item.getCarta().getId().equals(idCartaBuscada)
      );
    if (yaLaTiene) {
      throw new Exception("No puedes solicitar una carta que ya posees");
    }

    // No puede tener dos solicitudes activas para la misma carta
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

  // -----------------------------------------------------------------------
  // Ofertas compatibles
  // -----------------------------------------------------------------------

  @Override
  public List<PropuestaIntercambio> obtenerOfertasCompatibles(Long jugadorId) {
    Usuario usuario = resolverUsuario(jugadorId);
    if (usuario == null) return new ArrayList<>();

    List<PropuestaIntercambio> activas = repoMercado.listarTodasLasActivas();
    List<PropuestaIntercambio> compatibles = new ArrayList<>();

    for (PropuestaIntercambio propuesta : activas) {
      if (!propuesta.getUsuarioEmisor().getId().equals(usuario.getId())) {
        compatibles.add(propuesta);
      }
    }
    return compatibles;
  }

  // -----------------------------------------------------------------------
  // Verificar si el usuario tiene una carta duplicada (para habilitar botón)
  // -----------------------------------------------------------------------

  @Override
  public boolean usuarioTieneCartaRepetida(Long jugadorId, Long idCarta) {
    Usuario usuario = resolverUsuario(jugadorId);
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
  // Cartas faltantes (grilla de publicar trade)
  // -----------------------------------------------------------------------

  /**
   * Devuelve las cartas que el jugador NO posee en absoluto.
   * Usa repoInventario para leer el inventario real desde BD.
   */
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
    // Si idsPoseidas está vacío porque el usuario no tiene nada,
    // listarCartasFaltantes([]) devuelve TODAS las cartas (comportamiento correcto).
    return repoCarta.listarCartasFaltantes(idsPoseidas);
  }

  // -----------------------------------------------------------------------
  // Mis Trades
  // -----------------------------------------------------------------------

  @Override
  public List<PropuestaIntercambio> obtenerMisTrades(Long jugadorId) {
    Usuario usuario = resolverUsuario(jugadorId);
    if (usuario == null) return new ArrayList<>();
    return repoMercado.listarMisTrades(usuario);
  }

  // -----------------------------------------------------------------------
  // Buscar propuesta por ID
  // -----------------------------------------------------------------------

  @Override
  public PropuestaIntercambio buscarPorId(Long id) {
    return repoMercado.buscarPorId(id);
  }

  // -----------------------------------------------------------------------
  // Opciones de recompensa (cartas del emisor con cantidad > 1)
  // -----------------------------------------------------------------------

  @Override
  public List<Carta> obtenerOpcionesRecompensa(PropuestaIntercambio propuesta) {
    List<Carta> opciones = new ArrayList<>();
    for (ItemInventario item : inventarioDe(propuesta.getUsuarioEmisor())) {
      if (item.getCantidad() != null && item.getCantidad() > CANTIDAD_MINIMA_DUPLICADO) {
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

    // El receptor debe tener la carta buscada duplicada
    ItemInventario itemReceptorEntrega = buscarItemEnInventario(
      receptor,
      propuesta.getCartaBuscada().getId()
    );
    if (
      itemReceptorEntrega == null || itemReceptorEntrega.getCantidad() <= CANTIDAD_MINIMA_DUPLICADO
    ) {
      throw new Exception("No tienes la carta solicitada repetida");
    }

    // El emisor debe tener la recompensa elegida duplicada
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

    actualizarStocks(propuesta.getUsuarioEmisor(), receptor, itemEmisorOfrece, itemReceptorEntrega);

    propuesta.setCartaOfrecida(cartaRecompensa);
    propuesta.setUsuarioReceptor(receptor);
    propuesta.setEstado(ESTADO_FINALIZADA);
    repoMercado.guardar(propuesta);
  }

  private void validarPropuestaActiva(PropuestaIntercambio propuesta) throws Exception {
    if (propuesta == null || !ESTADO_ACTIVA.equals(propuesta.getEstado())) {
      throw new Exception("La propuesta ya no esta disponible");
    }
  }

  private ItemInventario buscarItemEnInventario(Usuario usuario, Long idCarta) {
    for (ItemInventario item : inventarioDe(usuario)) {
      if (item.getCarta().getId().equals(idCarta)) {
        return item;
      }
    }
    return null;
  }

  private void actualizarStocks(
    Usuario emisor,
    Usuario receptor,
    ItemInventario itemEmisorOfrece,
    ItemInventario itemReceptorEntrega
  ) {
    itemEmisorOfrece.setCantidad(itemEmisorOfrece.getCantidad() - 1);
    itemReceptorEntrega.setCantidad(itemReceptorEntrega.getCantidad() - 1);

    // Acreditar al emisor la carta que entregó el receptor
    ItemInventario destinoEmisor = buscarItemEnInventario(
      emisor,
      itemReceptorEntrega.getCarta().getId()
    );
    if (destinoEmisor == null) {
      ItemInventario nuevo = new ItemInventario();
      nuevo.setJugador(emisor.getJugador());
      nuevo.setCarta(itemReceptorEntrega.getCarta());
      nuevo.setCantidad(1);
      repoInventario.guardar(nuevo);
    } else {
      destinoEmisor.setCantidad(destinoEmisor.getCantidad() + 1);
      repoInventario.actualizar(destinoEmisor);
    }

    // Acreditar al receptor la carta que entregó el emisor
    ItemInventario destinoReceptor = buscarItemEnInventario(
      receptor,
      itemEmisorOfrece.getCarta().getId()
    );
    if (destinoReceptor == null) {
      ItemInventario nuevo = new ItemInventario();
      nuevo.setJugador(receptor.getJugador());
      nuevo.setCarta(itemEmisorOfrece.getCarta());
      nuevo.setCantidad(1);
      repoInventario.guardar(nuevo);
    } else {
      destinoReceptor.setCantidad(destinoReceptor.getCantidad() + 1);
      repoInventario.actualizar(destinoReceptor);
    }

    // Persistir las cantidades descontadas
    repoInventario.actualizar(itemEmisorOfrece);
    repoInventario.actualizar(itemReceptorEntrega);
  }

  // -----------------------------------------------------------------------
  // Eliminar mi trade
  // -----------------------------------------------------------------------

  @Override
  public void eliminarMiTrade(Long jugadorId, Long idPropuesta) throws Exception {
    // Para eliminar usamos buscarUsuarioPorJugadorId igual que el resto
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

  // -----------------------------------------------------------------------
  // Validación de rareza
  // -----------------------------------------------------------------------

  private void validarCompatibilidadRareza(String rarezaEntregada, String rarezaRecompensa)
    throws Exception {
    if (obtenerValorRareza(rarezaRecompensa) > obtenerValorRareza(rarezaEntregada)) {
      throw new Exception(
        "La regla del mercado no permite una recompensa de rareza superior a la entregada"
      );
    }
  }

  private int obtenerValorRareza(String rareza) {
    if (rareza == null) return 1;
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
        return 1;
    }
  }
}
