package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import java.util.List;
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

    // 1. Crear el Jugador asociado
    Jugador jugador = new Jugador();
    jugador.setUsuario(usuario);
    usuario.setJugador(jugador);

    // 2. Guardar el usuario (jugador se persiste por CascadeType.ALL)
    repositorioUsuario.guardar(usuario);

    // 3. Asignar todas las cartas al inventario del nuevo jugador
    List<Carta> todasLasCartas = repositorioCarta.listarTodas();
    for (Carta carta : todasLasCartas) {
      ItemInventario item = new ItemInventario();
      item.setCarta(carta);
      item.setJugador(jugador);
      item.setCantidad(1);
      repositorioInventario.guardar(item);
    }
  }
}
