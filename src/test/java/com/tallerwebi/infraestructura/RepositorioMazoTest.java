package com.tallerwebi.infraestructura;

import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Carta;
import com.tallerwebi.dominio.Mazo;
import com.tallerwebi.dominio.MazoCarta;
import com.tallerwebi.dominio.RepositorioMazo;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RepositorioMazoTest {

  private RepositorioMazo repositorioMazo;
  private SessionFactory sessionFactoryMock;
  private Session sessionMock;

  @BeforeEach
  public void init() throws Exception {
    sessionFactoryMock = mock(SessionFactory.class);
    sessionMock = mock(Session.class);

    when(sessionFactoryMock.getCurrentSession()).thenReturn(sessionMock);

    repositorioMazo = new RepositorioMazoImpl(sessionFactoryMock);
  }

  @Test
  public void queSePuedaGuardarUnMazoConCartas() {
    Mazo mazo = new Mazo();
    Carta carta = new Carta();
    carta.setNombre("Hechizo de Fuego");

    MazoCarta nexo = new MazoCarta();

    nexo.setMazo(mazo);
    nexo.setCarta(carta);

    mazo.getMazoCartas().add(nexo);

    repositorioMazo.guardar(mazo);
    verify(sessionMock, times(1)).saveOrUpdate(mazo);
  }
}
