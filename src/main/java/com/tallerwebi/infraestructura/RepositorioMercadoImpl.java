package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.PropuestaIntercambio;
import com.tallerwebi.dominio.RepositorioMercado;
import com.tallerwebi.dominio.Usuario;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioMercadoImpl implements RepositorioMercado {

  private final SessionFactory sessionFactory;

  public RepositorioMercadoImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<PropuestaIntercambio> listarOfertasDeOtros(Usuario usuarioActual) {
    return sessionFactory
      .getCurrentSession()
      .createCriteria(PropuestaIntercambio.class)
      .add(Restrictions.ne("usuarioEmisor", usuarioActual))
      .list();
  }

  @Override
  public void guardar(PropuestaIntercambio propuesta) {
    sessionFactory.getCurrentSession().save(propuesta);
  }

  @Override
  public PropuestaIntercambio buscarPorId(Long id) {
    return sessionFactory.getCurrentSession().get(PropuestaIntercambio.class, id);
  }

  @Override
  public void eliminar(PropuestaIntercambio propuesta) {
    sessionFactory.getCurrentSession().delete(propuesta);
  }

  @Override
  public Usuario buscarUsuarioPorId(Long id) {
    return sessionFactory.getCurrentSession().get(Usuario.class, id);
  }
}
