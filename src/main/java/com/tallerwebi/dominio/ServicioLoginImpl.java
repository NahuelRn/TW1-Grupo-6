package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioLogin")
@Transactional
public class ServicioLoginImpl implements ServicioLogin {

  private final RepositorioUsuario repositorioUsuario;
  private final RepositorioCarta repositorioCarta;
  private final RepositorioInventario repositorioInventario;
  private final RepositorioMazo repositorioMazo;

  @Autowired
  public ServicioLoginImpl(
    RepositorioUsuario repositorioUsuario,
    RepositorioCarta repositorioCarta,
    RepositorioInventario repositorioInventario,
    RepositorioMazo repositorioMazo
  ) {
    this.repositorioUsuario = repositorioUsuario;
    this.repositorioCarta = repositorioCarta;
    this.repositorioInventario = repositorioInventario;
    this.repositorioMazo = repositorioMazo;
  }

  @Override
  public Usuario consultarUsuario(String email, String password) {
    return repositorioUsuario.buscarUsuario(email, password);
  }

  @Override
  public void registrar(Usuario usuario) throws UsuarioExistente {
    Usuario usuarioEncontrado = repositorioUsuario.buscarPorEmail(usuario.getEmail());

    if (usuarioEncontrado != null) {
      throw new UsuarioExistente();
    }

    // ===========================
    // Crear jugador
    // ===========================

    Jugador jugador = new Jugador();
    jugador.setUsuario(usuario);
    usuario.setJugador(jugador);

    repositorioUsuario.guardar(usuario);

    // ===========================
    // Crear mazo inicial
    // ===========================

    List<Carta> seleccionRandom = new ArrayList<>();

    // ATAQUE
    seleccionRandom.addAll(obtenerCartasRandom("ATAQUE", "Comun", 3));
    seleccionRandom.addAll(obtenerCartasRandom("ATAQUE", "Poco Comun", 1));
    seleccionRandom.addAll(obtenerCartasRandom("ATAQUE", "Rara", 1));

    // DEFENSA
    seleccionRandom.addAll(obtenerCartasRandom("DEFENSA", "Comun", 1));
    seleccionRandom.addAll(obtenerCartasRandom("DEFENSA", "Poco Comun", 3));
    seleccionRandom.addAll(obtenerCartasRandom("DEFENSA", "Rara", 1));

    // HECHIZO
    seleccionRandom.addAll(obtenerCartasRandom("HECHIZO", "Comun", 2));
    seleccionRandom.addAll(obtenerCartasRandom("HECHIZO", "Poco Comun", 2));
    seleccionRandom.addAll(obtenerCartasRandom("HECHIZO", "Rara", 1));

    Mazo mazoStarter = new Mazo();
    mazoStarter.setCartas(seleccionRandom);

    repositorioMazo.guardar(mazoStarter);

    usuario.setMazoActivo(mazoStarter);

    repositorioUsuario.modificar(usuario);

    // ===========================
    // Crear inventario
    // ===========================

    for (Carta carta : seleccionRandom) {
      ItemInventario item = new ItemInventario();

      item.setJugador(jugador);
      item.setCarta(carta);
      item.setCantidad(5);

      repositorioInventario.guardar(item);
    }
  }

  private List<Carta> obtenerCartasRandom(String tipo, String rareza, int cantidad) {
    List<Carta> disponibles = repositorioCarta.buscarPorTipoYRareza(tipo, rareza);

    if (disponibles == null || disponibles.isEmpty()) {
      return new ArrayList<>();
    }

    Collections.shuffle(disponibles);

    return disponibles.stream().limit(cantidad).collect(Collectors.toList());
  }
}
