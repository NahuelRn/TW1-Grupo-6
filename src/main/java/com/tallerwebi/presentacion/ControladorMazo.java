package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Carta;
import com.tallerwebi.dominio.Mazo;
import com.tallerwebi.dominio.MazoCarta;
import com.tallerwebi.dominio.ServicioMazo;
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

  private final ServicioMazo servicioMazo;

  @Autowired
  public ControladorMazo(ServicioMazo servicioMazo) {
    this.servicioMazo = servicioMazo;
  }

  @RequestMapping("/deckbuilding")
  public ModelAndView irADeckbuilding() {
    ModelMap modelo = new ModelMap();
    // Datos de prueba para ver cartas en la pantalla
    List<Carta> inventario = servicioMazo.buscarCartasPorIds(List.of(1L, 2L, 3L, 4L, 5L));
    modelo.put("inventario", inventario);
    return new ModelAndView("deckbuilding", modelo);
  }

  // Método para recibir los datos del formulario (POST)
  @RequestMapping(path = "/mazo/guardar", method = RequestMethod.POST)
  public String guardarMazo(@RequestParam("cartasIds") List<Long> cartasIds, ModelMap modelo) {
    try {
      // 1. Creamos el objeto Mazo principal
      Mazo nuevoMazo = new Mazo();

      // 2. Obtenemos las cartas elegidas
      List<Carta> cartasSeleccionadas = this.servicioMazo.buscarCartasPorIds(cartasIds);

      // 3. Creamos la entidad intermedia (MazoCarta) para cada carta
      for (Carta carta : cartasSeleccionadas) {
        MazoCarta nexo = new MazoCarta();
        nexo.setMazo(nuevoMazo);
        nexo.setCarta(carta);
        nuevoMazo.getMazoCartas().add(nexo);
      }

      // 4. Llamamos al servicio para validar (15 cartas, sin repetidos)
      servicioMazo.validarYGuardarMazo(nuevoMazo);

      // 5. CAMBIO APLICADO: Si todo está OK, redirigimos a la Selección de Zona
      return "redirect:/seleccion-zona";
    } catch (Exception e) {
      // 6. Si falla la validación, cargamos el error y volvemos a la vista
      modelo.put("error", e.getMessage());
      modelo.put("inventario", servicioMazo.buscarCartasPorIds(List.of(1L, 2L, 3L)));
      return "deckbuilding";
    }
  }
  /*private List<Carta> obtenerInventarioEjemplo() {
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
  } */
}
