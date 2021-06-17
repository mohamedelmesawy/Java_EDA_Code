/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pyramiddataset;

/**
 *
 * @author Mohamed
 */
public class Pyramid {
    // Private Fields
    private String modernName;
    private double height;
    private String pharaoh;
    private String site;

    
    // Constructors
    public Pyramid(String modernName, double height, String pharaoh, String site) {
        this.modernName = modernName;
        this.height = height;
        this.pharaoh = pharaoh;
        this.site = site;
    }

    
    // Getters and Setters
    public String getModernName() {
        return modernName;
    }

    public void setModernName(String modernName) {
        this.modernName = modernName;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getPharaoh() {
        return pharaoh;
    }

    public void setPharaoh(String pharaoh) {
        this.pharaoh = pharaoh;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }
    
    
    // Methods
    @Override
    public String toString() {
        return "Pyramid{ " + "modernName=" + modernName + ",\theight=" + height + ",\tpharaoh=" + pharaoh + ",\tsite=" + site + " }";
    }
    
}
