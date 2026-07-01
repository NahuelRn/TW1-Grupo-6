package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioLoginTest {

  private ServicioLogin servicioLogin;
  private RepositorioUsuario repositorioUsuarioMock;
  private RepositorioCarta repositorioCartaMock;
  private RepositorioInventario repositorioInventarioMock;

  @BeforeEach
  public void init() {
    this.repositorioUsuarioMock = mock(RepositorioUsuario.class);
    this.repositorioCartaMock = mock(RepositorioCarta.class);
    this.repositorioInventarioMock = mock(RepositorioInventario.class);
    this.servicioLogin =
      new ServicioLoginImpl(
        this.repositorioUsuarioMock,
        this.repositorioCartaMock,
        this.repositorioInventarioMock
      );
  }

  @Test
  public void consultarUsuarioDeberiaLlamarAlRepositorio() {
    String email = "test@test.com";
    String password = "password";
    Usuario usuarioEsperado = new Usuario();
    when(this.repositorioUsuarioMock.buscarUsuario(email, password)).thenReturn(usuarioEsperado);

    Usuario usuarioObtenido = this.servicioLogin.consultarUsuario(email, password);

    assertThat(usuarioObtenido, equalTo(usuarioEsperado));
    verify(this.repositorioUsuarioMock, times(1)).buscarUsuario(email, password);
  }

  @Test
  public void registrarUsuarioSiNoExisteDeberiaGuardarlo() throws UsuarioExistente {
    Usuario usuario = new Usuario();
    usuario.setEmail("nuevo@test.com");
    usuario.setPassword("123");
    when(this.repositorioUsuarioMock.buscarUsuario(usuario.getEmail(), usuario.getPassword()))
      .thenReturn(null);
    when(this.repositorioCartaMock.listarTodas()).thenReturn(List.of());

    this.servicioLogin.registrar(usuario);

    verify(this.repositorioUsuarioMock, times(1)).guardar(usuario);
    // Validamos que el inventario no quede nulo aunque no reciba cartas iniciales
    assertThat(usuario.getInventario(), notNullValue());
  }

  @Test
  public void registrarUsuarioSiNoExisteDeberiaAsignarleCartas() throws UsuarioExistente {
    Usuario usuario = new Usuario();
    usuario.setEmail("nuevo@test.com");
    usuario.setPassword("123");

    Carta carta = new Carta();
    carta.setId(1L);

    when(this.repositorioUsuarioMock.buscarUsuario(usuario.getEmail(), usuario.getPassword()))
      .thenReturn(null);

    when(this.repositorioCartaMock.buscarPorNombre(anyString())).thenReturn(carta);

    this.servicioLogin.registrar(usuario);

    verify(this.repositorioUsuarioMock, times(1)).guardar(usuario);
    assertThat(usuario.getInventario(), notNullValue());
  }

  @Test
  public void registrarUsuarioSiExisteDeberiaLanzarExcepcion() {
    Usuario usuario = new Usuario();
    usuario.setEmail("existe@test.com");
    usuario.setPassword("123");
    when(this.repositorioUsuarioMock.buscarUsuario(usuario.getEmail(), usuario.getPassword()))
      .thenReturn(new Usuario());

    assertThrows(UsuarioExistente.class, () -> this.servicioLogin.registrar(usuario));
    verify(this.repositorioUsuarioMock, times(0)).guardar(usuario);
  }
}
