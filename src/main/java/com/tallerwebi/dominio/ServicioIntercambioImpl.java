package com.tallerwebi.dominio;

import java.util.Collections;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioIntercambio")
@Transactional
public class ServicioIntercambioImpl implements ServicioIntercambio {

  private static final int CANTIDAD_CARTAS_REQUERIDAS = 4;
  private static final String RAREZA_COMUN = "Comun";
  private static final String RAREZA_POCO_COMUN = "Poco Comun";
  private static final String RAREZA_RARA = "Rara";
  private static final String RAREZA_EXOTICA = "Exotica";
  private static final String RAREZA_LEGENDARIA = "Legendaria";

  private final RepositorioCarta repositorioCarta;
  private final RepositorioInventario repositorioInventario;
  private final RepositorioMercado repositorioMercado;

  @Autowired
  public ServicioIntercambioImpl(
    RepositorioCarta repositorioCarta,
    RepositorioInventario repositorioInventario,
    RepositorioMercado repositorioMercado
  ) {
    this.repositorioCarta = repositorioCarta;
    this.repositorioInventario = repositorioInventario;
    this.repositorioMercado = repositorioMercado;
  }

  // -----------------------------------------------------------------------

  @Override
  public List<ItemInventario> obtenerInventario(Long jugadorId) {
    List<ItemInventario> inventarioCompleto = repositorioInventario.listarInventarioDeJugador(
      jugadorId
    );
    if (inventarioCompleto == null) return java.util.Collections.emptyList();

    return inventarioCompleto
      .stream()
      .filter(item -> item.getCantidad() > 0)
      .collect(java.util.stream.Collectors.toList());
  }

  @Override
  public List<ItemInventario> obtenerInventarioFiltrado(Long jugadorId, String rareza) {
    List<ItemInventario> inventario = obtenerInventario(jugadorId);
    if (rareza == null || rareza.equalsIgnoreCase("all") || rareza.trim().isEmpty()) {
      return inventario;
    }
    return inventario
      .stream()
      .filter(item ->
        item.getCarta() != null &&
        item.getCarta().getRareza() != null &&
        item.getCarta().getRareza().trim().equalsIgnoreCase(rareza.trim())
      )
      .collect(java.util.stream.Collectors.toList());
  }

  @Override
  public Carta realizarMejora(Long jugadorId, List<Long> idsCartasEntregadas) throws Exception {
    if (idsCartasEntregadas == null || idsCartasEntregadas.size() != CANTIDAD_CARTAS_REQUERIDAS) {
      throw new Exception("Debes entregar exactamente 4 cartas");
    }

    final String rarezaActual = obtenerRarezaDeLaPrimera(idsCartasEntregadas);
    validarRarezaUniforme(idsCartasEntregadas, rarezaActual);

    final List<Carta> posiblesPremios = repositorioCarta.buscarPorRareza(
      obtenerSiguienteRareza(rarezaActual)
    );
    if (posiblesPremios == null || posiblesPremios.isEmpty()) {
      throw new Exception("No hay cartas disponibles de la rareza superior");
    }

    descontarCartasEntregadas(jugadorId, idsCartasEntregadas);

    Collections.shuffle(posiblesPremios);
    final Carta premio = posiblesPremios.get(0);

    otorgarCartaPremio(jugadorId, premio);

    return premio;
  }

  // -----------------------------------------------------------------------
  // BUG FIX: otorgarCartaPremio ahora asigna el Jugador al nuevo item
  // -----------------------------------------------------------------------

  private void otorgarCartaPremio(Long jugadorId, Carta premio) {
    ItemInventario itemExistente = repositorioInventario.buscarItemDeJugador(
      jugadorId,
      premio.getId()
    );

    if (itemExistente != null) {
      // El jugador ya tiene esa carta → solo sumamos 1
      itemExistente.setCantidad(itemExistente.getCantidad() + 1);
      repositorioInventario.actualizar(itemExistente);
    } else {
      // El jugador no tiene esa carta → creamos el item CON el jugador asignado
      final Usuario usuarioTemp = repositorioMercado.buscarUsuarioPorJugadorId(jugadorId);
      final Jugador jugador = usuarioTemp.getJugador();
      final ItemInventario nuevoItem = new ItemInventario();
      nuevoItem.setJugador(jugador); // ← FIX: asignar el jugador
      nuevoItem.setCarta(premio);
      nuevoItem.setCantidad(1);
      repositorioInventario.guardar(nuevoItem);
    }
  }

  // -----------------------------------------------------------------------

  private void descontarCartasEntregadas(Long jugadorId, List<Long> idsCartasEntregadas)
    throws Exception {
    for (Long idCartaEntregada : idsCartasEntregadas) {
      final ItemInventario item = repositorioInventario.buscarItemDeJugador(
        jugadorId,
        idCartaEntregada
      );

      if (item == null || item.getCantidad() < 1) {
        throw new Exception(
          "No tienes suficientes copias en tu inventario de la carta con ID: " + idCartaEntregada
        );
      }

      item.setCantidad(item.getCantidad() - 1);
      repositorioInventario.actualizar(item);
    }
  }

  private String obtenerRarezaDeLaPrimera(List<Long> ids) throws Exception {
    final Carta primera = repositorioCarta.buscarPorId(ids.get(0));
    if (primera == null) throw new Exception("Una de las cartas no existe");
    return primera.getRareza();
  }

  private void validarRarezaUniforme(List<Long> ids, String rareza) throws Exception {
    if (rareza == null) throw new Exception("La rareza de las cartas no puede ser nula");
    for (int i = 1; i < ids.size(); i++) {
      final Carta carta = repositorioCarta.buscarPorId(ids.get(i));
      if (carta == null) throw new Exception("Una de las cartas no existe");
      if (carta.getRareza() == null || !carta.getRareza().trim().equalsIgnoreCase(rareza.trim())) {
        throw new Exception("Todas las cartas deben ser de la misma rareza");
      }
    }
  }

  @Override
  public Double transformarEnOro(Long idCarta) {
    final Carta carta = repositorioCarta.buscarPorId(idCarta);
    return carta.getValorOroBase() * 0.5;
  }

  private String obtenerSiguienteRareza(String actual) throws Exception {
    if (actual == null) throw new Exception("La rareza actual no puede ser nula");
    final String normalizada = actual.trim();
    if (RAREZA_COMUN.equalsIgnoreCase(normalizada)) return RAREZA_POCO_COMUN;
    if (RAREZA_POCO_COMUN.equalsIgnoreCase(normalizada)) return RAREZA_RARA;
    if (RAREZA_RARA.equalsIgnoreCase(normalizada)) return RAREZA_EXOTICA;
    if (RAREZA_EXOTICA.equalsIgnoreCase(normalizada)) return RAREZA_LEGENDARIA;
    throw new Exception("No se puede mejorar una carta Legendaria o de tipo desconocido");
  }
}
