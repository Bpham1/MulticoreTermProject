import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args){
        CSVReaderWriter csvreadwrter = new CSVReaderWriter();
        System.out.println(csvreadwrter.read("SampleInput.csv"));
        List<Integer> labels = csvreadwrter.getColumnAsInt("Y");
        List<List<Double>> data = csvreadwrter.getColumnRangeAsDouble("Feat 1", "Y");
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
        csvreadwrter.write("SampleInput2.csv", csvreadwrter.getFeatureNames(), data);
        List<String> testLabels = new ArrayList<String>();
        testLabels.add("A");
        testLabels.add("B");
        testLabels.add("C");
        for(String ele: testLabels){
            System.out.print(ele + " ");
        }
        System.out.println();
        LabelEnumerator le = new LabelEnumerator(testLabels);
        List<Integer> enumeratedLabels = le.enumerateLabels(testLabels);
        for(Integer ele: enumeratedLabels){
            System.out.print(ele + " ");
        }
        System.out.println();
        testLabels = le.labelEnumerates(enumeratedLabels);
        for(String ele: testLabels){
            System.out.print(ele + " ");
        }
        System.out.println();
    }
}
