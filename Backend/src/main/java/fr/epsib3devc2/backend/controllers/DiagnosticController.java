package fr.epsib3devc2.backend.controllers;

import fr.epsib3devc2.backend.repositories.PandemicsRepository;
import fr.epsib3devc2.backend.repositories.RegionsRepository;
import fr.epsib3devc2.backend.repositories.TotalByDayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/diagnostic")
public class DiagnosticController {

    @Autowired
    private PandemicsRepository pandemicsRepository;
    
    @Autowired
    private RegionsRepository regionsRepository;
    
    @Autowired
    private TotalByDayRepository totalByDayRepository;

    @GetMapping
    public String diagnosticPage(Model model) {
        model.addAttribute("pandemics", pandemicsRepository.findAll());
        model.addAttribute("regions", regionsRepository.findAll());
        model.addAttribute("totalRecords", totalByDayRepository.count());
        
        return "diagnostic";
    }
}