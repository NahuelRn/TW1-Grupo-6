package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Enemigo;
import com.tallerwebi.dominio.RepositorioEnemigo;
import org.hibernate.SessionFactory;
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
  public Enemigo obtenerEnemigoAleatorioPorZona(String zona) {
    // Buscamos un enemigo de esa zona en la base de datos
    return (Enemigo) sessionFactory
      .getCurrentSession()
      .createQuery("FROM Enemigo WHERE zona = :zona ORDER BY rand()")
      .setParameter("zona", zona)
      .setMaxResults(1)
      .uniqueResult();
  }
}
