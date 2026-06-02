package com.se.publicityplatform.controller;

import com.se.publicityplatform.service.AssetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping({"/assets", "/assets/list"})
    public String list(Model model) {
        model.addAttribute("assets", assetService.archivedAssets());
        return "assets/list";
    }
}
