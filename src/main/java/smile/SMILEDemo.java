package smile;

import org.apache.commons.csv.CSVFormat;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import smile.data.DataFrame;
import smile.data.formula.Formula;
import smile.data.measure.NominalScale;
import smile.data.vector.IntVector;
import smile.io.*;
import smile.plot.swing.Histogram;
import smile.regression.RandomForest;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class SMILEDemo {

    public static void main(String[] args) {

        DataFrame trainData = readCSV("src/main/resources/titanic_train.csv", true);
        System.out.println(trainData.structure());
        System.out.println(trainData.summary());


        System.out.println ("======= Encoding Non Numeric Data ==============");
        trainData = trainData.merge(IntVector.of("Gender", encodeCategory(trainData, "Sex")));
        trainData = trainData.merge(IntVector.of("Passenger Class", encodeCategory(trainData, "Pclass")));
        System.out.println(trainData.structure());

        System.out.println ("============== Dropping the Name, Sex, and Pclass Columns  ==============");
        trainData = trainData.drop("Name");
        trainData = trainData.drop("Sex");
        trainData = trainData.drop("Pclass");
        System.out.println(trainData.structure());
        System.out.println(trainData.summary());

        System.out.println ("============== After Omitting null Rows ==============");
        trainData = trainData.omitNullRows();
        System.out.println(trainData.summary());

        System.out.println ("============== Titanic EDA ==============");
        eda(trainData);


        System.out.println ("============== Train Model ==============");
        RandomForest model = RandomForest.fit(Formula.lhs("Survived"), trainData);

        System.out.println("RandomForest Model Schema: " + model.schema());
        System.out.println("Feature Importance: " + Arrays.toString(model.importance()));
        System.out.println("Metrics: " + model.metrics());




        System.out.println ("============== Test Data ==============");
        DataFrame testData = readCSV("src/main/resources/titanic_test.csv", false);

        // "Pclass" is already encoded in the DataSet, we will just change its name to be "Passenger Class" like the trainData
        testData = testData.merge(IntVector.of("Gender", encodeCategory(testData, "Sex")));
        testData = testData.merge(IntVector.of("Passenger Class", testData.column("Pclass").toIntArray()));

        // The Independent Variable "Survived" is already dropped in the DataSet
        testData = testData.drop("Name");
        testData = testData.drop("Sex");
        testData = testData.drop("Pclass");

        testData = testData.omitNullRows();

        double[] predict = model.predict(testData);
        double[][] test = model.test(testData);

    }


    // Private Methods  ---------------------------------------------------------
    private static DataFrame readCSV(String fileName, boolean isTrainData){
        CSVFormat format = CSVFormat.DEFAULT.withFirstRecordAsHeader();

        try
        {
            DataFrame  df = Read.csv(fileName, format);
            System.out.println(df.summary());

            if (isTrainData)
                df = df.select("Name", "Pclass", "Age", "Sex", "Survived");     // Train data
            else
                df = df.select("Name", "Pclass", "Age", "Sex");                 // Test data

            System.out.println(df.summary());

            return df;

        } catch (IOException | URISyntaxException e) {
            return null;
        }

    }

    private static int[] encodeCategory(DataFrame df, String columnName){
        String[] values = df.stringVector(columnName)
                .distinct()
                .toArray(new String[]{});

        int[] pclassValues = df.stringVector(columnName)
                .factorize(new NominalScale(values))
                .toIntArray();

        return pclassValues;
    }

    private static void eda(DataFrame df){
        df.summary();

        DataFrame survived = DataFrame.of(df.stream()
                .filter(t -> t.get("Survived").equals(1)));

        DataFrame notSurvived = DataFrame.of(df.stream()
                .filter(t -> t.get("Survived").equals(0)));

        survived = survived.omitNullRows ();
        survived.summary ();

        notSurvived = notSurvived.omitNullRows();
        notSurvived.summary();
        System.out.println ("Survived Size: " + survived.size() + " - Survived Size: " +  notSurvived.size());


        Double averageAge = survived.stream()
                .mapToDouble (t -> t.isNullAt ("Age")   ?   0.0  :   t.getDouble ("Age"))
                .average ()
                .orElse (0);

        System.out.println("Average Age: " + averageAge.intValue());



        Map map = survived.stream ()
                .collect(Collectors.groupingBy (
                        t -> Double.valueOf(t.getDouble ("Age")).intValue(),
                        Collectors.counting()
                    )
                );

        double[] breaks = ((Collection<Integer>) map.keySet ())
                .stream ()
                .mapToDouble (l -> Double.valueOf (l))
                .toArray ();

        int[] valuesInt = ((Collection<Long>) map.values ())
                .stream ().mapToInt (i -> i.intValue ())
                .toArray ();


        try
        {
            Histogram.of(survived.doubleVector("Age").toDoubleArray (), 15, false)
                    .canvas ().setAxisLabels ("Age", "Count")
                    .setTitle ("Age frequencies among surviving passengers")
                    .window ();
            Histogram.of (survived.intVector("Passenger Class").toIntArray (), 4, true)
                    .canvas ().setAxisLabels("Classes", "Count")
                    .setTitle ("Pclass values frequencies among surviving passengers")
                    .window ();

            // Histogram.of(values, map.size(), false).canvas().window();

            System.out.println (survived.schema());

        } catch (InterruptedException | InvocationTargetException e) { }
    }

}


