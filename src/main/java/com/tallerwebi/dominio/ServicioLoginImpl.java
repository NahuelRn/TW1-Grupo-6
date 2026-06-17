package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioLogin")
@Transactional
public class ServicioLoginImpl implements ServicioLogin {

  private final RepositorioUsuario repositorioUsuario;
  private final RepositorioCarta repositorioCarta;
  private final RepositorioInventario repositorioInventario;

  @Autowired
  public ServicioLoginImpl(
    RepositorioUsuario repositorioUsuario,
    RepositorioCarta repositorioCarta,
    RepositorioInventario repositorioInventario
  ) {
    this.repositorioUsuario = repositorioUsuario;
    this.repositorioCarta = repositorioCarta;
    this.repositorioInventario = repositorioInventario;
  }

  @Override
  public Usuario consultarUsuario(String email, String password) {
    return repositorioUsuario.buscarUsuario(email, password);
  }

  @Override
  public void registrar(Usuario usuario) throws UsuarioExistente {
    Usuario usuarioEncontrado = repositorioUsuario.buscarUsuario(
      usuario.getEmail(),
      usuario.getPassword()
    );
    if (usuarioEncontrado != null) {
      throw new UsuarioExistente();
    }

    Jugador jugador = new Jugador();
    jugador.setUsuario(usuario);
    usuario.setJugador(jugador);

    // 1. Guardamos al usuario y al jugador en la BD
    repositorioUsuario.guardar(usuario);

    // 2. Definimos las 15 cartas exactas del Mazo Inicial (las que tienen arte en data.sql)
    String[] mazoInicial = {
      "Patada Voladora",
      "Tajo Basico",
      "Puntazo",
      "Golpe Pesado",
      "Tajo Cruzado",
      "Cuchillo Arrojadizo",
      "Escudo de Madera",
      "Bloqueo Perfecto",
      "Postura Defensiva",
      "Anticipacion",
      "Sacrificio de Sangre",
      "Muro de Piedra",
      "Pocion Curativa",
      "Preparacion",
      "Afilador de Armas",
    };

    for (String nombreCarta : mazoInicial) {
      Carta cartaFisica = repositorioCarta.buscarPorNombre(nombreCarta);

      if (cartaFisica != null) {
        ItemInventario item = new ItemInventario();
        item.setCarta(cartaFisica);
        item.setJugador(jugador);
        item.setCantidad(5);
        repositorioInventario.guardar(item);
      }
    }
  }
}
