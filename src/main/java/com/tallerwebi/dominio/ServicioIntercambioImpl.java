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

  @Autowired
  public ServicioIntercambioImpl(
    RepositorioCarta repositorioCarta,
    RepositorioInventario repositorioInventario
  ) {
    this.repositorioCarta = repositorioCarta;
    this.repositorioInventario = repositorioInventario;
  }

  // Modificado: Ahora el servicio puede recibir una rareza para filtrar nativamente
  @Override
  public List<ItemInventario> obtenerInventario(Long jugadorId) {
    List<ItemInventario> inventarioCompleto = repositorioInventario.listarInventarioDeJugador(
      jugadorId
    );
    if (inventarioCompleto == null) {
      return java.util.Collections.emptyList();
    }

    // Mantenemos el filtro básico de cantidad > 0 para no arrastrar fantasmas
    return inventarioCompleto
      .stream()
      .filter(item -> item.getCantidad() > 0)
      .collect(java.util.stream.Collectors.toList());
  }

  // Sobrecarga opcional por si tu controlador necesita filtrar directamente por Rareza desde Java
  public List<ItemInventario> obtenerInventarioFiltrado(Long jugadorId, String rareza) {
    List<ItemInventario> inventario = obtenerInventario(jugadorId);
    if (rareza == null || rareza.equalsIgnoreCase("all") || rareza.isEmpty()) {
      return inventario;
    }

    return inventario
      .stream()
      .filter(item ->
        item.getCarta().getRareza() != null && item.getCarta().getRareza().equalsIgnoreCase(rareza)
      )
      .collect(java.util.stream.Collectors.toList());
  }

  @Override
  public Carta realizarMejora(Long jugadorId, List<Long> idsCartasEntregadas) throws Exception {
    if (idsCartasEntregadas == null || idsCartasEntregadas.size() != CANTIDAD_CARTAS_REQUERIDAS) {
      throw new Exception("Debes entregar exactamente 4 cartas");
    }

    String rarezaActual = obtenerRarezaDeLaPrimera(idsCartasEntregadas);
    validarRarezaUniforme(idsCartasEntregadas, rarezaActual);

    List<Carta> posiblesPremios = repositorioCarta.buscarPorRareza(
      obtenerSiguienteRareza(rarezaActual)
    );
    if (posiblesPremios == null || posiblesPremios.isEmpty()) {
      throw new Exception("No hay cartas disponibles de la rareza superior");
    }

    descontarCartasEntregadas(jugadorId, idsCartasEntregadas);

    Collections.shuffle(posiblesPremios);
    Carta premio = posiblesPremios.get(0);

    otorgarCartaPremio(jugadorId, premio);

    return premio;
  }

  private void descontarCartasEntregadas(Long jugadorId, List<Long> idsCartasEntregadas)
    throws Exception {
    for (Long idCartaEntregada : idsCartasEntregadas) {
      ItemInventario itemEncontrado = repositorioInventario.buscarItemDeJugador(
        jugadorId,
        idCartaEntregada
      );

      if (itemEncontrado == null || itemEncontrado.getCantidad() < 1) {
        throw new Exception(
          "No tienes suficientes copias en tu inventario de la carta con ID: " + idCartaEntregada
        );
      }

      itemEncontrado.setCantidad(itemEncontrado.getCantidad() - 1);
      repositorioInventario.actualizar(itemEncontrado);
    }
  }

  private void otorgarCartaPremio(Long jugadorId, Carta premio) {
    ItemInventario itemPremio = repositorioInventario.buscarItemDeJugador(
      jugadorId,
      premio.getId()
    );

    if (itemPremio != null) {
      itemPremio.setCantidad(itemPremio.getCantidad() + 1);
      repositorioInventario.actualizar(itemPremio);
    } else {
      ItemInventario nuevoItem = new ItemInventario();
      nuevoItem.setCarta(premio);
      nuevoItem.setCantidad(1);
      repositorioInventario.guardar(nuevoItem);
    }
  }

  private String obtenerRarezaDeLaPrimera(List<Long> ids) throws Exception {
    Carta primera = repositorioCarta.buscarPorId(ids.get(0));
    if (primera == null) {
      throw new Exception("Una de las cartas no existe");
    }
    return primera.getRareza();
  }

  private void validarRarezaUniforme(List<Long> ids, String rareza) throws Exception {
    for (int i = 1; i < ids.size(); i++) {
      Carta carta = repositorioCarta.buscarPorId(ids.get(i));
      if (carta == null) {
        throw new Exception("Una de las cartas no existe");
      }
      if (!carta.getRareza().equals(rareza)) {
        throw new Exception("Todas las cartas deben ser de la misma rareza");
      }
    }
  }

  @Override
  public Double transformarEnOro(Long idCarta) {
    Carta carta = repositorioCarta.buscarPorId(idCarta);
    return carta.getValorOroBase() * 0.5;
  }

  private String obtenerSiguienteRareza(String actual) throws Exception {
    switch (actual.trim()) {
      case RAREZA_COMUN:
        return RAREZA_POCO_COMUN;
      case RAREZA_POCO_COMUN:
        return RAREZA_RARA;
      case RAREZA_RARA:
        return RAREZA_EXOTICA;
      case RAREZA_EXOTICA:
        return RAREZA_LEGENDARIA;
      default:
        throw new Exception("No se puede mejorar una carta Legendaria");
    }
  }
}
