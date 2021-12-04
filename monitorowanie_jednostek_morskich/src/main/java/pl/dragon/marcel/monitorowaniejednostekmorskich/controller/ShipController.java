package pl.dragon.marcel.monitorowaniejednostekmorskich.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import pl.dragon.marcel.monitorowaniejednostekmorskich.service.ShipService;

@Controller
public class ShipController {
	@Autowired
	private ShipService shipService;

	@GetMapping
	public String getMap(Model model) {
		model.addAttribute("ships", shipService.getShipsInSkope());
		return "index";
	}
}
