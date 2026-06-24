package com.tallerwebi.dominio;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ServicioCombateImpl implements ServicioCombate {

  private RepositorioPartida repositorioPartida;
  private RepositorioCarta repositorioCarta;

  @Autowired
  public ServicioCombateImpl(
    RepositorioPartida repositorioPartida,
    RepositorioCarta repositorioCarta
  ) {
    this.repositorioPartida = repositorioPartida;
    this.repositorioCarta = repositorioCarta;
  }

  @Override
  public String jugarTurno(Partida partida, Long idCarta) {
    Carta cartaJugada = repositorioCarta.buscarPorId(idCarta);

    int daño = (cartaJugada.getDano() != null) ? cartaJugada.getDano() : 0;
    int defensa = (cartaJugada.getDefensa() != null) ? cartaJugada.getDefensa() : 0;

    partida.setHpEnemigo(partida.getHpEnemigo() - daño);

    int danoRecibido;
    if (partida.getHpEnemigo() > 0) {
      int calculo = 5 - defensa;
      danoRecibido = (calculo < 0) ? 0 : calculo;
      partida.setHpJugador(partida.getHpJugador() - danoRecibido);
    } else {
      danoRecibido = 0;
    }

    String logCombate;
    if (partida.getHpEnemigo() <= 0) {
      partida.setEnumEstadoPartida(EnumEstadoPartida.GANADOR_JUGADOR);
      logCombate =
        "¡EL INFECTADO HA SIDO DESTRUIDO! HAS GANADO. Tu golpe con [" +
        cartaJugada.getNombre() +
        "] hizo " +
        daño;
    } else if (partida.getHpJugador() <= 0) {
      partida.setEnumEstadoPartida(EnumEstadoPartida.GANADOR_ENEMIGO);
      logCombate =
        "HAS MUERTO. FIN DE LA PARTIDA. Usaste [" +
        cartaJugada.getNombre() +
        "], Daño: " +
        daño +
        ", Defensa: " +
        defensa +
        ", Recibiste: " +
        danoRecibido +
        " HP.";
    } else {
      logCombate =
        "Usaste [" +
        cartaJugada.getNombre() +
        "]. " +
        (daño > 0 ? "Hiciste " + daño + " de Daño. " : "") +
        (defensa > 0 ? "Levantaste " + defensa + " de Escudo. " : "") +
        "El Infectado te sacó " +
        danoRecibido +
        " HP.";
    }

    return logCombate;
  }

  @Override
  public Partida obtenerPartidaPorIdentificador(Long identificadorPartida) {
    return this.repositorioPartida.buscarPartidaPorIdentificador(identificadorPartida);
  }
}
