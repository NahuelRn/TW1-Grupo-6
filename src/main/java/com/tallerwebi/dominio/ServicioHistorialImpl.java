package com.tallerwebi.dominio;

import java.time.LocalDate;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ServicioHistorialImpl implements ServicioHistorial {

  private final RepositorioHistorialPartida repositorioHistorialPartida;

  @Autowired
  public ServicioHistorialImpl(RepositorioHistorialPartida repositorioHistorialPartida) {
    this.repositorioHistorialPartida = repositorioHistorialPartida;
  }

  @Override
  public void guardarHistorialPartidaServicio(HistorialPartida historialPartida) {
    if (historialPartida.getUsuario() == null) {
      throw new RuntimeException("Error, el usuario no existe.");
    }

    historialPartida.setLocalDate(LocalDate.now());
    this.repositorioHistorialPartida.guardarHistorialPartidaRepositorio(historialPartida);
  }

  @Override
  public List<HistorialPartida> listarHistorialPorUsuario(Long identificadorUsuario) {
    return this.repositorioHistorialPartida.listarPorUsuario(identificadorUsuario);
  }
}
