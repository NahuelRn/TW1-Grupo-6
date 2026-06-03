package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.tallerwebi.dominio.Carta;
import com.tallerwebi.dominio.ItemInventario;
import com.tallerwebi.dominio.Jugador;
import com.tallerwebi.dominio.RepositorioInventario;
import com.tallerwebi.integracion.config.HibernateTestConfig;
import java.util.List;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { HibernateTestConfig.class })
@Transactional
@Rollback
public class RepositorioInventarioTest {

  @Autowired
  private SessionFactory sessionFactory;

  private RepositorioInventario repositorioInventario;
  @BeforeEach
  public void init() {
    this.repositorioInventario = new RepositorioInventarioImpl(sessionFactory);
  }

  @Test
  public void queSePuedaGuardarYBuscarUnItemEnElInventario() {
    Jugador jugador = new Jugador();
    sessionFactory.getCurrentSession().save(jugador);

    Carta carta = new Carta();
    sessionFactory.getCurrentSession().save(carta);

    ItemInventario item = new ItemInventario();
    item.setJugador(jugador);
    item.setCarta(carta);
    item.setCantidad(2);

    // Ejecución
    repositorioInventario.guardar(item);
    ItemInventario itemBuscado = repositorioInventario.buscarItemDeJugador(
      jugador.getId(),
      carta.getId()
    );

    // Validación
    assertThat(itemBuscado, is(notNullValue()));
    assertThat(itemBuscado.getCantidad(), is(2));
  }

  @Test
  public void queSePuedaListarElInventarioCompletoDeUnJugador() {
    // Preparación
    Jugador jugador = new Jugador();
    sessionFactory.getCurrentSession().save(jugador);

    Carta cartaFuego = new Carta();
    sessionFactory.getCurrentSession().save(cartaFuego);

    Carta cartaHielo = new Carta();
    sessionFactory.getCurrentSession().save(cartaHielo);

    ItemInventario item1 = new ItemInventario();
    item1.setJugador(jugador);
    item1.setCarta(cartaFuego);

    ItemInventario item2 = new ItemInventario();
    item2.setJugador(jugador);
    item2.setCarta(cartaHielo);

    repositorioInventario.guardar(item1);
    repositorioInventario.guardar(item2);

    // Ejecución
    List<ItemInventario> inventario = repositorioInventario.listarInventarioDeJugador(
      jugador.getId()
    );

    // Validación
    assertThat(inventario, is(notNullValue()));
    assertThat(inventario, hasSize(2));
  }

  @Test
  public void queSePuedaActualizarLaCantidadDeUnItem() {
    // Preparación
    Jugador jugador = new Jugador();
    sessionFactory.getCurrentSession().save(jugador);

    Carta carta = new Carta();
    sessionFactory.getCurrentSession().save(carta);

    ItemInventario item = new ItemInventario();
    item.setJugador(jugador);
    item.setCarta(carta);
    item.setCantidad(1);
    repositorioInventario.guardar(item);

    // Ejecución
    item.sumarCantidad(1);
    repositorioInventario.actualizar(item);

    ItemInventario itemActualizado = repositorioInventario.buscarItemDeJugador(
      jugador.getId(),
      carta.getId()
    );

    // Validación
    assertThat(itemActualizado.getCantidad(), is(2));
  }
}
