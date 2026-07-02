package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Jugador;
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
  public void guardar(PropuestaIntercambio propuesta) {
    sessionFactory.getCurrentSession().saveOrUpdate(propuesta);
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

  @Override
  public Usuario buscarUsuarioPorJugadorId(Long jugadorId) {
    final Jugador jugador = sessionFactory.getCurrentSession().get(Jugador.class, jugadorId);
    if (jugador == null) return null;
    return jugador.getUsuario();
  }

  /** Propuestas donde el usuario es EMISOR (las que publicó). */
  @Override
  @SuppressWarnings("unchecked")
  public List<PropuestaIntercambio> listarMisTrades(Usuario usuario) {
    return sessionFactory
      .getCurrentSession()
      .createCriteria(PropuestaIntercambio.class)
      .add(Restrictions.eq("usuarioEmisor", usuario))
      .list();
  }

  /** Propuestas FINALIZADAS donde el usuario es RECEPTOR (las que aceptó). */
  @Override
  @SuppressWarnings("unchecked")
  public List<PropuestaIntercambio> listarTradesAceptados(Usuario usuario) {
    return sessionFactory
      .getCurrentSession()
      .createCriteria(PropuestaIntercambio.class)
      .add(Restrictions.eq("usuarioReceptor", usuario))
      .add(Restrictions.eq("estado", "FINALIZADA"))
      .list();
  }

  /** Todas las propuestas ACTIVAS (de cualquier usuario). */
  @Override
  @SuppressWarnings("unchecked")
  public List<PropuestaIntercambio> listarTodasLasActivas() {
    return sessionFactory
      .getCurrentSession()
      .createCriteria(PropuestaIntercambio.class)
      .add(Restrictions.eq("estado", "ACTIVA"))
      .list();
  }
}
