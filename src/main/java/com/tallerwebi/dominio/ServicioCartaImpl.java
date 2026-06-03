package com.tallerwebi.dominio;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("servicioCarta")
@Transactional
public class ServicioCartaImpl implements ServicioCarta {

  private final RepositorioCarta repositorioCarta;
  private final RepositorioInventario repositorioInventario; // 1. Agregamos el repositorio acá

  @Autowired
  public ServicioCartaImpl(
    RepositorioCarta repositorioCarta,
    RepositorioInventario repositorioInventario
  ) {
    // 2. Lo inyectamos en el constructor
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
    // 3. Usamos la variable con MINÚSCULA para que use la instancia, no la clase estática
    return repositorioInventario.listarInventarioDeJugador(jugadorId);
  }
}
