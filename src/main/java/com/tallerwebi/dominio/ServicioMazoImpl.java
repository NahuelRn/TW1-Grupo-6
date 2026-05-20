package com.tallerwebi.dominio;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional // Asegura que si algo falla, no se guarde nada a medias
public class ServicioMazoImpl implements ServicioMazo {

  private static final int MAX_CARTAS = 15;
  private final RepositorioMazo repositorioMazo;

  public ServicioMazoImpl(RepositorioMazo repositorioMazo) {
    this.repositorioMazo = repositorioMazo;
  }

  @Override
  public void validarYGuardarMazo(Mazo mazo) throws Exception {
    List<Carta> cartas = mazo.getCartas();

    // REGLA 1: Exactamente 15 cartas
    if (cartas.size() != MAX_CARTAS) {
      throw new Exception("El mazo debe tener exactamente 15 cartas");
    }

    // REGLA 2: No se pueden repetir cartas
    for (int i = 0; i < cartas.size(); i++) {
      for (int j = i + 1; j < cartas.size(); j++) {
        if (cartas.get(i).getId().equals(cartas.get(j).getId())) {
          throw new Exception("No puedes incluir cartas repetidas: " + cartas.get(i).getNombre());
        }
      }
    }
    repositorioMazo.guardar(mazo);
  }
}
