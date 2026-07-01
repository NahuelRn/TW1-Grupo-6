package com.tallerwebi.dominio;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ServicioCombateImpl implements ServicioCombate {

  private static final int VALOR_NULO = 0;
  private static final int DIVISOR_MITAD = 2;
  private static final String TIPO_ATAQUE = "ATAQUE";
  private static final String TIPO_DEFENSA = "DEFENSA";

  @Value("${partida.hp.jugador.maximo:100}")
  private int hpMaximo = 100;

  @Value("${enemigo.dano.min.default:3}")
  private int dmgMinDefault = 3;

  @Value("${enemigo.dano.max.default:8}")
  private int dmgMaxDefault = 8;

  private RepositorioPartida repositorioPartida;
  private RepositorioCarta repositorioCarta;
  private Random random = new Random();

  @Autowired
  public ServicioCombateImpl(
    RepositorioPartida repositorioPartida,
    RepositorioCarta repositorioCarta
  ) {
    this.repositorioPartida = repositorioPartida;
    this.repositorioCarta = repositorioCarta;
  }

  @Override
  public Map<String, String> obtenerConfiguracionZona(String zona) {
    Map<String, String> config = new HashMap<>();
    String zonaSegura = (zona != null) ? zona : "bosque";
    boolean esBosque = "bosque".equalsIgnoreCase(zonaSegura);

    config.put("nombreZona", zonaSegura.toUpperCase(Locale.ROOT));
    config.put("archivoFondo", esBosque ? "Bosque.jpg" : "Cueva.jpg");
    return config;
  }

  @Override
  @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
  public String jugarTurno(Long idPartida, Long idCarta) {
    Partida partida = repositorioPartida.buscarPartidaPorIdentificador(idPartida);
    Carta carta = repositorioCarta.buscarPorId(idCarta);
    Enemigo enemigo = partida.getEnemigo();
    String nombreEnemigo = (enemigo != null) ? enemigo.getNombre() : "Enemigo";

    String accionJugador;

    int defensaGenerada = TIPO_DEFENSA.equalsIgnoreCase(carta.getTipo())
      ? calcularValorAleatorio(carta.getDefensaMin(), carta.getDefensaMax())
      : VALOR_NULO;

    if (TIPO_ATAQUE.equalsIgnoreCase(carta.getTipo())) {
      int danoInfligido = calcularValorAleatorio(carta.getDanoMin(), carta.getDanoMax());
      partida.setHpEnemigo(partida.getHpEnemigo() - danoInfligido);
      accionJugador =
        "Atacas con [" + carta.getNombre() + "] haciendo " + danoInfligido + " de daño.";
    } else if (TIPO_DEFENSA.equalsIgnoreCase(carta.getTipo())) {
      accionJugador =
        "Te cubrís con [" + carta.getNombre() + "], logrando " + defensaGenerada + " de escudo.";
    } else {
      int curacion = calcularValorAleatorio(carta.getDefensaMin(), carta.getDefensaMax());
      partida.setHpJugador(Math.min(hpMaximo, partida.getHpJugador() + curacion));
      accionJugador = "✨ Usas [" + carta.getNombre() + "] y recuperas " + curacion + " HP.";
    }

    if (partida.getHpEnemigo() <= VALOR_NULO) {
      partida.setEnumEstadoPartida(EnumEstadoPartida.GANADOR_JUGADOR);
      repositorioPartida.actualizar(partida);
      return accionJugador + " ¡" + nombreEnemigo.toUpperCase(Locale.ROOT) + " HA SIDO DERROTADO!";
    }

    int danoRecibido = Math.max(VALOR_NULO, calcularAtaqueEnemigo(enemigo) - defensaGenerada);
    partida.setHpJugador(partida.getHpJugador() - danoRecibido);

    if (partida.getHpJugador() <= VALOR_NULO) {
      partida.setEnumEstadoPartida(EnumEstadoPartida.GANADOR_ENEMIGO);
      repositorioPartida.actualizar(partida);
      return (
        accionJugador +
        " " +
        nombreEnemigo +
        " te mató con " +
        danoRecibido +
        " de daño. FIN DE LA PARTIDA."
      );
    }

    repositorioPartida.actualizar(partida);

    if (danoRecibido == VALOR_NULO && TIPO_DEFENSA.equalsIgnoreCase(carta.getTipo())) {
      return accionJugador + " ¡Bloqueo perfecto! Evitaste el ataque de " + nombreEnemigo + ".";
    }

    return (
      accionJugador + " " + nombreEnemigo + " contraataca y recibes " + danoRecibido + " de daño."
    );
  }

  private int calcularValorAleatorio(Integer min, Integer max) {
    int statMin = (min != null) ? min : VALOR_NULO;
    int statMax = (max != null) ? max : VALOR_NULO;

    if (statMax <= VALOR_NULO && statMin <= VALOR_NULO) return VALOR_NULO;
    if (statMin == statMax) return statMax;

    return random.nextInt((statMax - statMin) + 1) + statMin;
  }

  private int calcularAtaqueEnemigo(Enemigo enemigo) {
    int minDmg = (enemigo != null && enemigo.getDanoMin() != null)
      ? enemigo.getDanoMin()
      : dmgMinDefault;
    int maxDmg = (enemigo != null && enemigo.getDanoMax() != null)
      ? enemigo.getDanoMax()
      : dmgMaxDefault;

    if (minDmg == maxDmg) return maxDmg;
    return random.nextInt((maxDmg - minDmg) + 1) + minDmg;
  }

  @Override
  public Partida obtenerPartidaPorIdentificador(Long identificadorPartida) {
    return repositorioPartida.buscarPartidaPorIdentificador(identificadorPartida);
  }
}
