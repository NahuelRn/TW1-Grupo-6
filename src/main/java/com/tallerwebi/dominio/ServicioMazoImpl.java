package com.tallerwebi.dominio;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ServicioMazoImpl implements ServicioMazo {

  private static final int MAX_CARTAS = 15;
  private final RepositorioMazo repositorioMazo;
  private final RepositorioCarta repositorioCarta;
  private final RepositorioInventario repositorioInventario;

  // Inyectamos todos los repositorios necesarios
  public ServicioMazoImpl(
    RepositorioMazo repositorioMazo,
    RepositorioCarta repositorioCarta,
    RepositorioInventario repositorioInventario
  ) {
    this.repositorioMazo = repositorioMazo;
    this.repositorioCarta = repositorioCarta;
    this.repositorioInventario = repositorioInventario;
  }

  @Override
  public void validarYGuardarMazo(Mazo mazo) throws Exception {
    List<MazoCarta> nexos = mazo.getMazoCartas();

    if (nexos.size() != MAX_CARTAS) {
      throw new Exception("El mazo debe tener exactamente 15 cartas");
    }

    Set<Long> idsUnicos = new HashSet<>();
    for (MazoCarta nexo : nexos) {
      Carta carta = nexo.getCarta();
      if (idsUnicos.contains(carta.getId())) {
        throw new Exception("No puedes incluir cartas repetidas: " + carta.getNombre());
      }
      idsUnicos.add(carta.getId());
    }

    if (idsUnicos.size() != MAX_CARTAS) {
      throw new Exception("Error de validación interna con las cartas");
    }

    repositorioMazo.guardar(mazo);
  }

  @Override
  public List<Carta> buscarCartasPorIds(List<Long> ids) {
    List<Carta> cartas = new ArrayList<>();
    for (Long id : ids) {
      Carta carta = repositorioCarta.buscarPorId(id); // Vaya a la base de datos de verdad
      if (carta != null) {
        cartas.add(carta);
      }
    }
    return cartas;
  }

  @Override
  public List<Carta> obtenerInventarioPorJugador(Long jugadorId) {
    List<ItemInventario> items = repositorioInventario.listarInventarioDeJugador(jugadorId);
    List<Carta> cartasDelJugador = new ArrayList<>();

    for (ItemInventario item : items) {
      // ignorar ítems con cantidad 0 o menor
      if (item.getCarta() != null && item.getCantidad() >= 1) {
        cartasDelJugador.add(item.getCarta());
      }
    }
    return cartasDelJugador;
  }
}
