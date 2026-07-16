package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Partida;
import com.tallerwebi.dominio.RepositorioPartida;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioPartida")
public class RepositorioPartidaImpl implements RepositorioPartida {

  private SessionFactory sessionFactory;

  @Autowired
  public RepositorioPartidaImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public Partida buscarPartidaPorIdentificador(Long identificador) {
    return sessionFactory.getCurrentSession().get(Partida.class, identificador);
  }

  //    @Override
  //    public Partida buscarPartidaPorIdentificador(Long identificador) {
  //        return (Partida) this.sessionFactory.getCurrentSession().createCriteria(Partida.class).add(Restrictions.eq("id", identificador)).uniqueResult();
  //    }

  @Override
  public void guardar(Partida partida) {
    this.sessionFactory.getCurrentSession().save(partida);
  }

  @Override
  public void actualizar(Partida partida) {
    this.sessionFactory.getCurrentSession().update(partida);
  }

  //    @Override
  //    public void modificar(Partida partida) {
  //        this.sessionFactory.getCurrentSession().update(partida);s
  //    }

  @Override
  public Partida buscarPartidaActivaPorUsuario(Long usuarioId) {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        "FROM Partida p WHERE p.usuario.id = :usuarioId AND p.estado = 'ACTIVA'",
        Partida.class
      )
      .setParameter("usuarioId", usuarioId)
      .uniqueResult();
  }
}
