package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Mazo;
import com.tallerwebi.dominio.RepositorioMazo;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioMazo")
public class RepositorioMazoImpl implements RepositorioMazo {

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioMazoImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(Mazo mazo) {
    // Guarda/actualiza el mazo en la base de datos
    sessionFactory.getCurrentSession().saveOrUpdate(mazo);
  }
}
