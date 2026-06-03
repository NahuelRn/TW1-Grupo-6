package com.tallerwebi.config;

import com.tallerwebi.dominio.Carta;
import com.tallerwebi.dominio.ItemInventario;
import com.tallerwebi.dominio.Jugador;
import com.tallerwebi.dominio.RepositorioCarta;
import com.tallerwebi.dominio.RepositorioInventario;
import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements ApplicationListener<ContextRefreshedEvent> {

  @Autowired
  private RepositorioUsuario repositorioUsuario;

  @Autowired
  private RepositorioCarta repositorioCarta;

  @Autowired
  private RepositorioInventario repositorioInventario;

  @Override
  @Transactional
  public void onApplicationEvent(ContextRefreshedEvent event) {
    if (repositorioUsuario.buscarPorEmail("admin@admin.com") == null) {
      Jugador jugador = new Jugador();

      Usuario admin = new Usuario();
      admin.setEmail("admin@admin.com");
      admin.setPassword("1234");
      admin.setRol("ADMIN");
      admin.setActivo(true);
      jugador.setUsuario(admin);
      admin.setJugador(jugador);

      repositorioUsuario.guardar(admin);

      List<Carta> todasLasCartas = repositorioCarta.listarTodas();
      for (Carta carta : todasLasCartas) {
        if (carta.getImagen() != null) {
          ItemInventario item = new ItemInventario();
          item.setCarta(carta);
          item.setJugador(jugador);
          item.setCantidad(1); // Le damos el x10
          repositorioInventario.guardar(item);
        }
      }
    }
  }
}
