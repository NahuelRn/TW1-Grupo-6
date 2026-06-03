package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Carta;
import com.tallerwebi.dominio.RepositorioCarta;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioCarta")
public class RepositorioCartaImpl implements RepositorioCarta {

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioCartaImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public List<Carta> listarTodas() {
    return sessionFactory.getCurrentSession().createCriteria(Carta.class).list();
  }

  @Override
  public Carta buscarPorId(Long id) {
    return (Carta) sessionFactory
      .getCurrentSession()
      .createCriteria(Carta.class)
      .add(Restrictions.eq("id", id))
      .uniqueResult();
  }

  @Override
  public List<Carta> buscarPorRareza(String rareza) {
    return sessionFactory
      .getCurrentSession()
      .createCriteria(Carta.class)
      .add(Restrictions.eq("rareza", rareza))
      .list();
  }
}
