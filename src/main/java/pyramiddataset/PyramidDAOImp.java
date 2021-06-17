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
public interface PyramidDAOImp {
    List<Pyramid> readPyramids(String fileName);
    Pyramid createPyramid(String[] metadata, String[] headers);
}
