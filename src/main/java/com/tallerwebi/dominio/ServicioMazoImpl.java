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
  private final RepositorioUsuario repositorioUsuario;

  public ServicioMazoImpl(
    RepositorioMazo repositorioMazo,
    RepositorioCarta repositorioCarta,
    RepositorioInventario repositorioInventario,
    RepositorioUsuario repositorioUsuario
  ) {
    this.repositorioMazo = repositorioMazo;
    this.repositorioCarta = repositorioCarta;
    this.repositorioInventario = repositorioInventario;
    this.repositorioUsuario = repositorioUsuario;
  }

  @Override
  public void validarYGuardarMazo(Mazo mazo, Long jugadorId) throws Exception {
    validarEstructuraMazo(mazo);

    repositorioMazo.guardar(mazo);

    vincularMazoAlUsuario(mazo, jugadorId);
  }

  private void vincularMazoAlUsuario(Mazo mazo, Long jugadorId) throws Exception {
    Usuario usuario = repositorioUsuario.buscarPorId(jugadorId);

    if (usuario != null) {
      usuario.setMazoActivo(mazo);
      repositorioUsuario.modificar(usuario);
    } else {
      throw new Exception("No se encontró el usuario para asignarle el mazo.");
    }
  }

  private static void validarEstructuraMazo(Mazo mazo) throws Exception {
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
  public List<ItemInventario> obtenerInventarioPorJugador(Long jugadorId) {
    List<ItemInventario> items = repositorioInventario.listarInventarioDeJugador(jugadorId);
    if (items == null) {
      return java.util.Collections.emptyList();
    }
    return items
      .stream()
      .filter(item -> item.getCarta() != null && item.getCantidad() >= 1)
      .collect(java.util.stream.Collectors.toList());
  }
}
