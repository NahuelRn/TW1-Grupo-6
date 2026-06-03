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
  public ColeccionDto obtenerColeccionAgrupada(Long jugadorId) {
    List<Carta> todasLasCartasBruto = this.obtenerTodas();
    Map<Long, Carta> cartasUnicas = new TreeMap<>();
    for (Carta c : todasLasCartasBruto) {
      cartasUnicas.put(c.getId(), c);
    }

    List<ItemInventario> miInventario = this.obtenerInventario(jugadorId);
    Map<Long, Integer> misCantidades = new HashMap<>();

    if (miInventario != null) {
      for (ItemInventario item : miInventario) {
        Long idCarta = item.getCarta().getId();
        Integer cantidadActual = misCantidades.getOrDefault(idCarta, 0);
        misCantidades.put(idCarta, cantidadActual + item.getCantidad());
      }
    }

    return new ColeccionDto(new ArrayList<>(cartasUnicas.values()), misCantidades);
  }
}
