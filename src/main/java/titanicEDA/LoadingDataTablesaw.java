package titanicEDA;

import tech.tablesaw.api.*;
import tech.tablesaw.columns.Column;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoadingDataTablesaw {

    // Loading Data From CSV Using Tablesaw
    public static Table loadDataFromCSV(String fileName){
        try
        {
            Table titanicData = Table.read().csv(fileName);

            return titanicData;
        }
        catch (IOException e) {
            return null;
        }
    }

    // Loading Data From JSON
    public static Table loadDataFromJSON(String fileName){
        try
        {
            Table titanicData = Table.read().file(fileName);

            return titanicData;
        }
        catch (IOException e) {
            return null;
        }
    }

    // Get the structure of the data
    public static String getDataInfoStructure(Table data) {
        Table dataStructure = data.structure();

        return dataStructure.toString();
    }

    // Get Data Summary
    public static String getDataSummary(Table data){
        Table summary = data.summary();

        return summary.toString();
    }

    //Adding Columns to the data-table
    public static Table addDateColumnToData(Table data){
        List<LocalDate> dateList = new ArrayList<>();

        for (int i = 0; i < data.rowCount(); i++)
            dateList.add( LocalDate.of(2021,  i%12==0 ? 1 : i%12 , i%28==0 ? 1 : i%28) );

        DateColumn dateColumn = DateColumn.create("Fake Date", dateList);

        data.insertColumn(data.columnCount(), dateColumn);

        return data;
    }

    // Mapping a TEXT column to a NUMERIC valued column  [column 'SEX']  --> female=1, male=0 --> then adding a column named gender
    public static Table mapTextColumnToNumber(Table data){
        List<Number> mappedValues = new ArrayList<>();

        var gender = (StringColumn)data.column("Sex");

        for (String item : gender) {
            if (item != null && item.toLowerCase().equals("female"))
                mappedValues.add(1d);     // .add (new Double (1));   # The same
            else
                mappedValues.add(0d);     // .add (new Double (0));   # The same
        }

        NumberColumn mappedGenderColumn = DoubleColumn.create("gender", mappedValues);
        data.addColumns(mappedGenderColumn);

        return data;
    }

}
