import java.util.List;
import java.util.concurrent.Callable;

public class MoveCentersCallable implements Callable<Boolean> {
    List<Double> centers;
    List<Center1D> centerObjs;
    int start;
    int end;

    public MoveCentersCallable(List<Double> centers, List<Center1D> centerObjs, int start, int end){
        this.centers = centers;
        this.centerObjs = centerObjs;
        this.start = start;
        this.end = end;
    }

    @Override
    public Boolean call() throws Exception {
        double new_center;
        boolean changed = false;
        for (int i = start; i < end; i++){
            new_center = centerObjs.get(i).adjustCenter();
            if(new_center != centers.get(i)){
                changed = true;
            }
            centers.set(i, new_center);
            centerObjs.get(i).clearPoints();
        }
        return changed;
    }
}
