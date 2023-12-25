package com.project.scraper.service;

import com.project.scraper.model.ResponseDTO;
import java.util.Set;
public interface ScraperService {
    Set<ResponseDTO> getVehicleByModel(String vehicleModel);
}
