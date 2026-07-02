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
    // Si no tiene ninguna carta asignada, le faltan absolutamente TODAS las cartas del juego
    if (idsCartasPoseidas == null || idsCartasPoseidas.isEmpty()) {
      return sessionFactory
        .getCurrentSession()
        .createQuery("FROM Carta", Carta.class)
        .getResultList();
    }

    // Usamos HQL explícito con NOT IN, que es mucho más robusto que la API de Criteria para colecciones
    return sessionFactory
      .getCurrentSession()
      .createQuery("FROM Carta c WHERE c.id NOT IN (:ids)", Carta.class)
      .setParameterList("ids", idsCartasPoseidas)
      .getResultList();
  }

  @Override
  public Carta buscarPorNombre(String nombre) {
    Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Carta.class);
    criteria.add(Restrictions.eq("nombre", nombre));
    return (Carta) criteria.uniqueResult();
  }

  @Override
  public List<Carta> buscarPorTipoYRareza(String tipo, String rareza) {
    return sessionFactory
      .getCurrentSession()
      .createQuery("FROM Carta WHERE tipo = :tipo AND rareza = :rareza", Carta.class)
      .setParameter("tipo", tipo)
      .setParameter("rareza", rareza)
      .getResultList();
  }

  @Override
  public void guardar(Carta carta) {
    sessionFactory.getCurrentSession().save(carta);
  }
}
