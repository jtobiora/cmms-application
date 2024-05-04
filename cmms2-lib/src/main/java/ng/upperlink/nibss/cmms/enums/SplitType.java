package ng.upperlink.nibss.cmms.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum SplitType {
    NONE("None"),
    FIXED("Fixed"),
    PERCENTAGE("Percentage");

    private String value;

    SplitType(String value) {
        this.value = value;
    }

    public String getValue() {
        return  value;
    }

    public static synchronized SplitType find(String splitType) {
        try {
            return SplitType.valueOf(splitType);
        } catch (Exception e) {
            return findByValue(splitType);
        }
    }

    private static SplitType findByValue(String value) {
        SplitType type = null;

        for (SplitType splitType : SplitType.values()) {
            if( splitType.value.equalsIgnoreCase(value)) {
                return splitType;
            }
        }
        return  null;

    }

    public static List<Map<SplitType,String>> getAll(){
        Map<SplitType,String> splitTypeMap = new HashMap<>();
        List<Map<SplitType,String>> list = null;
        for(SplitType splitType : SplitType.values()){
            list = new ArrayList<>();
            splitTypeMap.put(splitType,splitType.getValue());
            list.add(splitTypeMap);
        }

        return list;
    }

}
