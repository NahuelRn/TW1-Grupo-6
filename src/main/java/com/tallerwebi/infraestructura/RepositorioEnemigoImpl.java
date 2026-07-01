package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Enemigo;
import com.tallerwebi.dominio.RepositorioEnemigo;
import java.util.List;
import java.util.Random;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioEnemigo")
public class RepositorioEnemigoImpl implements RepositorioEnemigo {

  private SessionFactory sessionFactory;

  @Autowired
  public RepositorioEnemigoImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Enemigo obtenerEnemigoAleatorioPorZona(String zona) {
    List<Enemigo> enemigos = sessionFactory
      .getCurrentSession()
      .createCriteria(Enemigo.class)
      .add(Restrictions.eq("zona", zona))
      .list();

    if (enemigos == null || enemigos.isEmpty()) {
      return null;
    }
    Random random = new Random();
    return enemigos.get(random.nextInt(enemigos.size()));
  }

  @Override
  public List<Enemigo> buscarPorZona(String zona) {
    return sessionFactory
      .getCurrentSession()
      .createQuery("FROM Enemigo WHERE zona = :zona", Enemigo.class)
      .setParameter("zona", zona)
      .getResultList();
  }
}
