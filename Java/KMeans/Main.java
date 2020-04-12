import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        KMeans km = new KMeans(4, 10,4);
        //List<List<Integer>> points = new ArrayList<List<Integer>>();
        List<Integer> points = new ArrayList<Integer>();
        for(int i = 0; i < 20; i++){
            //List<Integer> temp = new ArrayList<Integer>();
            //temp.add(i);
            //temp.add(i);
            //points.add(temp);
            points.add(i);
        }
        km.fit(points);
        //List<List<Double>> centers = km.getCenters();
        List<?> centers = km.getCenters();
        /*
        for(int i = 0; i < centers.size(); i++){
            System.out.print("(");
            for(int j = 0; j < centers.get(i).size(); j++){
                System.out.print(centers.get(i).get(j) + " ");
            }
            System.out.print(") ");
        }*/
        for(int i = 0; i < centers.size(); i++){
            System.out.print(centers.get(i) + " ");
        }
        System.out.println();
        List<Integer> pred = km.predict(points);

        for(int i = 0; i < pred.size(); i++){
            System.out.print(pred.get(i) + " ");
        }
        System.out.println();
    }
}

