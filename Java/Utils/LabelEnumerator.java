import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LabelEnumerator {
    private List<String> stringLabels;
    private List<Integer> enumeratedLabels;
    private HashMap<Integer, String> mappingInt2String;
    private HashMap<String, Integer> mappingString2Int;
    public LabelEnumerator(){
        stringLabels = null;
        enumeratedLabels = null;
        mappingInt2String = null;
        mappingString2Int = null;
    }
    public LabelEnumerator(List<String> labels){
        stringLabels = labels;
        enumeratedLabels = null;
        mappingInt2String = null;
        mappingString2Int = null;
    }
    public List<Integer> enumerateLabels(List<String> labels){
        stringLabels = labels;
        mappingInt2String = new HashMap<Integer, String>();
        mappingString2Int = new HashMap<String, Integer>();
        int enumeration = 0;
        List<Integer> ret = new ArrayList<Integer>();
        for(String label: labels){
            if(!mappingString2Int.containsKey(label)){
                mappingString2Int.put(label, enumeration);
                mappingInt2String.put(enumeration, label);
                enumeration++;
            }
            ret.add(mappingString2Int.get(label));
        }
        return ret;
    }
    public List<String> labelEnumerates(List<Integer> enumeratedLabels){
        List<String> labels = new ArrayList<String>();
        for(int eNum: enumeratedLabels){
            labels.add(mappingInt2String.get(eNum));
        }
        return labels;
    }
}
