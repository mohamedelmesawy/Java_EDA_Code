package titanicEDA;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import tech.tablesaw.api.Table;

public class MainClass {
    public static void main(String[] args) {

        // Loading Data From CSV Using Tablesaw
        Table titanicData = LoadingDataTablesaw.loadDataFromCSV("src/main/resources/titanic.csv");

        //Adding Columns to the data-table
        LoadingDataTablesaw.addDateColumnToData(titanicData);

        // Get the structure of the data
        String dataInfoStructure = LoadingDataTablesaw.getDataInfoStructure(titanicData);

        // Get Data Summary
        String summary = LoadingDataTablesaw.getDataSummary(titanicData);

        // Mapping a TEXT column to a NUMERIC valued column  [column 'SEX']  --> female=1, male=0 --> then adding a column named gender
        Table data = LoadingDataTablesaw.mapTextColumnToNumber(titanicData);

        // Show data
        System.out.println("\n\n======================= All DataFrame ===============================");
        System.out.println(data.print());


        System.out.println("\n\n======================= Data Structure Info ===============================");
        System.out.println(dataInfoStructure);


        System.out.println("\n\n======================= All Data Summary ===============================");
        System.out.println(data.summary());


        System.out.println("\n\n======================= Some Features Summary ===============================");
        var features = data.columns("survived", "pclass", "gender", "embarked");
        features.stream()
                .forEach(objects -> System.out.println(objects.summary() + "\n\n-------------------------------------"));


        System.out.println("\n\n======================= Feature Gender Summary ===============================");
        SummaryStatistics statistics = new SummaryStatistics();

        var genderFeature = data.column("gender");

        genderFeature.asList().stream()
                .forEach(value -> statistics.addValue((Double) value));

        System.out.println(statistics.getSummary());
    }
}
