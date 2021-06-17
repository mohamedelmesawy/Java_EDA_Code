/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pyramiddataset;

import java.util.List;

/**
 *
 * @author Mohamed
 */
public class MainClass {
    public static void main(String[] args) {
        var pyramidCSVDAO = new PyramidCSVDAO();

        List<Pyramid> readPyramids = pyramidCSVDAO.readPyramids( "src/main/resources/pyramids.csv");
        for (Pyramid pyramid : readPyramids) {
            System.out.println(pyramid);
        }
        
    }
}
