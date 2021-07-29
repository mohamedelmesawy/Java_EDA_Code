/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package countriesdatabase;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mohamed
 */
public class DAOCities implements DAOImp<City>{

    @Override
    public List<City> readData(String fileName) {
        List<City> cities = new ArrayList<>();
        List<String> lines = new ArrayList<>();

        // access the data csv file
        var file = new File(fileName);
        
        // read the file lines
        try {
           lines = Files.readAllLines(file.toPath());
        } catch (IOException ex) {
            System.out.println( "Error happened when reading the file in class: " + DAOCities.class.getName());
        }
        
        // extract each CITY info
        String[] headers = lines.get(0).split(",");
        
        for (int i = 1; i < lines.size(); i++) {
            String[] fields = lines.get(i).split(",");

            for (int j = 0; j < fields.length; j++)
                fields[j] = fields[j].replaceAll("[^0-9a-zA-Z]", "").trim();

            var city =createEntity(fields, headers);
            cities.add(city);  
        }
        System.out.println("Total number of cities is: " + cities.size());
        
        return cities;
    }

    @Override
    public City createEntity(String[] metadata, String[] headers) {
        String name = "";
        String code = "";
        int population = 0;
        String country = null;
        boolean isCapital = false;

        for (int i = 0; i < headers.length; i++) {
            if (headers[i].toLowerCase().replaceAll("[^a-zA-Z]", "").trim().equals("city"))
                name = metadata[i].toLowerCase().replaceAll("[^a-zA-Z]", "").trim();
            else if (headers[i].toLowerCase().contains("iso3"))
                code = metadata[i].toLowerCase().replaceAll("[^a-zA-Z]", "").trim();
            else if (headers[i].toLowerCase().contains("population") && !metadata[i].replaceAll("[^\\d+-]","").trim().isBlank() )
                population = Integer.parseInt(metadata[i].replaceAll("[^\\d+-]","").trim());
            else if (headers[i].toLowerCase().contains("country"))
                country = metadata[i].toLowerCase().replaceAll("[^a-zA-Z]", "").trim();
            else if (headers[i].toLowerCase().contains("capital") && metadata[i].replaceAll("[^a-zA-Z]", "").trim().equals("primary"))
                isCapital = true;
        }

        return new City(name, code, population, country, isCapital);
    }
    
}
