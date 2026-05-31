package com.tallerwebi.dominio;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioCarta")
@Transactional
public class ServicioCartaImpl implements ServicioCarta {

  private final RepositorioCarta repositorioCarta;

  @Autowired
  public ServicioCartaImpl(RepositorioCarta repositorioCarta) {
    this.repositorioCarta = repositorioCarta;
  }

  @Override
  public List<Carta> obtenerTodas() {
    return repositorioCarta.listarTodas();
  }

  @Override
  public Carta obtenerCartaPorId(Long id) {
    return repositorioCarta.buscarPorId(id);
  }
}
