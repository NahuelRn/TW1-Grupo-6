package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioMercado {
  List<PropuestaIntercambio> listarOfertasDeOtros(Usuario usuarioActual);
  void guardar(PropuestaIntercambio propuesta);
  PropuestaIntercambio buscarPorId(Long id);
  void eliminar(PropuestaIntercambio propuesta);
  // Agregamos esto para acoplar prolijamente la búsqueda del usuario
  Usuario buscarUsuarioPorId(Long id);
}
