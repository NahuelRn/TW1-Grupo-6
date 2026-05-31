package com.tallerwebi.config;

import com.tallerwebi.dominio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DataInitializer implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    // Inyectamos esto para cargar las cartas
    @Autowired
    private RepositorioCarta repositorioCarta;

    @Autowired
    private RepositorioInventario repositorioInventario;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (repositorioUsuario.buscarPorEmail("admin@admin.com") == null) {
            // 1. Creamos el admin
            Usuario admin = new Usuario();
            admin.setEmail("admin@admin.com");
            admin.setPassword("1234");
            admin.setRol("ADMIN");
            admin.setActivo(true);
            repositorioUsuario.guardar(admin);

            // Asegúrate de que el admin tenga su jugador asociado si tu lógica lo requiere
            // (Si tu sistema crea el Jugador al registrar, quizás debas llamar al servicio)

            // 2. Le damos todas las cartas existentes (para testeo)
            List<Carta> todasLasCartas = repositorioCarta.listarTodas();
            for (Carta carta : todasLasCartas) {
                ItemInventario item = new ItemInventario();
                item.setCarta(carta);
                // Asumiendo que tenes la relacion directa usuario-jugador o acceso al objeto jugador
                item.setJugador(admin.getJugador());
                item.setCantidad(10); // Le damos 10 de cada una para que sobre para probar contratos
                repositorioInventario.guardar(item);
            }
        }
    }
}