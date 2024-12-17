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

    @GetMapping("links")
    public String links(Model model) {
        List<String> codes = parseService.parseAllCodes();
        System.out.println(codes);
        model.addAttribute("links", codes);
        return "tiktok/links";
    }

    @GetMapping("links-get")
    public String linksGet(Model model) {
        parseService.processAndSubmit();

        return "idinahuy";
    }
}
