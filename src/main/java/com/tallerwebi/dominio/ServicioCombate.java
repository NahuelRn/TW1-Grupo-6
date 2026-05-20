package com.tallerwebi.dominio;

import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ServicioCombate {

    public Integer jugarCarta(Integer identificadorCarta, Integer identificadorPartida) {
        validarTurno(identificadorPartida);
        validarCartaEnMano(identificadorCarta, identificadorPartida);

        Integer danioCartaHaciaEnemigo = calcularEfectoCarta(identificadorCarta);

        Partida partida = obtenerPartidaPorIdentificador(identificadorPartida);
        partida.setHpEnemigo(partida.getHpEnemigo() - danioCartaHaciaEnemigo);
        actualizarEstadoPartida(partida);

        return danioCartaHaciaEnemigo;
    }

    private void validarTurno(Integer identificadorPartida) {
        Partida partida = obtenerPartidaPorIdentificador(identificadorPartida);

        if (partida.getTurno() == null) {
            throw new RuntimeException("Error, turno no definido.");
        }

        if (partida.getTurno() != 1) {
            throw new RuntimeException("Turno del enemigo.");
        }
    }

    private void validarCartaEnMano(Integer identificadorCarta, Integer identificadorPartida) {
        Partida partida = obtenerPartidaPorIdentificador(identificadorPartida);

        if (partida.getCartasEnManoJugador() == null || partida.getCartasEnManoJugador().size() == 0) {
            throw new RuntimeException("Error, el jugador no tiene cartas en mano.");
        }

        Integer contadorCartasRepetidas = 0;
        for (Integer buscarCartaIdentificador : partida.getCartasEnManoJugador()) {
            if (buscarCartaIdentificador.equals(identificadorCarta) == true) {
                contadorCartasRepetidas++;
            }
        }

        if (contadorCartasRepetidas > 3) {
            throw new RuntimeException("Error, el máximo de cartas repetidas es de 3.");
        }
    }

    private Integer calcularEfectoCarta(Integer identificadorCarta) {
//      Formula -> danio = valor_base_carta * multiplicador + factor_suerte;}

//      Ejemplo:
        Integer valorBaseCarta = 10;
        Integer multiplicador = 5;
        Integer factorSuerte = 5;

        return valorBaseCarta * multiplicador + factorSuerte;
    }

    public Partida obtenerPartidaPorIdentificador(Integer identificadorPartida) {
//      Aca va la logica del repositorio...

        if (identificadorPartida == 1) {

            Partida partida = new Partida(100, 100, 1);
            ArrayList<Integer> cartas = new ArrayList<>();

            cartas.add(1);
            cartas.add(2);
            cartas.add(3);

            partida.setCartasEnManoJugador(cartas);

            return partida;
        }

        return new Partida(100, 100, 2);
    }

    private void actualizarEstadoPartida(Partida partida) {
        if (partida.getTurno() == 1) {
            partida.setTurno(2);
        }

        if (partida.getHpEnemigo() <= 0) {
            partida.setEstado("Ganador jugador.");
        } else if (partida.getHpJugador() <= 0) {
            partida.setEstado("Ganador enemigo.");
        }
    }
}