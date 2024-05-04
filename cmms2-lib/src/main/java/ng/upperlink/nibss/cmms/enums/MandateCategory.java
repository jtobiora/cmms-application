package ng.upperlink.nibss.cmms.enums;

import ng.upperlink.nibss.cmms.dto.mandates.MandateCategoryResponse;
import ng.upperlink.nibss.cmms.dto.mandates.MandateFrequencyResponse;

import java.util.ArrayList;
import java.util.List;

public enum MandateCategory {
    PAPER("Paper Mandate"),
    EMANDATE("E-Mandate");

    private String value;

    MandateCategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static MandateCategory findByValue (String value) {
        for (MandateCategory mandateCategory : MandateCategory.values())
        {
            if (mandateCategory.value.equalsIgnoreCase(value)) return mandateCategory;
        }
        return null;
    }
    public static synchronized MandateCategory find(String mandateCategory){
        try{
            return MandateCategory.valueOf(mandateCategory);
        }catch (Exception ex){
            return findByValue(mandateCategory);
        }
    }

    public static List<MandateCategoryResponse> getMandateCategories() {
        List<MandateCategoryResponse> responseList = new ArrayList<>();
        int index = 1;
        for(MandateCategory mCategory : MandateCategory.values()) {
            MandateCategoryResponse response = new MandateCategoryResponse();
            response.setMandateCategory(mCategory.getValue());
            response.setId(index++);

            responseList.add(response);
        }
        return responseList;
    }
}
