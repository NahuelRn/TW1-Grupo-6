package com.tallerwebi.dominio;

import java.time.LocalDate;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ServicioCombateImpl implements ServicioCombate {

  private ServicioCalculoRecompensa servicioCalculoRecompensa;
  private final ServicioUsuario servicioUsuario;
  private RepositorioHistorialPartida repositorioHistorialPartida;
  private RepositorioPartida repositorioPartida;
  private RepositorioCarta repositorioCarta;

  @Autowired
  public ServicioCombateImpl(
    RepositorioPartida repositorioPartida,
    RepositorioCarta repositorioCarta,
    RepositorioHistorialPartida repositorioHistorialPartida,
    ServicioUsuario servicioUsuario,
    ServicioCalculoRecompensa servicioCalculoRecompensa
  ) {
    this.repositorioPartida = repositorioPartida;
    this.repositorioCarta = repositorioCarta;
    this.repositorioHistorialPartida = repositorioHistorialPartida;
    this.servicioUsuario = servicioUsuario;
    this.servicioCalculoRecompensa = servicioCalculoRecompensa;
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

    if (partida.getHpEnemigo() <= 0) {
      partida.setEnumEstadoPartida(EnumEstadoPartida.GANADOR_JUGADOR);

      this.servicioUsuario.aplicarRecompensa(partida.getUsuario(), partida);

      RecompensaDTO recompensaDTO = this.servicioCalculoRecompensa.obtenerRecompensa(partida);

      HistorialPartida historialPartida = new HistorialPartida(); // L
      historialPartida.setUsuario(partida.getUsuario());
      historialPartida.setResultado("GANADOR_JUGADOR");
      historialPartida.setOroGanado(recompensaDTO.getOro());
      historialPartida.setExperienciaGanada(recompensaDTO.getExperiencia());
      historialPartida.setFecha(LocalDate.now());

      this.repositorioHistorialPartida.guardarHistorialPartidaRepositorio(historialPartida);

      return "¡EL INFECTADO HA SIDO DESTRUIDO! HAS GANADO." + detalleStats;
    }

    if (partida.getHpJugador() <= 0) {
      partida.setEnumEstadoPartida(EnumEstadoPartida.GANADOR_ENEMIGO);

      HistorialPartida historialPartida = new HistorialPartida(); // L
      historialPartida.setUsuario(partida.getUsuario());
      historialPartida.setResultado("GANADOR_ENEMIGO");
      historialPartida.setOroGanado(0);
      historialPartida.setExperienciaGanada(10);
      historialPartida.setFecha(LocalDate.now());

      this.repositorioHistorialPartida.guardarHistorialPartidaRepositorio(historialPartida);

      return "HAS MUERTO. FIN DE LA PARTIDA." + detalleStats;
    }

    return "Usaste [" + cartaJugada.getNombre() + "]." + detalleStats;
  }
}
//    @Override
//    public Partida obtenerPartidaPorIdentificador(Long identificadorPartida) {
//        return this.repositorioPartida.buscarPartidaPorIdentificador(identificadorPartida);
//    }
