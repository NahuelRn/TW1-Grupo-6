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
    return repositorioCarta.buscarPorId(id);
  }

  @Override
  public List<ItemInventario> obtenerInventario(Long jugadorId) {
    return repositorioInventario.listarInventarioDeJugador(jugadorId);
  }

  @Override
  public ColeccionDTO obtenerColeccionAgrupada(Long jugadorId) {
    // 1. Obtenemos TODAS las cartas del juego (Para mantener el efecto "Álbum")
    List<Carta> todasLasCartasBruto = this.obtenerTodas();
    Map<Long, Carta> cartasUnicas = new TreeMap<>();
    for (Carta c : todasLasCartasBruto) {
      cartasUnicas.put(c.getId(), c);
    }

    // 2. Mapeamos el inventario real: id de la Carta -> Cantidad
    Map<Long, Integer> misCantidades = new HashMap<>();
    List<ItemInventario> miInventario = this.obtenerInventario(jugadorId);

    for (ItemInventario item : miInventario) {
      if (item.getCantidad() < CANTIDAD_MINIMA) {
        continue;
      }
      Long idCarta = item.getCarta().getId();
      Integer cantidadActual = misCantidades.getOrDefault(idCarta, 0);
      misCantidades.put(idCarta, cantidadActual + item.getCantidad());
    }

    // 3. Servimos todo en la "bandeja" (DTO): TODAS las cartas y SOLO las cantidades que posee
    return new ColeccionDTO(new ArrayList<>(cartasUnicas.values()), misCantidades);
  }
}
