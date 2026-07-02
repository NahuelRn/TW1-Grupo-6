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

  /**
   * Busca por ID de USUARIO (tabla usuario). Usado internamente si tienes el ID de usuario.
   */
  @Override
  public Usuario buscarUsuarioPorId(Long id) {
    return sessionFactory.getCurrentSession().get(Usuario.class, id);
  }

  /**
   * Busca el Usuario a partir del ID de JUGADOR (tabla jugador).
   * Esto es lo que necesitamos porque en sesión guardamos jugador.getId().
   */
  @Override
  public Usuario buscarUsuarioPorJugadorId(Long jugadorId) {
    Jugador jugador = sessionFactory.getCurrentSession().get(Jugador.class, jugadorId);
    if (jugador == null) return null;
    return jugador.getUsuario();
  }

  /**
   * Todas las propuestas del propio usuario (activas e históricas).
   */
  @Override
  @SuppressWarnings("unchecked")
  public List<PropuestaIntercambio> listarMisTrades(Usuario usuario) {
    return sessionFactory
      .getCurrentSession()
      .createCriteria(PropuestaIntercambio.class)
      .add(Restrictions.eq("usuarioEmisor", usuario))
      .list();
  }

  /**
   * Todas las propuestas con estado ACTIVA.
   * El servicio filtra en memoria cuáles son compatibles para el usuario actual.
   */
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
