package examples;

import datascilib.Utils.CSVReaderWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WriteCSV {
    public static void main(String[] args){
        CSVReaderWriter csvWR = new CSVReaderWriter();
        List<Integer> Feat1 = new ArrayList<Integer>(Arrays.asList(1,4,7));
        List<Integer> Feat2 = new ArrayList<Integer>(Arrays.asList(2,5,8));
        List<Integer> Feat3 = new ArrayList<Integer>(Arrays.asList(3,6,9));
        List<String> Y = new ArrayList<String>(Arrays.asList("A","B","C"));
        csvWR.addColumn("Feat1", Feat1);
        csvWR.addColumn("Feat2", Feat2);
        csvWR.addColumn("Feat3", Feat3);
        csvWR.addColumn("Y", Y);
        csvWR.write("./src/sampleinputs/SampleOutput.csv");
    }
}
