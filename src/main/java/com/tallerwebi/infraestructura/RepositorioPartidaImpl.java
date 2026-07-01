package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Partida;
import com.tallerwebi.dominio.RepositorioPartida;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioPartida")
public class RepositorioPartidaImpl implements RepositorioPartida {

  private SessionFactory sessionFactory;

  @Autowired
  public RepositorioPartidaImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(Partida partida) {
    sessionFactory.getCurrentSession().save(partida);
  }

  @Override
  public void actualizar(Partida partida) {
    sessionFactory.getCurrentSession().update(partida);
  }

  @Override
  public Partida buscarPartidaPorIdentificador(Long identificador) {
    return (Partida) sessionFactory
      .getCurrentSession()
      .createCriteria(Partida.class)
      .add(Restrictions.eq("id", identificador))
      .uniqueResult();
  }
}
