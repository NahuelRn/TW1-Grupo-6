package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioEnemigo {
  Enemigo obtenerEnemigoAleatorioPorZona(String zona);
  List<Enemigo> buscarPorZona(String zona);
}
