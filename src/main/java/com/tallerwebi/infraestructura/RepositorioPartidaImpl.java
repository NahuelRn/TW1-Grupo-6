package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Partida;
import com.tallerwebi.dominio.RepositorioPartida;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioPartidaImpl implements RepositorioPartida {

  private SessionFactory sessionFactory;

  public RepositorioPartidaImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public Partida buscarPartidaPorIdentificador(Long identificador) {
    return (Partida) this.sessionFactory.getCurrentSession()
      .createCriteria(Partida.class)
      .add(Restrictions.eq("id", identificador))
      .uniqueResult();
  }
}
