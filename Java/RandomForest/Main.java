import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        List<List<Double>> X = new ArrayList<List<Double>>();
        List<Double> row0 = new ArrayList<Double>(Arrays.asList(1.0, 2.0, 3.0));
        List<Double> row1 = new ArrayList<Double>(Arrays.asList(4.0, 5.0, 6.0));
        List<Double> row2 = new ArrayList<Double>(Arrays.asList(7.0, 8.0, 9.0));
        List<Double> row3 = new ArrayList<Double>(Arrays.asList(10.0, 11.0, 12.0));
        List<Double> row4 = new ArrayList<Double>(Arrays.asList(13.0, 14.0, 15.0));
        List<Double> row5 = new ArrayList<Double>(Arrays.asList(16.0, 17.0, 18.0));
        List<Double> row6 = new ArrayList<Double>(Arrays.asList(19.0, 20.0, 21.0));
        List<Double> row7 = new ArrayList<Double>(Arrays.asList(22.0, 23.0, 24.0));
        X.add(row0);
        X.add(row1);
        X.add(row2);
        X.add(row3);
        X.add(row4);
        X.add(row5);
        X.add(row6);
        X.add(row7);
        List<Integer> Y = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));
        DescisionTreeEstimator dte = new DescisionTreeEstimator(4);
        dte.fit(X, Y);
    }
}

