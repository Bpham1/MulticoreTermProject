import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CenterMultiD {
    List<Double> center;
    List<List<Double>> points;

    public CenterMultiD(List<Double> center){
        this.center = center;
        this.points = Collections.synchronizedList(new ArrayList<List<Double>>());
    }

    public void addPoint(List<Double> point){
        points.add(point);
    }

    public void clearPoints(){
        points.clear();
    }

    public List<Double> adjustCenter(){
        // O(PD)
        if(points.size() == 0){
            return center;
        }
        List<Double> sum = new ArrayList<Double>();
        for(int i = 0; i < points.get(0).size(); i++){
            sum.add(0.0);
        }
        for (List<Double> point : points) {
            for(int i = 0; i < sum.size(); i++){
                sum.set(i, sum.get(i) + (double) point.get(i));
            }
        }
        for(int i = 0; i < sum.size(); i++){
            sum.set(i, sum.get(i) / points.size());
        }
        center = sum;
        return center;
    }
}
