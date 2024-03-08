package org.hh99.tmomi_consumer.emitter.controller.v1;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

	@GetMapping("/")
	public String getQueueNumber() {
		return "queueNumber";
	}
}
