package com.tallerwebi.presentacion;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ControladorLobby {

  @RequestMapping(path = "/lobby", method = RequestMethod.GET)
  public String irAlLobby(ModelMap modelo) {
    return "lobby";
  }
}
