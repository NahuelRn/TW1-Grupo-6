package com.tallerwebi.infraestructura;

import static org.junit.Assert.assertNotNull;

import com.tallerwebi.config.HibernateConfig; // Tu configuración real
import com.tallerwebi.dominio.RepositorioCarta;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { HibernateConfig.class, RepositorioCartaTest.TestConfig.class })
@Transactional
public class RepositorioCartaTest {

  @Configuration
  @ComponentScan(basePackages = { "com.tallerwebi.dominio", "com.tallerwebi.infraestructura" })
  public static class TestConfig {}

  @Autowired
  private RepositorioCarta repositorioCarta;

  @Test
  public void queSePuedanListarCartasDesdeLaBaseDeDatos() {
    assertNotNull(repositorioCarta);
    assertNotNull(repositorioCarta.listarTodas());
  }
}
