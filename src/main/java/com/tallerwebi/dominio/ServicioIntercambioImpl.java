package com.tallerwebi.dominio;

import java.util.Collections;
import java.util.List;
//import java.util.Locale;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ServicioIntercambioImpl implements ServicioIntercambio {

  private static final int CANTIDAD_CARTAS_REQUERIDAS = 4;

  private final RepositorioCarta repositorioCarta;
  private final RepositorioInventario repositorioInventario;
  private static final String RAREZA_COMUN = "Comun";
  private static final String RAREZA_POCO_COMUN = "Poco Comun";
  private static final String RAREZA_RARA = "Rara";
  private static final String RAREZA_EXOTICA = "Exotica";
  private static final String RAREZA_LEGENDARIA = "Legendaria";

  @Autowired
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

  // En com.tallerwebi.dominio.ServicioIntercambioImpl.java

  @Override
  public Carta realizarMejora(Long jugadorId, List<Long> idsCartasEntregadas) throws Exception {

    // 1. Tus validaciones de tamaño de lista...
    if (idsCartasEntregadas == null || idsCartasEntregadas.size() != 4) {
      throw new Exception("Debes seleccionar exactamente 4 cartas.");
    }

    // 2. Tu lógica actual para buscar las cartas en repoCartaMock...

    // 3. ¡Ojo acá! Si adentro del método necesitas buscar el inventario del jugador,
    // ahora ya tenés disponible la variable 'jugadorId' para pasársela al repositorio:
    // List<ItemInventario> inventario = repositorioInventario.listarInventarioDeJugador(jugadorId);

    // 4. Tu lógica de remover cartas, buscar la nueva y retornar el premio...

    return cartaPremio;
  }

    Collections.shuffle(posiblesPremios);
    return posiblesPremios.get(0);
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
