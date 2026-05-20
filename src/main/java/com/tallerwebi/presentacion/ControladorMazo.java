package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Carta;
import com.tallerwebi.dominio.Mazo;
import com.tallerwebi.dominio.ServicioMazo;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorMazo {

  private ServicioMazo servicioMazo;

  @Autowired
  public ControladorMazo(ServicioMazo servicioMazo) {
    this.servicioMazo = servicioMazo;
  }

  // Método para mostrar la pantalla de armado de mazo
  @RequestMapping("/deckbuilding")
  public ModelAndView irADeckbuilding() {
    ModelMap modelo = new ModelMap();

    // Aquí deberías traer las cartas del inventario del usuario desde un servicio
    // Por ahora, pasamos una lista vacía o de ejemplo para que la vista no falle
    List<Carta> inventario = obtenerInventarioEjemplo();
    modelo.put("inventario", inventario);

    return new ModelAndView("deckbuilding", modelo);
  }

  // Método para recibir los datos del formulario (POST)
  @RequestMapping(path = "/mazo/guardar", method = RequestMethod.POST)
  public ModelAndView guardarMazo(@RequestParam("cartasIds") List<Long> cartasIds) {
    try {
      // Creamos el objeto Mazo y le asignamos las cartas según los ID recibidos
      Mazo nuevoMazo = new Mazo();
      List<Carta> cartasSeleccionadas = transformarIdsEnCartas(cartasIds);
      nuevoMazo.setCartas(cartasSeleccionadas);

      // Llamamos al servicio para validar las reglas (15 cartas, sin repetidos)
      servicioMazo.validarYGuardarMazo(nuevoMazo);

      // Si todo está bien, redirigimos al Lobby
      return new ModelAndView("redirect:/lobby");
    } catch (Exception e) {
      ModelMap modelo = new ModelMap();
      // Si hay un error (ej: menos de 15 cartas), volvemos a la vista con el mensaje
      modelo.put("error", e.getMessage());
      // Volver a cargar el inventario para que la vista no quede vacía
      modelo.put("inventario", obtenerInventarioEjemplo());
      return new ModelAndView("deckbuilding", modelo);
    }
  }

  // Métodos auxiliares
  private List<Carta> transformarIdsEnCartas(List<Long> ids) {
    List<Carta> cartas = new ArrayList<>();
    for (Long id : ids) {
      Carta carta = new Carta();
      carta.setId(id);
      cartas.add(carta);
    }
    return cartas;
  }

  private List<Carta> obtenerInventarioEjemplo() {
    List<Carta> cartasDePrueba = new ArrayList<>();

    // cartas ficticias para verlas en la pantalla
    Carta c1 = new Carta();
    c1.setId(1L);
    c1.setNombre("Golpe de Escudo");

    Carta c2 = new Carta();
    c2.setId(2L);
    c2.setNombre("Flecha Sombría");

    Carta c3 = new Carta();
    c3.setId(3L);
    c3.setNombre("Poción de Furia");

    Carta c4 = new Carta();
    c4.setId(4L);
    c4.setNombre("Invocación de Esqueleto");

    cartasDePrueba.add(c1);
    cartasDePrueba.add(c2);
    cartasDePrueba.add(c3);
    cartasDePrueba.add(c4);

    return cartasDePrueba;
  }
}
