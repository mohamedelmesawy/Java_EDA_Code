package spark;


import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Youtube Word Count
public class SparkDemo {

    private static final String COMMA_DELIMITER = ",";

    public static void main(String[] args) {
        Logger.getLogger("org").setLevel(Level.ERROR);

        // Create Spark Context
        SparkConf conf = new SparkConf().setAppName("wordCounts").setMaster("local[*]");   // using 3 CPU cores
        JavaSparkContext context = new JavaSparkContext(conf);


        // Load Data-sets
        JavaRDD<String> videos = context.textFile("src/main/resources/USvideos.csv");


        // Transformation
        JavaRDD<String> titles = videos
                .map(SparkDemo::extractTitle)
                .filter(StringUtils::isNotBlank);

        JavaRDD<String> words = titles
                .flatMap(title -> Arrays.asList(title
                                .toLowerCase()
                                .trim()
                                .replaceAll("\\p{Punct}", "")
                                .split("")
                        )
                        .iterator()
                );


        // Counting
        Map<String, Long> wordCounts = words.countByValue();

        List<Map.Entry> sorted = wordCounts.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toList());


        // Display
        for (Map.Entry entry : sorted)
            System.out.println(entry.getKey() + " : " + entry.getValue());

    }


    // PRIVATE FUNCTIONS --------------------------------------------------
    private static String extractTitle(String videoLine) {
        try {
            return videoLine.split(COMMA_DELIMITER)[2];
        } catch (ArrayIndexOutOfBoundsException e) {
            return "";
        }
    }

}
