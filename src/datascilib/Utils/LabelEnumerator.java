package datascilib.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LabelEnumerator<T> {
    private List<T> stringLabels;
    private List<Integer> enumeratedLabels;
    private HashMap<Integer, T> mappingInt2String;
    private HashMap<T, Integer> mappingString2Int;
    public LabelEnumerator(){
        stringLabels = null;
        enumeratedLabels = null;
        mappingInt2String = null;
        mappingString2Int = null;
    }
    public LabelEnumerator(List<T> labels){
        stringLabels = labels;
        enumeratedLabels = null;
        mappingInt2String = null;
        mappingString2Int = null;
    }
    public List<Integer> enumerateLabels(List<T> labels){
        stringLabels = labels;
        mappingInt2String = new HashMap<Integer, T>();
        mappingString2Int = new HashMap<T, Integer>();
        int enumeration = 0;
        List<Integer> ret = new ArrayList<Integer>();
        for(T label: labels){
            if(!mappingString2Int.containsKey(label)){
                mappingString2Int.put(label, enumeration);
                mappingInt2String.put(enumeration, label);
                enumeration++;
            }
            ret.add(mappingString2Int.get(label));
        }
        return ret;
    }
    public List<T> labelEnumerates(List<Integer> enumeratedLabels){
        List<T> labels = new ArrayList<T>();
        for(int eNum: enumeratedLabels){
            labels.add(mappingInt2String.get(eNum));
        }
        return labels;
    }
}
