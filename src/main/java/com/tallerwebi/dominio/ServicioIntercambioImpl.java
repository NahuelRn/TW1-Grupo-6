package com.tallerwebi.dominio;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ServicioIntercambioImpl implements ServicioIntercambio {

  private static final int CANTIDAD_CARTAS_REQUERIDAS = 4;

  private final RepositorioCarta repositorioCarta;
  private final RepositorioInventario repositorioInventario;

  public ServicioIntercambioImpl(
    RepositorioCarta repositorioCarta,
    RepositorioInventario repositorioInventario
  ) {
    this.repositorioCarta = repositorioCarta;
    this.repositorioInventario = repositorioInventario;
  }

  @Override
  public List<ItemInventario> obtenerInventario(Long jugadorId) {
    return repositorioInventario.listarInventarioDeJugador(jugadorId);
  }

  @Override
  public Carta realizarMejora(List<Long> idsCartasEntregadas) throws Exception {
    if (idsCartasEntregadas.size() != CANTIDAD_CARTAS_REQUERIDAS) {
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

    Collections.shuffle(posiblesPremios);
    return posiblesPremios.get(0);
  }

  // solo obtiene la rareza de la primera carta
  private String obtenerRarezaDeLaPrimera(List<Long> ids) throws Exception {
    Carta primera = repositorioCarta.buscarPorId(ids.get(0));
    if (primera == null) {
      throw new Exception("Una de las cartas no existe");
    }
    return primera.getRareza();
  }

  // solo valida que el resto tenga la misma rareza
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
    switch (actual.toUpperCase(Locale.ROOT)) {
      case "COMUN":
        return "ESPECIAL";
      case "ESPECIAL":
        return "EPICA";
      case "EPICA":
        return "LEGENDARIA";
      default:
        throw new Exception("No se puede mejorar una carta Legendaria");
    }
  }
}
