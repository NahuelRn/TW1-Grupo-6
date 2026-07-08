package com.tallerwebi.dominio;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ServicioCalculoRecompensaImpl implements ServicioCalculoRecompensa {

  private final RepositorioConfiguracionJuego REPOSITORIO_CONFIGURACION_JUEGO;
  private static final Integer HP_ALTO = 80;
  private static final Integer HP_MEDIA = 50;

  @Autowired
  public ServicioCalculoRecompensaImpl(
    RepositorioConfiguracionJuego repositorioConfiguracionJuego
  ) {
    this.REPOSITORIO_CONFIGURACION_JUEGO = repositorioConfiguracionJuego;
  }

  @Override
  public RecompensaDTO obtenerRecompensa(Partida partida) {
    Integer experienciaBase =
      this.REPOSITORIO_CONFIGURACION_JUEGO.obtenerValor(ConfiguracionJuego.EXPERIENCIA_BASE);

    RecompensaDTO recompensaDTO = new RecompensaDTO();

    int hpEnemigo = obtenerHpEnemigo(partida);
    if (hpEnemigo <= 0) {
      Integer oroBase =
        this.REPOSITORIO_CONFIGURACION_JUEGO.obtenerValor(ConfiguracionJuego.ORO_BASE);
      calcularRecompensaVictoria(partida, oroBase, experienciaBase, recompensaDTO);
    } else {
      calcularRecompensaDerrota(recompensaDTO, experienciaBase);
    }

    return recompensaDTO;
  }

  private Integer obtenerHpEnemigo(Partida partida) {
    Integer hpEnemigo;
    if (partida.getHpEnemigo() != null) {
      hpEnemigo = partida.getHpEnemigo();
    } else {
      hpEnemigo = 100;
    }
    return hpEnemigo;
  }

  private void calcularRecompensaVictoria(
    Partida partida,
    Integer oroBase,
    Integer experienciaBase,
    RecompensaDTO recompensaDTO
  ) {
    Integer oro = oroBase;
    Integer experiencia = experienciaBase;

    Integer hpJugador;
    if (partida.getHpJugador() != null) {
      hpJugador = partida.getHpJugador();
    } else {
      hpJugador = 0;
    }

    if (hpJugador > this.HP_ALTO) {
      oro += 10;
      experiencia += 5;
    } else if (hpJugador > this.HP_MEDIA) {
      oro += 5;
    }

    recompensaDTO.setOro(oro);
    recompensaDTO.setExperiencia(experiencia);
  }

  private void calcularRecompensaDerrota(RecompensaDTO recompensaDTO, Integer experienciaBase) {
    recompensaDTO.setOro(0);
    recompensaDTO.setExperiencia(experienciaBase / 2);
  }
}
