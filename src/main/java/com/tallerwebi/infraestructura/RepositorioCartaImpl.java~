package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Carta;
import com.tallerwebi.dominio.RepositorioCarta;
import java.util.List;
import org.hibernate.Criteria;
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

  @Override
  @SuppressWarnings("unchecked")
  public List<Carta> listarCartasFaltantes(List<Long> idsCartasPoseidas) {
    Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Carta.class);

    // Si la lista de IDs tiene algo, le decimos a Hibernate que excluya esos IDs (NOT IN)
    if (idsCartasPoseidas != null && !idsCartasPoseidas.isEmpty()) {
      criteria.add(Restrictions.not(Restrictions.in("id", idsCartasPoseidas)));
    }

    return criteria.list();
  }

  @Override
  public Carta buscarPorNombre(String nombre) {
    Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Carta.class);
    criteria.add(Restrictions.eq("nombre", nombre));
    return (Carta) criteria.uniqueResult();
  }

  @Override
  public void guardar(Carta carta) {
    sessionFactory.getCurrentSession().save(carta);
  }
}
