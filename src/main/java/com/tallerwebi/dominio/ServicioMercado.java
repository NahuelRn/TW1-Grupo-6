package com.tallerwebi.dominio;

import java.util.List;

public interface ServicioMercado {
  void publicarOferta(Usuario usuario, Carta carta, String rarezaBuscada) throws Exception;
  List<PropuestaIntercambio> obtenerMercado(Usuario usuario);
  void aceptarOferta(Usuario usuarioReceptor, Long idOferta) throws Exception;
}
