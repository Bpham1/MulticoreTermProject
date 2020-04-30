package examples;

import datascilib.Utils.CSVReaderWriter;

import java.util.List;

public class ReadCSV {
    public static void main(String[] args){
        CSVReaderWriter csvWR = new CSVReaderWriter();
        boolean readSuccess = csvWR.read("./src/sampleinputs/SampleInput.csv");
        assert readSuccess;
        List<List<Double>> train_X = csvWR.getColumnRangeAsDouble("Feat 1", "Feat 3");
        List<Integer> train_Y = csvWR.getColumnAsInt("Y");
        System.out.println("train_X");
        for(List<Double> row: train_X){
            for(Double ele: row){
                System.out.print(ele + " ");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println("train_Y");
        for(Integer ele: train_Y){
            System.out.println(ele);
        }
    }
}
