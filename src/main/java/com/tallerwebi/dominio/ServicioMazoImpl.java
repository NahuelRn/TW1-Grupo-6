package com.tallerwebi.dominio;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

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
    // obtenemos la lista del nexo intermedio
    List<MazoCarta> nexos = mazo.getMazoCartas();

    // REGLA 1: Exactamente 15 cartas
    if (nexos.size() != MAX_CARTAS) {
      throw new Exception("El mazo debe tener exactamente 15 cartas");
    }

    // REGLA 2: No se pueden repetir cartas
    Set<Long> idsUnicos = new HashSet<>();

    for (MazoCarta nexo : nexos) {
      Carta carta = nexo.getCarta(); // Accedemos a la carta a través del nexo

      if (!idsUnicos.add(carta.getId())) {
        throw new Exception("No puedes incluir cartas repetidas: " + carta.getNombre());
      }
    }

    repositorioMazo.guardar(mazo);
  }

  @Override
  public List<Carta> buscarCartasPorIds(List<Long> ids) {
    List<Carta> cartas = new ArrayList<>();
      for (Long id : ids) {
        Carta carta = new Carta();
        carta.setId(id);
        cartas.add(carta);
      }
      return cartas;
  }
}
