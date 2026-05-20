package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Partida;
import com.tallerwebi.dominio.ServicioCombate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ControladorCombateTest {

    ServicioCombate servicioCombate = new ServicioCombate();
    Partida partida = new Partida(100, 100, 1);

    @Test
    public void validarTurnoDelEnemigo() {

        assertThrows(RuntimeException.class,() -> this.servicioCombate.jugarCarta(1, 2));
    }

//    @Test
//    public void debeFallarSiNoTieneLasCartasEnMano() {
//
//        assertThrows(RuntimeException.class, () -> this.servicioCombate.jugarCarta(1, 2));
//        assertThrows(RuntimeException.class, () -> this.servicioCombate.jugarCarta(1, 2));
//    }

    @Test
    public void calcularEfectoCartaHaciaEnemigo() {

        Integer resultadoDanioCartaHaciaEnemigo = this.servicioCombate.jugarCarta(1, 1);

        assertEquals(55, resultadoDanioCartaHaciaEnemigo);
    }
}