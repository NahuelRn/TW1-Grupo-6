package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioMercado {
  List<PropuestaIntercambio> listarOfertasDeOtros(Usuario usuarioActual);
  void guardar(PropuestaIntercambio propuesta);
  PropuestaIntercambio buscarPorId(Long id);
  void eliminar(PropuestaIntercambio propuesta);
  Usuario buscarUsuarioPorId(Long id);
}
