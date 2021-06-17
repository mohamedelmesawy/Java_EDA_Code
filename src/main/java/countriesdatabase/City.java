/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package countriesdatabase;

/**
 *
 * @author Mohamed
 */
public class City implements Comparable<City>{
    private String name;
    private String code;
    private int population;
    private String country;

    
    public City(String name, String code, int population, String country) {
        this.name = name;
        this.code = code;
        this.country = country;
        this.population = population;
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

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
    
    
    @Override
    public String toString() {
        // return "City{" + "name=" + name + ", code=" + code + ", population=" + population + ", country=" + country + '}';
        return "< " + name.toUpperCase() + " -  " + population + " >";
    }

    @Override
    public int compareTo(City other) {
        return Integer.compare(this.population, other.population);
    }
    
}
