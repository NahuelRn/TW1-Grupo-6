package com.tallerwebi.dominio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("servicioCarta")
@Transactional
public class ServicioCartaImpl implements ServicioCarta {

  private final RepositorioCarta repositorioCarta;
  private final RepositorioInventario repositorioInventario;
  private static final int CANTIDAD_MINIMA = 1;

  @Autowired
  public ServicioCartaImpl(
    RepositorioCarta repositorioCarta,
    RepositorioInventario repositorioInventario
  ) {
    this.repositorioCarta = repositorioCarta;
    this.repositorioInventario = repositorioInventario;
  }

  @Override
  public List<Carta> obtenerTodas() {
    return repositorioCarta.listarTodas();
  }

  @Override
  public Carta obtenerCartaPorId(Long id) {
    return this.repositorioCarta.buscarPorId(id);
  }

  @Override
  public List<ItemInventario> obtenerInventario(Long jugadorId) {
    List<ItemInventario> inventario = repositorioInventario.listarInventarioDeJugador(jugadorId);
    if (inventario == null) {
      return java.util.Collections.emptyList();
    }
    return inventario;
  }

  @Override
  public ColeccionDto obtenerColeccionAgrupada(Long jugadorId) {
    List<ItemInventario> miInventario = this.obtenerInventario(jugadorId);

    Map<Long, Integer> misCantidades = new HashMap<>();
    Map<Long, Carta> albumCompleto = new TreeMap<>();

    if (miInventario != null && !miInventario.isEmpty()) {
      List<Long> idsQueTengo = new ArrayList<>();

      for (ItemInventario item : miInventario) {
        Carta carta = item.getCarta();
        Long idCarta = carta.getId();

        Integer cantidadActual = misCantidades.getOrDefault(idCarta, 0);
        misCantidades.put(idCarta, cantidadActual + item.getCantidad());

        if (!idsQueTengo.contains(idCarta)) {
          idsQueTengo.add(idCarta);
          albumCompleto.put(idCarta, carta);
        }
      }

      List<Carta> cartasQueFaltan = repositorioCarta.listarCartasFaltantes(idsQueTengo);
      for (Carta cartaFaltante : cartasQueFaltan) {
        albumCompleto.put(cartaFaltante.getId(), cartaFaltante);
      }
    } else {
      List<Carta> todasLasCartas = this.obtenerTodas();
      for (Carta c : todasLasCartas) {
        albumCompleto.put(c.getId(), c);
      }
    }

    return new ColeccionDto(new ArrayList<>(albumCompleto.values()), misCantidades);
  }

  @Override
  public Carta buscarPorId(Long id) {
    return this.repositorioCarta.buscarPorId(id);
  }
}
