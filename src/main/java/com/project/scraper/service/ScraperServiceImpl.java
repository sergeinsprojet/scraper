package com.project.scraper.service;

import com.project.scraper.model.ResponseDTO;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;


import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Service
public class ScraperServiceImpl  implements ScraperService {
    //Reading data from property file to a list
    @Value("#{'${website.urls}'.split(',')}")
    List<String> urls;

    @Override
    public Set<ResponseDTO> getVehicleByModel(String vehicleModel) {
        //Using a set here to only store unique elements
        Set<ResponseDTO> responseDTOS = new HashSet<>();
        //Traversing through the urls
        for (String url: urls) {

            if (url.contains("biz-trade")) {
                //method to extract data from Ikman.lk
                extractDataFromIkman(responseDTOS, url);
            }

        }

        return responseDTOS;
    }

    private void extractDataFromRiyasewana(Set<ResponseDTO> responseDTOS, String url) {

        try {
            //loading the HTML to a Document Object
            Document document = Jsoup.connect(url).get();
            //Selecting the element which contains the ad list
            Element element = document.getElementById("content");
            //getting all the <a> tag elements inside the content div tag
            Elements elements = element.getElementsByTag("a");
            //traversing through the elements
            for (Element ads: elements) {
                ResponseDTO responseDTO = new ResponseDTO();
                if (!StringUtils.isEmpty(ads.attr("title")) ) {
                    //mapping data to the model class
                    responseDTO.setTitle(ads.attr("title"));
                    responseDTO.setUrl(ads.attr("href"));
                }
                if (responseDTO.getUrl() != null) responseDTOS.add(responseDTO);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void extractDataFromIkman(Set<ResponseDTO> responseDTOS, String url) {
        try {
            //loading the HTML to a Document Object
            Document document = Jsoup.connect(url).get();
//Selecting the element which contains the ad list
            Elements elements = document.getElementsByClass("adsxs");

            Integer parsingCounter = 0;
            for (Element ads: elements) {
                parsingCounter++;
                ResponseDTO responseDTO = new ResponseDTO();
                Elements adsElements = ads.children();
                Elements typeElements = ads.getElementsByTag("p").get(0).getElementsByTag("i");
                String type = null;
                if(typeElements.size() != 0){
                    type = typeElements.get(1).text();
                }
                System.out.println("Type: " + type);
                Element motivation = ads.getElementsByTag("span").get(0);
                System.out.println("Motivation: " + motivation.text());
                Element descriptionElement = ads.getElementById("descr");;
                Element descriptionTitleElement = null;
                Element descriptionTextElement = null;
                String description = null;
                String title = null;
                Element areaElement = adsElements.get(adsElements.size() - 4);
                String size = null;
                Element sizeElement = null;
                Element sizeAndLocation = adsElements.get(adsElements.size() - 2);
                String location = null;
                String area = null;

                if(descriptionElement != null){
                    area = areaElement.text();
                    
                    if(descriptionElement != null){
                        descriptionTitleElement = descriptionElement.getElementsByTag("a").get(0);
                        if(descriptionTitleElement != null){
                            title = descriptionTitleElement.text();
                        }
                        descriptionTextElement = descriptionElement.getElementsByClass("descr-caption").get(0);
                        if(descriptionTextElement != null){
                            description = descriptionTextElement.text();
                        }
                    }
                    if(sizeAndLocation != null){
                        
                        if(!sizeAndLocation.getElementsByTag("i").isEmpty()){
                            sizeElement = sizeAndLocation.getElementsByTag("i").get(1);
                            size = sizeElement.text();
                        }
                        if(size != null){
                            location = sizeAndLocation.text().replace(size,"").trim();
                        }
                        else{
                            location = sizeAndLocation.text().trim();
                        }
                    }
                    System.out.println("Description: " + description);
                }
                System.out.println("Area: " + area);
                if(size != null){
                    System.out.println("Size: " + size);
                }
                System.out.println("Location: " + location);
                System.out.println("Element parsing is done. Counter: " + parsingCounter.toString());

                if (descriptionElement != null) {
                    //mapping data to our model class
                    if(title != null) responseDTO.setTitle(title);
                    if(description != null) responseDTO.setDescription(description);
                    if(area != null) responseDTO.setArea(area);
                    if(location != null) responseDTO.setLocation(location);
                    if(motivation != null) responseDTO.setMotivation(motivation.text());
                    if(size != null) responseDTO.setSize(size);
                    if(type != null) responseDTO.setType(type);
                    responseDTO.setUrl("https://www.biz-trade.de/"+  descriptionElement.getElementsByTag("a").attr("href"));
                }
                if (responseDTO.getUrl() != null) responseDTOS.add(responseDTO);

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
