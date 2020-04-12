import java.util.List;
import java.util.concurrent.Callable;

public class MoveCentersCallableMultiD implements Callable<Boolean> {
    List<List<Double>> centers;
    List<CenterMultiD> centerObjs;
    int start;
    int end;

    public MoveCentersCallableMultiD(List<List<Double>> centers, List<CenterMultiD> centerObjs, int start, int end){
        this.centers = centers;
        this.centerObjs = centerObjs;
        this.start = start;
        this.end = end;
    }

    @Override
    public Boolean call() throws Exception {
        List<Double> new_center;
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
