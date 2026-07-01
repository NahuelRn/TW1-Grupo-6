package com.tallerwebi.dominio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("servicioPartida")
@Transactional
public class ServicioPartidaImpl implements ServicioPartida {

  @Value("${partida.cartas.iniciales:5}")
  private int cartasIniciales;

  private RepositorioEnemigo repositorioEnemigo;
  private RepositorioPartida repositorioPartida;

  @Autowired
  public ServicioPartidaImpl(
    RepositorioEnemigo repositorioEnemigo,
    RepositorioPartida repositorioPartida
  ) {
    this.repositorioEnemigo = repositorioEnemigo;
    this.repositorioPartida = repositorioPartida;
  }

  @Override
  public Partida iniciarPartida(Usuario usuario, String zona) {
    Partida nuevaPartida = new Partida();
    nuevaPartida.setUsuario(usuario);

    Enemigo enemigoSeleccionado = determinarEnemigoParaZona(zona);

    nuevaPartida.setEnemigo(enemigoSeleccionado);
    nuevaPartida.setHpEnemigo(enemigoSeleccionado != null ? enemigoSeleccionado.getHpBase() : 100);
    nuevaPartida.setHpJugador(100);
    nuevaPartida.setEnumEstadoPartida(EnumEstadoPartida.ACTIVA);

    List<Carta> mazoCompleto = new ArrayList<>();
    if (usuario.getMazoActivo() != null && usuario.getMazoActivo().getCartas() != null) {
      mazoCompleto.addAll(usuario.getMazoActivo().getCartas());
    }

    // ÚNICO shuffle de todo el flujo: acá se decide la mano y el mazo de robo.
    Collections.shuffle(mazoCompleto);

    int cartasARobar = Math.min(cartasIniciales, mazoCompleto.size());
    List<Carta> manoInicial = new ArrayList<>(mazoCompleto.subList(0, cartasARobar));
    List<Carta> mazoRestante = new ArrayList<>(
      mazoCompleto.subList(cartasARobar, mazoCompleto.size())
    );

    nuevaPartida.setManoJugador(manoInicial);
    nuevaPartida.setMazoRestante(mazoRestante);

    repositorioPartida.guardar(nuevaPartida);

    return nuevaPartida;
  }

  @Override
  public void gestionarRoboDeCarta(Long idCartaJugada, List<Long> idsMano, List<Long> idsMazoRobo) {
    if (idsMano != null) {
      idsMano.remove(idCartaJugada);
      if (idsMazoRobo != null && !idsMazoRobo.isEmpty() && idsMano.size() < 5) {
        Long cartaRobada = idsMazoRobo.remove(0);
        idsMano.add(cartaRobada);
      }
    }
  }

  @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
  private Enemigo determinarEnemigoParaZona(String zona) {
    List<Enemigo> enemigosZona = repositorioEnemigo.buscarPorZona(zona);

    if (enemigosZona == null || enemigosZona.isEmpty()) {
      return repositorioEnemigo.obtenerEnemigoAleatorioPorZona(zona);
    }

    int sumaProbabilidades = enemigosZona
      .stream()
      .mapToInt(e -> e.getProbabilidad() != null ? e.getProbabilidad() : 10)
      .sum();

    int roll = new java.util.Random().nextInt(sumaProbabilidades) + 1;
    int acumulador = 0;

    for (Enemigo e : enemigosZona) {
      acumulador += (e.getProbabilidad() != null ? e.getProbabilidad() : 10);
      if (roll <= acumulador) {
        return e;
      }
    }
    return enemigosZona.get(0);
  }
}
