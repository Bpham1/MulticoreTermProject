package datascilib.Clustering.KMeans;

import java.util.List;

public class AssignPointsRunnableMultiD implements Runnable {
    List<List<Double>> points;
    List<Integer> prediction;
    List<CenterMultiD> centerObjs;
    int start;
    int end;
    public AssignPointsRunnableMultiD(List<List<Double>> points, List<CenterMultiD> centerObjs, List<Integer> prediction, int start, int end){
        this.points = points;
        this.prediction = prediction;
        this.centerObjs = centerObjs;
        this.start = start;
        this.end = end;
    }

    @Override
    public void run(){
        // O(PCD/T)
        for(int i = start; i < end; i++){
            // O(CD)
            int closestCenter = getClosestCenter(points.get(i));
            centerObjs.get(closestCenter).addPoint(points.get(i));
            prediction.set(i, closestCenter);
        }
    }

    private int getClosestCenter(List<Double> point){
        // O(CD)
        int closestCenterIdx = -1;
        double closestDist = 0;
        for(int i = 0; i < centerObjs.size(); i++){
            double dist = 0;
            for(int j = 0; j < point.size(); j++){
                double center_temp = centerObjs.get(i).center.get(j);
                double point_temp = (double) point.get(j);
                dist += Math.pow(point_temp - center_temp, 2);
            }
            if(closestCenterIdx == -1 || closestDist > dist){
                closestCenterIdx = i;
                closestDist = dist;
            }
        }
        return closestCenterIdx;
    }
}
