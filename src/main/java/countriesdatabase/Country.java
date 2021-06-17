/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package countriesdatabase;

import java.util.List;

/**
 *
 * @author Mohamed
 */
public class Country {
    private String name;
    private String code;    
    private String continent;
    private List<City> cities;

    public Country(String name, String code, String continent) {
        this.name = name;
        this.code = code;
        this.continent = continent;
    }    


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }    

    @Override
    public String toString() {
        return "Country{" + "name=" + name + ", code=" + code + ", continent=" + continent + ", cities=" + cities + '}';
    }
    
}
