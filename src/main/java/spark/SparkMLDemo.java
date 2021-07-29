package spark;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.ml.clustering.KMeans;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.regression.LinearRegression;
import org.apache.spark.sql.*;
import org.apache.spark.sql.execution.columnar.ColumnBuilder;
import org.jdesktop.swingx.table.ColumnFactory;
import scala.tools.nsc.transform.patmat.Solving;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SparkMLDemo {

    public static void main(String[] args) {
        Logger.getLogger("org").setLevel(Level.ERROR);

        // Create Spark Session to create connection to Spark
        System.out.println("===================== Create Spark Session =============================================\n\n");
        SparkSession session = SparkSession.builder()
                .appName("AirBNB Analysis")
                .master("local[4]")
                .getOrCreate();


        // Get DataFrameReader using SparkSession
        System.out.println("\n\n===================== Read CSV =============================================\n\n");
        DataFrameReader dataFrameReader = session.read();
        dataFrameReader.option("header", "true");

        Dataset<Row> df = dataFrameReader.csv("src/main/resources/listings.csv");

        // Print Schema to see column names, types and other metadata
        df.printSchema();

        // Select specific columns and Print the top 10 of the Data-Frame
        df = df.select("id", "name",  "minimum_nights", "room_type", "bedrooms", "number_of_reviews", "price");
        df.show(10);

        System.out.println("\nCount: " + df.count());



        //Ensure that the data-set field bedrooms is double and that it does not contain nulls
        System.out.println("\n\n===================== Feature Engineering =============================================");
        df = df.withColumn("id", df.col("id"))
                .filter(df.col("id").isNotNull());

        df = df.withColumn("minimum_nights", df.col("minimum_nights").cast("double"))
                .filter(df.col("minimum_nights").isNotNull());

        df = df.withColumn("number_of_reviews", df.col("number_of_reviews").cast("double"))
                .filter(df.col("number_of_reviews").isNotNull());

        df = df.withColumn("minimum_nights", df.col("minimum_nights").cast("double"))
                .filter(df.col("minimum_nights").isNotNull());

        df = df.withColumn("bedrooms", df.col("bedrooms").cast("double"))
                .filter(df.col("bedrooms").isNotNull());

        df = df.withColumn("price", df.col("price").cast("double"))
                .filter(df.col("price").isNotNull());

        df.show();
        //    +-----+--------------------+--------------+---------------+--------+-----------------+-----+
        //    |   id|                name|minimum_nights|      room_type|bedrooms|number_of_reviews|price|
        //    +-----+--------------------+--------------+---------------+--------+-----------------+-----+
        //    |  958|Bright, Modern Ga...|           2.0|Entire home/apt|     3.0|            277.0|150.0|
        //    | 5858|  Creative Sanctuary|          30.0|Entire home/apt|     3.0|            111.0|195.0|
        //    | 7918|A Friendly Room -...|          32.0|   Private room|     1.0|             19.0| 56.0|
        //    | 8142|Friendly Room Apt...|          32.0|   Private room|     1.0|              8.0| 56.0|
        //    | 8339|Historic Alamo Sq...|           7.0|Entire home/apt|     3.0|             28.0|795.0|
        //    |10578|Classic Nob Hill ...|          30.0|Entire home/apt|     3.0|             18.0|120.0|
        //    |24723|Guest Suite in Pa...|           5.0|Entire home/apt|     3.0|            332.0|199.0|
        //    |25094|San Francisco's L...|           5.0|   Private room|     1.0|             42.0|150.0|
        //    +-----+--------------------+--------------+---------------+--------+-----------------+-----+




        // Randomly Split the Dataset to 80 % Train Data and 20 % test Data
        System.out.println("\n\n================ Randomly Split the Dataset to 80-20 ==============================");
        double[] split = {0.8, 0.2 };
        Dataset<Row>[] dfArray = df.randomSplit(split, 42);

        // Getting the Train Dataset
        Dataset<Row> trainData = dfArray[0];
        Dataset<Row> testData = dfArray[1];

        trainData.show();

        // Create the Vector Assembler That will contain the feature columns -- Dependent Variables
        System.out.println("\n\n===================== Transform the Train Data using VectorAssembler ===========================");
        var vectorAssembler = new VectorAssembler();
        vectorAssembler.setInputCols(new String[]{ "minimum_nights", "bedrooms", "number_of_reviews" });
        vectorAssembler.setOutputCol("features");

        // Transform the Train Dataset using vectorAssembler.transform
        Dataset<Row> trainDataTransformed = vectorAssembler.transform(trainData.na().drop());
        trainDataTransformed.printSchema();
        trainDataTransformed.select ( "features", "price").show(10);

        Dataset<Row> testDataTransformed = vectorAssembler.transform(testData.na().drop());
        testDataTransformed.printSchema();
        testDataTransformed.select ( "features", "price").show(10);
        //    +---------------+-----+
        //    |       features|price|
        //    +---------------+-----+
        //    |[30.0,1.0,17.0]| 71.0|
        //    | [3.0,2.0,57.0]|289.0|
        //    |[2.0,1.0,306.0]| 99.0|
        //    +---------------+-----+



        // Create a LinearRegression Estimator and set the feature column and the label column
        // Call linearRegression.fit(trainData) to return a LinearRegressionModel Object
        System.out.println("\n\n===================== Linear Regression ===========================");
        LinearRegression linearRegression = new LinearRegression();
        linearRegression.setFeaturesCol("features");
        linearRegression.setLabelCol("price");

        var model = linearRegression.fit(trainDataTransformed);
        System.out.println("Model Mean Squared Error:  " + model.summary().meanSquaredError());


        double coefficient = Math.round(model.coefficients().toArray()[0]);
        double intercept   = Math.round(model.intercept ());
        System.out.println("The formula for the linear regression line is price = " + coefficient + "*bedrooms + " + intercept);

        System.out.println("Prediction: ");
        Dataset<Row> predictions = model.transform(testDataTransformed);
        predictions.show();
        //    +--------+--------------------+--------------+---------------+--------+-----------------+------+---------------+------------------+
        //    |      id|                name|minimum_nights|      room_type|bedrooms|number_of_reviews| price|       features|        prediction|
        //    +--------+--------------------+--------------+---------------+--------+-----------------+------+---------------+------------------+
        //    | 1001422|Charming Flat in ...|          31.0|Entire home/apt|     3.0|              6.0| 200.0| [31.0,3.0,6.0]|257.99864263420386|
        //    | 1001759|Spacious sunny ro...|          30.0|   Private room|     1.0|             13.0|  89.0|[30.0,1.0,13.0]|208.93491102849364|
        //    | 1003756|AIRBNB EMPLOYEES:...|           2.0|Entire home/apt|     3.0|             25.0| 273.0| [2.0,3.0,25.0]| 282.9679407357564|
        //    |10071932|Carriage House:  ...|          60.0|Entire home/apt|     2.0|              2.0| 110.0| [60.0,2.0,2.0]|202.30247673799337|
        //    |10099733|2 Twin beds with ...|           5.0|   Private room|     1.0|             77.0|  99.0| [5.0,1.0,77.0]| 207.2509255846636|
        //    +--------+--------------------+--------------+---------------+--------+-----------------+------+---------------+------------------+

    }


}
