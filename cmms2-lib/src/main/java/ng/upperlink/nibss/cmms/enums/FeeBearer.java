package ng.upperlink.nibss.cmms.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum FeeBearer {
    NONE("None"),
    SUBSCRIBER("Subscriber"),
    BILLER("Biller"),
    BANK("Bank");

    private String value;

    FeeBearer(String value) {
        this.value = value;
    }

    public String getValue() {
        return  value;
    }

    public static List<Map<FeeBearer,String>> getAll(){
        Map<FeeBearer,String> feeMap = new HashMap<>();
        List<Map<FeeBearer,String>> list = null;
        for(FeeBearer feeBearer : FeeBearer.values()){
            list = new ArrayList<>();
            feeMap.put(feeBearer,feeBearer.getValue());
            list.add(feeMap);
        }

        return list;
    }

    public static synchronized FeeBearer find(String feeBearer) {
        try {
            return FeeBearer.valueOf(feeBearer);
        } catch (Exception e) {
            return findByValue(feeBearer);
        }
    }

    private static FeeBearer findByValue(String value) {
        FeeBearer type = null;

        for (FeeBearer feeBearer : FeeBearer.values()) {
            if( feeBearer.value.equalsIgnoreCase(value)) {
                return feeBearer;
            }
        }
        return  null;

    }

}
