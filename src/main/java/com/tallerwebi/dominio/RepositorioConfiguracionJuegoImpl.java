package com.tallerwebi.dominio;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioConfiguracionJuegoImpl implements RepositorioConfiguracionJuego {

  private SessionFactory sessionFactory;

  public RepositorioConfiguracionJuegoImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public Integer obtenerValor(String clave) {
    ConfiguracionJuego configuracionJuego =
      (ConfiguracionJuego) this.sessionFactory.getCurrentSession()
        .createQuery("FROM ConfiguracionJuego c WHERE c.clave = :clave")
        .setParameter("clave", clave)
        .uniqueResult();

    if (configuracionJuego == null) {
      throw new RuntimeException("Configuración no encontrada: " + clave);
    }

    return configuracionJuego.getValor();
  }
}
