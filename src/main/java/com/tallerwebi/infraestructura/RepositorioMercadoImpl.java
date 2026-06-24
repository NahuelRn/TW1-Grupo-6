package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Carta;
import com.tallerwebi.dominio.PropuestaIntercambio;
import com.tallerwebi.dominio.RepositorioMercado;
import com.tallerwebi.dominio.Usuario;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioMercado")
@SuppressWarnings("unchecked")
public class RepositorioMercadoImpl implements RepositorioMercado {

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioMercadoImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(PropuestaIntercambio propuesta) {
    sessionFactory.getCurrentSession().saveOrUpdate(propuesta);
  }

  @Override
  public PropuestaIntercambio buscarPorId(Long id) {
    return (PropuestaIntercambio) sessionFactory
      .getCurrentSession()
      .get(PropuestaIntercambio.class, id);
  }

  @Override
  public void eliminar(PropuestaIntercambio propuesta) {
    sessionFactory.getCurrentSession().delete(propuesta);
  }

  @Override
  public Usuario buscarUsuarioPorId(Long id) {
    return (Usuario) sessionFactory
      .getCurrentSession()
      .createCriteria(Usuario.class)
      .add(Restrictions.eq("id", id))
      .uniqueResult();
  }

  @Override
  public List<PropuestaIntercambio> listarTodasLasActivas() {
    return sessionFactory
      .getCurrentSession()
      .createCriteria(PropuestaIntercambio.class)
      .add(Restrictions.eq("estado", "ACTIVA"))
      .list();
  }

  @Override
  public List<PropuestaIntercambio> listarMisTrades(Usuario usuarioActual) {
    return sessionFactory
      .getCurrentSession()
      .createCriteria(PropuestaIntercambio.class)
      .add(Restrictions.eq("usuarioEmisor", usuarioActual))
      .list();
  }

  @Override
  public List<Carta> listarTodasLasCartas() {
    return sessionFactory.getCurrentSession().createCriteria(Carta.class).list();
  }
}
