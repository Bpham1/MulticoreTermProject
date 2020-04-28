import java.util.List;

public class Main {
    public static void main(String[] args) {
        CSVReaderWriter csvreadwrter = new CSVReaderWriter();
        System.out.println(csvreadwrter.read("src/SampleInput.csv"));
        List<Integer> labels = csvreadwrter.getColumnAsInt("Y");
        List<List<Double>> data = csvreadwrter.getColumnRangeAsDouble("Feat 1", "Feat 3");
        for(Integer label: labels){
            System.out.print(label + " ");
        }
        System.out.println();
        for(List<Double> row: data){
            for(Double ele: row){
                System.out.print(ele + " ");
            }
            System.out.println();
        }
        SequentialGaussianBayesClassifier sgb = new SequentialGaussianBayesClassifier();
        ParallelGaussianBayesClassifier pgb = new ParallelGaussianBayesClassifier(29);
        sgb.fit(data, labels);
        pgb.fit(data, labels);
        List<Integer> pred = sgb.predict(data);
        for(Integer label: pred){
            System.out.print(label + " ");
        }
        System.out.println();
        List<Integer> pred2 = pgb.predict(data);
        for(Integer label: pred2){
            System.out.print(label + " ");
        }
        System.out.println();
    }
}
