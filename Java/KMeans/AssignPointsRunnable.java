import java.util.List;

public class AssignPointsRunnable implements Runnable {
    List<Integer> points;
    List<Integer> prediction;
    List<Center1D> centerObjs;
    int start;
    int end;
    public AssignPointsRunnable(List<Integer> points, List<Center1D> centerObjs, List<Integer> prediction, int start, int end){
        this.points = points;
        this.prediction = prediction;
        this.centerObjs = centerObjs;
        this.start = start;
        this.end = end;
    }

    @Override
    public void run(){
        for(int i = start; i < end; i++){
            int closestCenter = getClosestCenter(points.get(i));
            centerObjs.get(closestCenter).addPoint(points.get(i));
            prediction.set(i, closestCenter);
        }
    }

    private int getClosestCenter(Integer point){
        int closestCenterIdx = -1;
        double closestDist = 0;
        for(int i = 0; i < centerObjs.size(); i++){
            if(closestCenterIdx == -1 || closestDist > Math.pow(Math.abs((double) point - centerObjs.get(i).center), 2)){
                closestCenterIdx = i;
                closestDist = Math.pow(Math.abs(point - centerObjs.get(i).center), 2);
            }
        }
        return closestCenterIdx;
    }
}
