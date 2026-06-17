package com.tallerwebi.dominio;

import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ServicioMercadoImpl implements ServicioMercado {

  private static final int MINIMO_COPIAS_PARA_TRADEAR = 1;
  private static final int CANTIDAD_A_DESCONTAR = 1;
  private static final int CANTIDAD_A_INCREMENTAR = 1;
  private static final int STOCK_MINIMO_DISPONIBLE = 0;
  private static final int STOCK_INICIAL_NUEVO_ITEM = 1;

  private final RepositorioMercado repositorioMercado;

  public ServicioMercadoImpl(RepositorioMercado repositorioMercado) {
    this.repositorioMercado = repositorioMercado;
  }

  @Override
  public void publicarOferta(Usuario usuario, Carta carta, String rarezaBuscada) throws Exception {
    ItemInventario item = usuario
      .getInventario()
      .stream()
      .filter(i -> i.getCarta().getId().equals(carta.getId()))
      .findFirst()
      .orElseThrow(() -> new Exception("No posees esta carta en tu inventario."));

    if (item.getCantidad() <= MINIMO_COPIAS_PARA_TRADEAR) {
      throw new Exception("Solo puedes tradear cartas que tengas repetidas.");
    }

    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setUsuarioEmisor(usuario);
    propuesta.setCartaOfrecida(carta);
    propuesta.setRarezaBuscada(rarezaBuscada);

    repositorioMercado.guardar(propuesta);
  }

  @Override
  public List<PropuestaIntercambio> obtenerMercado(Usuario usuario) {
    return repositorioMercado.listarOfertasDeOtros(usuario);
  }

  @Override
  public void aceptarOferta(Usuario usuarioReceptor, Long idOferta) throws Exception {
    PropuestaIntercambio propuesta = repositorioMercado.buscarPorId(idOferta);

    if (propuesta == null) {
      throw new Exception("La propuesta de intercambio ya no está disponible.");
    }

    ItemInventario itemPagoReceptor = usuarioReceptor
      .getInventario()
      .stream()
      .filter(i ->
        i.getCarta().getRareza().equalsIgnoreCase(propuesta.getRarezaBuscada()) &&
        i.getCantidad() > STOCK_MINIMO_DISPONIBLE
      )
      .findFirst()
      .orElseThrow(() ->
        new Exception(
          "No tienes ninguna carta de rareza '" +
          propuesta.getRarezaBuscada() +
          "' para ofrecer a cambio."
        )
      );

    ItemInventario itemOfrecidoEmisor = propuesta
      .getUsuarioEmisor()
      .getInventario()
      .stream()
      .filter(i ->
        i.getCarta().getId().equals(propuesta.getCartaOfrecida().getId()) &&
        i.getCantidad() > STOCK_MINIMO_DISPONIBLE
      )
      .findFirst()
      .orElseThrow(() -> new Exception("El emisor ya no dispone de la carta ofrecida."));

    // Modificamos stocks utilizando las constantes declaradas arriba
    itemPagoReceptor.setCantidad(itemPagoReceptor.getCantidad() - CANTIDAD_A_DESCONTAR);
    itemOfrecidoEmisor.setCantidad(itemOfrecidoEmisor.getCantidad() - CANTIDAD_A_DESCONTAR);

    // Acreditación al Emisor
    ItemInventario itemDestinoEmisor = propuesta
      .getUsuarioEmisor()
      .getInventario()
      .stream()
      .filter(i -> i.getCarta().getId().equals(itemPagoReceptor.getCarta().getId()))
      .findFirst()
      .orElse(null);

    if (itemDestinoEmisor != null) {
      itemDestinoEmisor.setCantidad(itemDestinoEmisor.getCantidad() + CANTIDAD_A_INCREMENTAR);
    } else {
      ItemInventario nuevoItem = new ItemInventario();
      nuevoItem.setJugador(propuesta.getUsuarioEmisor().getJugador());
      nuevoItem.setCarta(itemPagoReceptor.getCarta());
      nuevoItem.setCantidad(STOCK_INICIAL_NUEVO_ITEM);
      propuesta.getUsuarioEmisor().getInventario().add(nuevoItem);
    }

    // Acreditación al Receptor
    ItemInventario itemDestinoReceptor = usuarioReceptor
      .getInventario()
      .stream()
      .filter(i -> i.getCarta().getId().equals(propuesta.getCartaOfrecida().getId()))
      .findFirst()
      .orElse(null);

    if (itemDestinoReceptor != null) {
      itemDestinoReceptor.setCantidad(itemDestinoReceptor.getCantidad() + CANTIDAD_A_INCREMENTAR);
    } else {
      ItemInventario nuevoItem = new ItemInventario();
      nuevoItem.setJugador(usuarioReceptor.getJugador());
      nuevoItem.setCarta(propuesta.getCartaOfrecida());
      nuevoItem.setCantidad(STOCK_INICIAL_NUEVO_ITEM);
      usuarioReceptor.getInventario().add(nuevoItem);
    }

    repositorioMercado.eliminar(propuesta);
  }

  @Override
  public List<ItemInventario> obtenerCartasRepetidas(Usuario usuario) {
    if (usuario == null || usuario.getInventario() == null) {
      return List.of();
    }
    return usuario
      .getInventario()
      .stream()
      .filter(item -> item.getCantidad() > MINIMO_COPIAS_PARA_TRADEAR)
      .collect(Collectors.toList());
  }

  @Override
  public void crearPropuesta(Usuario usuario, Long idCarta, String rarezaBuscada) throws Exception {
    if (idCarta == null || rarezaBuscada == null || rarezaBuscada.isBlank()) {
      throw new Exception("Datos de propuesta inválidos.");
    }

    ItemInventario item = usuario
      .getInventario()
      .stream()
      .filter(i -> i.getCarta().getId().equals(idCarta))
      .findFirst()
      .orElseThrow(() -> new Exception("No posees esta carta en tu inventario."));

    if (item.getCantidad() <= MINIMO_COPIAS_PARA_TRADEAR) {
      throw new Exception("Solo puedes tradear cartas que tengas repetidas.");
    }

    PropuestaIntercambio propuesta = new PropuestaIntercambio();
    propuesta.setUsuarioEmisor(usuario);
    propuesta.setCartaOfrecida(item.getCarta());
    propuesta.setRarezaBuscada(rarezaBuscada);

    repositorioMercado.guardar(propuesta);
  }
}
