package extendelibraries;

import com.google.common.collect.ListMultimap;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import com.ibm.icu.util.LocaleData;
import joinery.DataFrame;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import com.aol.simple.react.stream.lazy.LazyReact;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.jooq.lambda.tuple.Tuple2;
import org.apache.log4j.BasicConfigurator;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ExtendStandardLibDemo {
    public static void main(String[] args) {
        BasicConfigurator.configure();

        // "src/main/resources/titanic_csv.json"
        readLinesUsingApacheCommonsIO("src/main/resources/pyramids.csv");

        readLinesUsingGoogleGuava("src/main/resources/pyramids.csv");

        readCSVAsObjectsUsingApacheCommonCSV("src/main/resources/persons_generateddata.csv");

        readCSVAsDataFrameUsingJoinery("src/main/resources/persons_generateddata.csv");

        readCSVDescribeVideoGameDataUsingJoinery("src/main/resources/vgsales.csv");

        readCSVMeanVideoGameDataGroupByYearUsingJoinery("src/main/resources/vgsales.csv");

        readCSVGetSummaryStatistics("src/main/resources/vgsales.csv");

        EDAUsingTablesaw();
    }


    // Private Methods ------------------------------------------------------------------
    private static void readLinesUsingApacheCommonsIO(String fileName){
        // org.apache.commons.io
        // ListMultimap --> Map<String, List<String>>
        System.out.println("\n###################readLinesUsingApacheCommonsIO() ----------------------------------");

        try (InputStream inputStream = new FileInputStream(fileName)) {
            String allContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            System.out.println(allContent);
        } catch (IOException ignored) { }

        try (InputStream inputStream = new FileInputStream(fileName)) {
            List<String> lines = IOUtils.readLines(inputStream, StandardCharsets.UTF_8);
            System.out.println(lines);
        } catch (IOException ignored) { }
    }

    private static void readLinesUsingGoogleGuava(String fileName){
        File file = new File(fileName);

        CharSource wordsSource = Files.asCharSource(file, StandardCharsets.UTF_8);
        List<String> lines = null;
        try {
            lines = wordsSource.readLines();
        } catch (IOException ignored) { }

        System.out.println("\n###################readLinesUsingGoogleGuava() ----------------------------------");
        System.out.println(lines);
    }

    private static void readCSVAsObjectsUsingApacheCommonCSV(String fileName) {
        List<Person> result = new ArrayList<>();
        File file = new File(fileName);
        CSVFormat csv = CSVFormat.RFC4180.withHeader();

        try (BufferedReader reader = Files.newReader(file, StandardCharsets.UTF_8);
             CSVParser parser = csv.parse(reader))
        {
            Iterator<CSVRecord> iter = parser.iterator();

            iter.forEachRemaining( record -> {
                String name = record.get("name");
                String email = record.get("email");
                String country = record.get("country");
                int salary = Integer.parseInt(record.get("salary"));
                int experience = Integer.parseInt(record.get("experience"));

                Person person = new Person(name, email, country, salary, experience);
                result.add(person);
            });

            System.out.println("\n###################readCSVAsObjectsUsingApacheCommonCSV() ----------------------------------");
            System.out.println(result);
        }
        catch (IOException ignored) { }
    }

    private static void readCSVAsDataFrameUsingJoinery(String fileName){
        try {
            DataFrame<Object> df = DataFrame.readCsv(fileName);

            List<Object> countries = df.col("country");

            Map<String, Long> map = LazyReact.sequentialBuilder()
                    .from(countries)
                    .cast(String.class)
                    .distinct()
                    .zipWithIndex()
                    .toMap(Tuple2::v1, Tuple2::v2);

            List<Long> indecies = countries.stream()
                    .map(map::get)
                    .collect(Collectors.toList());

            System.out.println("\n###################readCSVAsDataFrameUsingJoinery() ----------------------------------");
            System.out.println(map);
        } catch (IOException ignored) { }



    }

    private static void readCSVDescribeVideoGameDataUsingJoinery(String fileName){
        System.out.println("\n###################readCSVDescribeVideoGameDataGroubByYearUsingJoinery() ----------------------------------");

        try {
            DataFrame<Object> df = DataFrame.readCsv(fileName)
                    .retain("Year", "NA_Sales", "EU_Sales", "JP_Sales")
                    //.groupBy( row -> row.get(0))    //groupBy ---> Year
                    .describe();

            // df.iterrows().forEachRemaining(System.out::println);

            System.out.println(df.toString());
        } catch (IOException ignored) { }
    }

    private static void readCSVMeanVideoGameDataGroupByYearUsingJoinery(String fileName){
        System.out.println("\n###################readCSVMeanVideoGameDataGroupByYearUsingJoinery() ----------------------------------");

        try {
            var df = DataFrame.readCsv(fileName)
                    .retain("Year", "NA_Sales", "EU_Sales", "JP_Sales")
                    .sortBy(Comparator.comparing(obj -> obj.get(0).toString()) )
                    .groupBy( row -> row.get(0))    //groupBy ---> Year
                    .mean();

            // df.iterrows().forEachRemaining(System.out::println);

            System.out.println(df.toString());
        } catch (IOException ignored) { }
    }

    private static void readCSVGetSummaryStatistics(String fileName){
        System.out.println("\n###################readCSVGetSummaryStatistics() ----------------------------------");

        try {
            // EU_Sales
            SummaryStatistics statistics = new SummaryStatistics();

            DataFrame.readCsv(fileName)
                    .col("EU_Sales")
                    .stream()
                    .map(obj -> (Double)obj)
                    .forEach(statistics::addValue);

            System.out.println(statistics.getSummary());
        } catch (IOException ignored) { }
    }

    private static void EDAUsingTablesaw(){
        System.out.println("\n###################EDAUsingTablesaw() ----------------------------------");

        List<LocalDate> dateList = List.of(
                LocalDate.ofYearDay(2020, 250),
                LocalDate.ofYearDay(2021, 250),
                LocalDate.ofYearDay(2022, 50),
                LocalDate.ofYearDay(2018, 20),
                LocalDate.ofYearDay(2017, 210),
                LocalDate.ofYearDay(2019, 30)
        );
        DateColumn dateColumn = DateColumn.create("Loca Data", dateList);     //ColumnType of LOCAL_DATE.

        double[] values = {1, 2, 3, 7, 9.44242, 11};
        DoubleColumn doubleColumn = DoubleColumn.create("My Numbers", values);

        Table table = Table.create();
        table.addColumns(dateColumn);
        table.addColumns(doubleColumn);


        System.out.println(dateColumn.print());
        System.out.println(doubleColumn.print());
        System.out.println(table.print());
    }

}
