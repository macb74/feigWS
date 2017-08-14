package de.opentiming.feigWS.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import de.opentiming.feigWS.reader.FedmConnect;

@Controller
public class FeigWsHtmlController {
	
	@Resource(name = "connections")
	private Map<String, FedmConnect> connections;
	
    @RequestMapping("/app")
    public String greeting(Model model) {
    	model.addAttribute("readers", connections.keySet());
        return "index";
    }

}