package spark;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.sql.*;

public class SparkSQLDemo {

    public static void main(String[] args) {
        Logger.getLogger("org").setLevel(Level.ERROR);

        // Create Spark Session to create connection to Spark
        SparkSession session = SparkSession.builder()
                .appName("Spark CSV Analysis Demo")
                .master("local[*]")
                .getOrCreate();

        // Get DataFrameReader using SparkSession
        DataFrameReader dataFrameReader = session.read();

        // Set header option to true to specify that first row in file contains - name of columns
        dataFrameReader.option("header", "true");
        Dataset<Row> csvDataFrame = dataFrameReader.csv("src/main/resources/data.csv");

        // Print Schema to see column names, types and other metadata
        csvDataFrame.printSchema();

        // Create view and execute query to convert types as, by default, all columns have string types
        csvDataFrame.createOrReplaceTempView("ROOM_OCCUPANCY_RAW");
        Dataset<Row> roomOccupancyData = session
            .sql(
              "SELECT  CAST( id as int               ) id," +
                            "CAST(  date as string         ) date," +
                            "CAST(  Temperature as float   ) Temperature," +
                            "CAST(  Humidity as float      ) Humidity," +
                            "CAST(  Light as float         ) Light," +
                            "CAST(  CO2 as float           ) CO2," +
                            "CAST(  HumidityRatio as float ) HumidityRatio," +
                            "CAST(  Occupancy as int       ) Occupancy " +
                        "FROM ROOM_OCCUPANCY_RAW"
            );

        // Print Schema to see column names, types and other metadata
        roomOccupancyData.printSchema();

        // Create view to execute query to get filtered data
        roomOccupancyData.createOrReplaceTempView("ROOM_OCCUPANCY_FILTERED");
        session.sql(
                "SELECT * " +
                        "FROM ROOM_OCCUPANCY_FILTERED " +
                        "WHERE temperature >= 23.6 " +
                        "AND humidity > 27 " +
                        "AND CO2 BETWEEN 920 and 950"
                ).show();

        //    +---+-------------------+-----------+--------+------+-----+-------------+---------+
        //    | id|               date|Temperature|Humidity| Light|  CO2|HumidityRatio|Occupancy|
        //    +---+-------------------+-----------+--------+------+-----+-------------+---------+
        //    |162|2015-02-02 14:41:00|       23.6|  27.236| 498.4|925.2|  0.004910462|        1|
        //    |163|2015-02-02 14:42:00|       23.6|   27.29| 530.2|929.4|  0.004920275|        1|
        //    |164|2015-02-02 14:43:00|       23.6|   27.33| 533.6|936.4|  0.004927544|        1|
        //    |165|2015-02-02 14:44:00|       23.6|   27.34|524.25|950.0|  0.004929361|        1|
        //    +---+-------------------+-----------+--------+------+-----+-------------+---------+

    }


}
