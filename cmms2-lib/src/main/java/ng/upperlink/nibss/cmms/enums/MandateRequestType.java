/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ng.upperlink.nibss.cmms.enums;

import ng.upperlink.nibss.cmms.dto.mandates.MandateCategoryResponse;
import ng.upperlink.nibss.cmms.dto.mandates.MandateRequest;
import ng.upperlink.nibss.cmms.dto.mandates.MandateTypeResponse;
import ng.upperlink.nibss.cmms.model.MandateType;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Megafu Charles <noniboycharsy@gmail.com>
 */
public enum MandateRequestType {
    FIXED("Fixed"),
    VARIABLE("Variable");

    private String value;

    MandateRequestType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }

    public static MandateRequestType findByValue (String value)
    {
        for (MandateRequestType mandateRequestType : MandateRequestType.values())
        {
            if (mandateRequestType.value.equalsIgnoreCase(value)) return mandateRequestType;
        }
        return null;
    }
    public static synchronized MandateRequestType find(String type){
        try{
            return MandateRequestType.valueOf(type);
        }catch (Exception ex){
            return findByValue(type);
        }
    }

    public static List<MandateTypeResponse> getMandateTypes() {
        List<MandateTypeResponse> responseList = new ArrayList<>();
        int index = 1;
        for(MandateRequestType mType : MandateRequestType.values()) {
            MandateTypeResponse response = new MandateTypeResponse();
            response.setMandateType(mType.getValue());
            response.setId(index++);

            responseList.add(response);
        }
        return responseList;
    }
}
