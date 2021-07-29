/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package countriesdatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author Mohamed
 */
public class MainClass {
    public static void main(String[] args) {
        var citiesDAO = new DAOCities();
        var countriesDAO = new DAOCountries();

        List<City> cities = citiesDAO.readData("src/main/resources/cities.csv");
        List<Country> countries = countriesDAO.readData("src/main/resources/countries.csv");

        Collections.sort(countries, Comparator.comparing(Country::getName));   // The Same as the below
        // Collections.sort(countries, (Country c1, Country c2) -> c1.getName().compareTo(c2.getName()));


        Map<String, List<City>> map = new HashMap<>();
        for (Country country : countries) {
            ArrayList<City> filteredCities = new ArrayList<>();

            for (City city : cities) {
                if (city.getCountry().equals(country.getName()))
                    filteredCities.add(city);
            }

            Collections.sort(filteredCities, Collections.reverseOrder());
            country.setCities(filteredCities);

            map.put(country.getCode(), country.getCities());
        }

        // for (var entry : map.entrySet())
        //     System.out.println(entry.getKey() + entry.getValue());

        // for (City city : cities)
        //     System.out.println(city);

        // for (Country country : countries)
        //     System.out.println(country);

        // for (var c : countries)
        //     System.out.println(c.getContinent());;


        System.out.println("\n\n====================================================================");
        System.out.println("HighestPopulationCityByCountry: ------------------------------------");
        getHighestPopulationCityByCountry(cities);


        System.out.println("\n\n====================================================================");
        System.out.println("HighestPopulationCityByContinent: ----------------------------------");
        getHighestPopulationCityByContinent(countries);

        System.out.println("\n\n====================================================================");
        System.out.println("HighestPopulationCapital: ------------------------------------------");
        System.out.println(getHighestPopulationCapital(cities));

    }



    // Private Methods ------------------------------------------------------------------
    private static void getHighestPopulationCityByCountry(List<City> cities){
        cities.stream()
            .collect(Collectors.groupingBy(
                    City::getCountry,
                    Collectors.maxBy( Comparator.comparing(City::getPopulation) ))
                )
            .forEach( (k, v) -> System.out.println(k.toUpperCase()  + "\t\t ---> \t" + v.get()) );
    }

    private static void getHighestPopulationCityByCountry_02(List<Country> countries){
                countries.stream()                                         // stream of countries
                     .filter(country -> country.getCities().size() > 0)
                     .map(country -> country.getCities()
                             .stream()
                             .max(Comparator.comparing(City::getPopulation))
                             .get()
                             .getName()
                      )
                .forEach(c -> System.out.println(c));
    }

    private static void getHighestPopulationCityByContinent(List<Country> countries){
        Map<String, City> sortedHashMap = new HashMap<>();

        countries.stream()
            .filter(country -> country.getCities().size() > 0)
            .collect(Collectors.groupingBy( Country::getContinent) )
            .entrySet()                                             //<Continent, Country>
            .stream()
            .forEach((entry) -> {
                var maxPopCity = entry.getValue().stream()          // countries of each Continent
                    .map(country ->
                          country.getCities()
                             .stream()
                             .max(Comparator.comparing(City::getPopulation))
                             .get()
                     )
                    .findFirst()
                    .get();
                sortedHashMap.put(entry.getKey(), maxPopCity);
            });

            sortedHashMap.forEach((k, v) -> System.out.println(k + "\t\t ---> \t" + v ));

}

    private static void getHighestPopulationCityByContinent_02(List<Country> countries){
        countries.stream()
                .filter(country -> country.getCities().size() > 0)
                .collect(Collectors.groupingBy( c -> c.getContinent()))
                .values()   //List<country>
                .stream()
                .map(countriesList -> countriesList
                        .stream()
                        .findFirst()
                        .get()
                        .getName()
                )
                .forEach( e -> System.out.println(e));
    }

    private static City getHighestPopulationCapital(List<City> cities){
        City highestPopulationCity = cities.stream()
                .filter(City::isCapital)
                .max(Comparator.comparing(City::getPopulation))
                .get();

        return highestPopulationCity;
    }

}
