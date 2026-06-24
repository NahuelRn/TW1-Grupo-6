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

    int dano = (cartaJugada.getDano() != null) ? cartaJugada.getDano() : 0;
    int defensa = (cartaJugada.getDefensa() != null) ? cartaJugada.getDefensa() : 0;

    partida.setHpEnemigo(partida.getHpEnemigo() - dano);

    int danoRecibido = (partida.getHpEnemigo() > 0) ? Math.max(0, 5 - defensa) : 0;

    if (partida.getHpEnemigo() > 0) {
      partida.setHpJugador(partida.getHpJugador() - danoRecibido);
    }

    String detalleStats =
      " [Estadísticas -> Daño tuyo: " +
      dano +
      " | Defensa tuya: " +
      defensa +
      " | Daño sufrido: " +
      danoRecibido +
      "]";

    // Retornos directos
    if (partida.getHpEnemigo() <= 0) {
      partida.setEnumEstadoPartida(EnumEstadoPartida.GANADOR_JUGADOR);
      return "¡EL INFECTADO HA SIDO DESTRUIDO! HAS GANADO." + detalleStats;
    }

    if (partida.getHpJugador() <= 0) {
      partida.setEnumEstadoPartida(EnumEstadoPartida.GANADOR_ENEMIGO);
      return "HAS MUERTO. FIN DE LA PARTIDA." + detalleStats;
    }

    return "Usaste [" + cartaJugada.getNombre() + "]." + detalleStats;
  }
  // @Override
  // public Partida obtenerPartidaPorIdentificador(Long identificadorPartida) {
  //   return this.repositorioPartida.buscarPartidaPorIdentificador(identificadorPartida);
  // }
}
