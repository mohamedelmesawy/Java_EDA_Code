package xchart;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Hello world!
 *
 */
public class XChartDemo
{
    public static void main( String[] args )
    {
        var passengerList = getPassengerFromJsonFile("src/main/resources/titanic_csv.json");

        System.out.println(passengerList);

        graphPassengerAges(passengerList);
        graphPassengerClass(passengerList);
        graphPassengerSurvivedVSClass(passengerList);
    }


    private static List<TitanicPassenger> getPassengerFromJsonFile(String fileName){
        List<TitanicPassenger> allPassengers = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try (InputStream inputStream= new FileInputStream(fileName)) {
            allPassengers = mapper.readValue(inputStream, new TypeReference<List<TitanicPassenger>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }

        return allPassengers;
    }

    private static void graphPassengerAges(List<TitanicPassenger> passengerList){
        List<Float> pAges = passengerList.stream()
                .map(TitanicPassenger::getAge)
                .limit(8)
                .collect(Collectors.toList());

        List<String> pNames = passengerList.stream()
                .map(TitanicPassenger::getName)
                .limit(8)
                .collect(Collectors.toList());

        Float[] ages = new Float[pAges.size()];
        String[] names = new String[pNames.size()];

        // Create Chart
        CategoryChart chart = new CategoryChartBuilder()
                .width(1024)
                .height(768)
                .title("Age Histogram")
                .xAxisTitle("Names")
                .yAxisTitle("Age")
                .build();

        // Customizing Chart
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setHasAnnotations(true);
        chart.getStyler().setStacked(true);

        // Series
        chart.addSeries("Passenger's Ages", pNames, pAges);

        // Show Graph
        new SwingWrapper<CategoryChart>(chart).displayChart();
    }

    private static void graphPassengerClass(List<TitanicPassenger> passengerList) {
        //filter to get a map of passenger class and total number of passengers in each class
        Map<String, Long> result =
                passengerList.stream()
                    .collect( Collectors.groupingBy (
                                    TitanicPassenger::getPclass,
                                    Collectors.counting()
                            )
                    );

        // Create Chart
        PieChart chart = new PieChartBuilder().width(800).height(600).title(XChartDemo.class.getSimpleName()).build();

        // Customize Chart
        Color[] sliceColors = {
            new Color (180, 68, 50),
            new Color(130, 105, 120),
            new Color(80, 143, 160)
        };

        chart.getStyler().setSeriesColors(sliceColors);

        // Series
        chart.addSeries ("First Class", result.get("1"));
        chart.addSeries ("Second Class", result.get("2"));
        chart.addSeries ("Third Class", result.get("3"));

        // Show it
        new SwingWrapper (chart).displayChart();
        System.out.println(result);
    }

    private static void graphPassengerSurvivedVSClass(List<TitanicPassenger> passengerList){
        List<Float> ages =  passengerList.stream()
                .map(p -> p.getAge())
                .collect(Collectors.toList());

        List<Float> fares =  passengerList.stream()
                .map( p -> p.getFare() )
                .collect(Collectors.toList());

        // Create Chart
        XYChart chart = new XYChartBuilder().width(800).height(600).title(XChartDemo.class.getSimpleName()).build();

        // Customize Chart
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
        chart.getStyler().setChartTitleVisible(false);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideSW);
        chart.getStyler().setMarkerSize(16);

        // Series
        chart.addSeries("Passenger Age - Fare",
                ages,
                fares);


        // Show it
        new SwingWrapper (chart).displayChart();
    }

}
