import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Center1D {
    Double center;
    List<Integer> points;
    public Center1D(Double center){
        this.center = center;
        this.points = Collections.synchronizedList(new ArrayList<Integer>());
    }

    public void addPoint(int point){
        points.add(point);
    }

    public void clearPoints(){
        points.clear();
    }

    public double adjustCenter(){
        if(points.size() == 0){
            return center;
        }
        int sum = 0;
        for (int point : points) {
            sum += point;
        }
        center = (double) sum/points.size();
        return center;
    }
}
