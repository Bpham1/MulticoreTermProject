package examples;

import datascilib.Utils.CSVReaderWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateLargeInput {
    public static void main(String[] args) {
        List<String> featureNames = new ArrayList<String>(Arrays.asList("Feat 1", "Feat 2", "Feat 3", "Feat 4", "Feat 5", "Feat 6", "Feat 7", "Feat 8", "Feat 9", "Feat 10", "Y"));
        int numRows = 100000;
        List<List> rows = new ArrayList<>();
        for (int i = 0; i < numRows; i++) {
            List row = new ArrayList();
            for(int j = 0; j < 10; j++){
                row.add((double) (j + (i*10)));
            }
            row.add(i/5);
            rows.add(row);
        }
        CSVReaderWriter csvReaderWriter = new CSVReaderWriter();
        csvReaderWriter.write("./src/sampleinputs/SuperExtremeInput.csv", featureNames, rows);
    }
}
