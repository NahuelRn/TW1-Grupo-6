package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.HistorialPartida;
import com.tallerwebi.dominio.RepositorioHistorialPartida;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioHistorialPartidaImpl implements RepositorioHistorialPartida {

  private SessionFactory sessionFactory;

  @Autowired
  public RepositorioHistorialPartidaImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardarHistorialPartidaRepositorio(HistorialPartida historialPartida) {
    this.sessionFactory.getCurrentSession().save(historialPartida);
  }

  @Override
  public List<HistorialPartida> listarPorUsuario(Long identificadorUsuario) {
    return this.sessionFactory.getCurrentSession()
      .createCriteria(HistorialPartida.class)
      .add(Restrictions.eq("usuario.id", identificadorUsuario))
      .list();
  }
}
