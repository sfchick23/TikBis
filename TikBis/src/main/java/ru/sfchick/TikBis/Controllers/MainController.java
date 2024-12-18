package ru.sfchick.TikBis.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.sfchick.TikBis.Services.ParseService;

import java.util.Collections;
import java.util.List;

@Controller
public class MainController {

    private final ParseService parseService;

    @Autowired
    public MainController(ParseService parseService) {
        this.parseService = parseService;
    }

    @GetMapping()
    public String links(Model model) {
        return "tiktok/links";
    }

    @GetMapping("documents")
    public String linksGet(Model model) {
        model.addAttribute("docs" ,parseService.processAndSubmit());
        return "tiktok/view-info";
    }
}
