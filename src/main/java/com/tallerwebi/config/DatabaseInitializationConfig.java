package com.tallerwebi.config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Configuration
public class DatabaseInitializationConfig {

  @Autowired
  private DataSource dataSource;

  @Bean
  @DependsOn("sessionFactory")
  public DataSourceInitializer dataSourceInitializer() {
    ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
    populator.addScript(new ClassPathResource("data.sql"));
    populator.setSqlScriptEncoding("UTF-8");

    DataSourceInitializer initializer = new DataSourceInitializer();
    initializer.setDataSource(dataSource);
    initializer.setDatabasePopulator(populator);

    initializer.setEnabled(baseDeDatosEstaVacia());

    return initializer;
  }

  private boolean baseDeDatosEstaVacia() {
    try (
      Connection conexion = dataSource.getConnection();
      Statement sentencia = conexion.createStatement();
      ResultSet resultado = sentencia.executeQuery("SELECT COUNT(*) FROM Usuario")
    ) {
      return resultado.next() && resultado.getInt(1) == 0;
    } catch (SQLException e) {
      return true;
    }
  }
}
