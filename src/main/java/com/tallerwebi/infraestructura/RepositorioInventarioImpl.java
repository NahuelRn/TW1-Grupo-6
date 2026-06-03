package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.ItemInventario;
import com.tallerwebi.dominio.RepositorioInventario;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioInventarioImpl implements RepositorioInventario {

  private SessionFactory sessionFactory;

  @Autowired
  public RepositorioInventarioImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(ItemInventario item) {
    sessionFactory.getCurrentSession().save(item);
  }

  @Override
  public void actualizar(ItemInventario item) {
    sessionFactory.getCurrentSession().update(item);
  }

  @Override
  public ItemInventario buscarItemDeJugador(Long jugadorId, Long cartaId) {
    Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ItemInventario.class);

    criteria.createAlias("jugador", "j");
    criteria.createAlias("carta", "c");

    criteria.add(Restrictions.eq("j.id", jugadorId));
    criteria.add(Restrictions.eq("c.id", cartaId));

    return (ItemInventario) criteria.uniqueResult();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<ItemInventario> listarInventarioDeJugador(Long jugadorId) {
    Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ItemInventario.class);

    criteria.createAlias("jugador", "j");
    criteria.add(Restrictions.eq("j.id", jugadorId));
    criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

    return criteria.list();
  }
}
