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
    // Given
    Mazo mazo = new Mazo();

    Carta carta = new Carta();
    carta.setNombre("Hechizo de Fuego");

    // Creamos la entidad intermedia (el nexo)
    MazoCarta nexo = new MazoCarta();

    // Seteamos las relaciones en el nexo
    nexo.setMazo(mazo);
    nexo.setCarta(carta);

    // Agregamos el nexo a la lista del mazo (en lugar de la carta directamente)
    mazo.getMazoCartas().add(nexo);

    // When
    repositorioMazo.guardar(mazo);

    // Then
    // Verificamos que se llame a saveOrUpdate con el objeto mazo
    verify(sessionMock, times(1)).saveOrUpdate(mazo);
  }
}
