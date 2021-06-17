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
public class DAOCountries implements DAOImp<Country>{
     @Override
    public List<Country> readData(String fileName) {
        List<Country> countries = new ArrayList<>();
        List<String> lines = new ArrayList<>();

        // access the data csv file
        var file = new File(fileName);
        
        // read the file lines
        try {
           lines = Files.readAllLines(file.toPath());
        } catch (IOException ex) {
            System.out.println("Error happened when reading the file in class: " + DAOCountries.class.getName());
        }
        

        // extract each COUNTRY info
        String[] headers = lines.get(0).split(",");
        
        for (int i = 1; i < lines.size(); i++) {
            String[] fields = lines.get(i).split(",");
            for (String field : fields)
                field = field.replaceAll("[^\\d+-]","").trim();

            var country = createEntity(fields, headers);
            countries.add(country);  
        }
        System.out.println("Total number of countries is: " + countries.size());
        
        return countries;
               
    }
    
    @Override
    public Country createEntity(String[] metadata, String[] headers) {
        String name = "";
        String code = "";
        String continent = "";

        for (int i = 0; i < headers.length; i++) {
            if (headers[i].toLowerCase().trim().equals("official_name_en"))
                name = metadata[i].toLowerCase().replaceAll("[^a-zA-Z]", "").trim();
            else if (headers[i].trim().contains("FIFA"))
                code = metadata[i].toLowerCase().replaceAll("[^a-zA-Z]", "").trim();
            else if (headers[i].toLowerCase().replaceAll("[^a-zA-Z]", "").trim().equals("continent"))
                continent = metadata[i].toLowerCase().replaceAll("[^a-zA-Z]", "").trim();
            
        }
        
        return new Country(name, code, continent);
    }
    
}
