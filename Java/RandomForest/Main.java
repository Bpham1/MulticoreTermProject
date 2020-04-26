import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args){
        CSVReaderWriter csvreadwrter = new CSVReaderWriter();
        System.out.println(csvreadwrter.read("C:\\Users\\brand\\IdeaProjects\\TermProject\\src\\SampleInput.csv"));
        List<Integer> labels = csvreadwrter.getColumnAsInt("Y");
        List<List<Double>> data = csvreadwrter.getColumnRangeAsDouble("Feat 1", "Feat 3");
        for(Integer label: labels){
            System.out.print(label + " ");
        }
        System.out.println();
        for(List<Double> row: data){
            for(Double ele: row){
                System.out.print(ele + " ");
            }
            System.out.println();
        }
        DescisionTreeEstimator dte = new DescisionTreeEstimator(4);
        dte.fit(data, labels);
        List<Integer> pred = dte.predict(data);
        for(Integer label: pred){
            System.out.print(label + " ");
        }
        System.out.println();
        RandomForest rf = new RandomForest(300, 4);
        rf.fit(data, labels);
        pred = rf.predict(data);
        for(Integer label: pred){
            System.out.print(label + " ");
        }
        System.out.println();
    }
}
