package com.tallerwebi.dominio;

import java.util.List;

public interface ServicioCarta {
  List<Carta> obtenerTodas();
  Carta obtenerCartaPorId(Long id);
}
