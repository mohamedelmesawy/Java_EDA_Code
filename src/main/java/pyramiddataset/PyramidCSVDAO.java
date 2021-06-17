/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pyramiddataset;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mohamed
 */
public class PyramidCSVDAO implements PyramidDAOImp {
    
    @Override
    public List<Pyramid> readPyramids(String fileName){
        List<Pyramid> pyramids = new ArrayList<>();
        List<String> lines = new ArrayList<>();

        // access the data csv file
        var file = new File(fileName);
        
        // read the file lines
        try {
           lines = Files.readAllLines(file.toPath());
        } catch (IOException ex) {
            System.out.println( "Error happened when reading the file in class: " + PyramidCSVDAO.class.getName());
        }
        
        // extract each pyramid info
        String[] headers = lines.get(0).split(",");
        
        for (int i = 1; i < lines.size(); i++) {
            String[] fields = lines.get(i).split(",");
            for (String field : fields)
                field = field.trim();

            pyramids.add(createPyramid(fields, headers));
        }
        
        System.out.println("Total number of pyramids is: " + pyramids.size());
        
        return pyramids;
    }

    
    @Override
    public Pyramid createPyramid(String[] metadata, String[] headers) {
        String pharaoh = "";
        String modernName = "";
        String site = "";
        double height = 0;
        
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].toLowerCase().equals("pharaoh"))
                pharaoh = metadata[i];
            else if (headers[i].toLowerCase().contains("modern"))
                modernName = metadata[i];
            else if (headers[i].toLowerCase().equals("site"))
                site = metadata[i];
            else if (headers[i].toLowerCase().contains("height") && !metadata[i].isBlank())
                height = Double.parseDouble(metadata[i]);
        }
        
        return new Pyramid(modernName, height, pharaoh, site);
    }
    
}
