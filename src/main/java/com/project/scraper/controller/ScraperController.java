package com.project.scraper.controller;

import com.project.scraper.model.ResponseDTO;
import com.project.scraper.service.ScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping(path = "/")
public class ScraperController {
    @Autowired
    ScraperService scraperService;

    @GetMapping(path = "/{vehicleModel}")
    public Set<ResponseDTO> getVehicleByModel(@PathVariable String vehicleModel) {
        return  scraperService.getVehicleByModel(vehicleModel);
    }
}
