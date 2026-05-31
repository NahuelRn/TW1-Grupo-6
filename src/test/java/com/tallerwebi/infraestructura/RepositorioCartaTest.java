package com.tallerwebi.infraestructura;

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

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
// IMPORTANTE: NO cargues SpringWebConfig aquí, solo HibernateConfig
@ContextConfiguration(classes = {HibernateConfig.class, RepositorioCartaTest.TestConfig.class})
@Transactional
public class RepositorioCartaTest {

    @Configuration
    // Escaneamos solo el dominio y la infraestructura, donde están los beans que necesitamos
    @ComponentScan(basePackages = {"com.tallerwebi.dominio", "com.tallerwebi.infraestructura"})
    public static class TestConfig {
    }

    @Autowired
    private RepositorioCarta repositorioCarta;

    @Test
    public void queSePuedanListarCartasDesdeLaBaseDeDatos() {
        assertNotNull(repositorioCarta); // Validamos que el bean se inyectó
        assertNotNull(repositorioCarta.listarTodas()); // Validamos la base
    }
}