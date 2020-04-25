import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVReaderWriter {
    private BufferedReader br;
    private List<String> featureNames;
    private List<List<String>> data;

    public CSVReaderWriter(){
        br = null;
        featureNames = new ArrayList<String>();
        data = new ArrayList<List<String>>();
    }

    public boolean read(String filePath){
        try {
            br = new BufferedReader(new FileReader(filePath));
            String line;
            int lineCount = 0;
            while((line = br.readLine()) != null){
                String[] values = line.split(",");
                if(lineCount == 0){
                    featureNames = Arrays.asList(values);
                } else {
                    data.add(Arrays.asList(values));
                }
                lineCount++;
            }
            br.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public List<String> getColumn(String colName){
        boolean columnFound = false;
        int columnIdx = 0;
        while(!columnFound && columnIdx < featureNames.size()){
            if(featureNames.get(columnIdx).equals(colName)){
                columnFound = true;
            } else {
                columnIdx++;
            }
        }
        if(columnFound){
            List<String> retColumn = new ArrayList<String>();
            for (List<String> datum : data) {
                retColumn.add(datum.get(columnIdx));
            }
            return retColumn;
        } else {
            return null;
        }
    }

    public List<Integer> getColumnAsInt(String colName){
        boolean columnFound = false;
        int columnIdx = 0;
        while(!columnFound && columnIdx < featureNames.size()){
            if(featureNames.get(columnIdx).equals(colName)){
                columnFound = true;
            } else {
                columnIdx++;
            }
        }
        if(columnFound){
            List<Integer> retColumn = new ArrayList<Integer>();
            for (List<String> datum : data) {
                retColumn.add(Integer.parseInt(datum.get(columnIdx)));
            }
            return retColumn;
        } else {
            return null;
        }
    }

    public List<Double> getColumnAsDouble(String colName){
        boolean columnFound = false;
        int columnIdx = 0;
        while(!columnFound && columnIdx < featureNames.size()){
            if(featureNames.get(columnIdx).equals(colName)){
                columnFound = true;
            } else {
                columnIdx++;
            }
        }
        if(columnFound){
            List<Double> retColumn = new ArrayList<Double>();
            for (List<String> datum : data) {
                retColumn.add(Double.parseDouble(datum.get(columnIdx)));
            }
            return retColumn;
        } else {
            return null;
        }
    }

    public List<List<String>> getColumnRange(String startCol, String endCol){
        boolean startColFound = false;
        boolean endColFound = false;
        int startColIdx = 0;
        int endColIdx = 0;
        int columnIdx = 0;
        while((!startColFound || !endColFound) && columnIdx < featureNames.size()){
            if(featureNames.get(columnIdx).equals(startCol)){
                startColFound = true;
                startColIdx = columnIdx;
            } else if(featureNames.get(columnIdx).equals(endCol)){
                endColFound = true;
                endColIdx = columnIdx;
            }
            columnIdx++;
        }
        if(startColFound && endColFound && startColIdx <= endColIdx){
            List<List<String>> retColumns = new ArrayList<List<String>>();
            for (List<String> datum : data) {
                retColumns.add(datum.subList(startColIdx, endColIdx));
            }
            return retColumns;
        } else {
            return null;
        }
    }

    public List<List<Double>> getColumnRangeAsDouble(String startCol, String endCol){
        boolean startColFound = false;
        boolean endColFound = false;
        int startColIdx = 0;
        int endColIdx = 0;
        int columnIdx = 0;
        while((!startColFound || !endColFound) && columnIdx < featureNames.size()){
            if(featureNames.get(columnIdx).equals(startCol)){
                startColFound = true;
                startColIdx = columnIdx;
            } else if(featureNames.get(columnIdx).equals(endCol)){
                endColFound = true;
                endColIdx = columnIdx + 1;
            }
            columnIdx++;
        }
        if(startColFound && endColFound && startColIdx <= endColIdx){
            List<List<Double>> retColumns = new ArrayList<List<Double>>();
            for (List<String> datum : data) {
                List<String> subList = datum.subList(startColIdx, endColIdx);
                List<Double> convertedRow = new ArrayList<Double>();
                for(String val: subList){
                    convertedRow.add(Double.parseDouble(val));
                }
                retColumns.add(convertedRow);
            }
            return retColumns;
        } else {
            return null;
        }
    }

    public boolean write(String filePath, List<String> featureNames, List data){
        try {
            FileWriter csvWriter = new FileWriter(filePath);
            csvWriter.append(String.join(",", featureNames));
            csvWriter.append("\n");
            for(Object row: data){
                if(!(row instanceof List)){
                    throw new IllegalArgumentException("Data should be of form List<List>");
                }
                List<String> stringRow = new ArrayList<String>();
                for(Object ele: (List) row){
                    if(ele instanceof Integer){
                        stringRow.add(String.valueOf((int) ele));
                    } else if (ele instanceof Double){
                        stringRow.add(String.valueOf((double) ele));
                    } else if (ele instanceof String){
                        stringRow.add((String) ele);
                    } else {
                        throw new IllegalArgumentException("Data contains a element that is not a Integer, Double, or String");
                    }
                }
                csvWriter.append(String.join(",", stringRow));
                csvWriter.append("\n");
            }
            csvWriter.flush();
            csvWriter.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public List<String> getFeatureNames() {
        return featureNames;
    }
}
