package com.tallerwebi.infraestructura;

import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Carta;
import com.tallerwebi.dominio.Mazo;
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
  public void init() {
    sessionFactoryMock = mock(SessionFactory.class);
    sessionMock = mock(Session.class);

    when(sessionFactoryMock.getCurrentSession()).thenReturn(sessionMock);

    repositorioMazo = new RepositorioMazoImpl(sessionFactoryMock);
  }

  @Test
  public void queSePuedaGuardarUnMazoConCartas() {
    // Given
    Mazo mazo = new Mazo();

    Carta carta = new Carta();
    carta.setNombre("Hechizo de Fuego");

    mazo.getCartas().add(carta);

    // When
    repositorioMazo.guardar(mazo);

    // Then
    verify(sessionMock, times(1)).saveOrUpdate(mazo);
  }
}
