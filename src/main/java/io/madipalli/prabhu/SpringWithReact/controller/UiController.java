package io.madipalli.prabhu.SpringWithReact.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Behaviour object in Domain Data Layer. <a href="https://w.amazon.com/bin/view/ACBDA_Pattern/Specifications/2.0">ACBDA Pattern</a>
 */
@Controller
@Profile("!prod")
public class UiController {

	@RequestMapping(value = "/")
	public String index() {
		return "index";
	}
}
