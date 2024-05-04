package ng.upperlink.nibss.cmms.utils;

import ng.upperlink.nibss.cmms.model.Mandate;
import ng.upperlink.nibss.cmms.service.mandateImpl.MandateService;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class MandateUtils {

    public static synchronized String getMandateCode(Long maxNum,String billerNo, String productId){
        if(maxNum == null){
            maxNum = 0L;
        }
        List<Long> maxNumList = new ArrayList<>();
        maxNumList.add(maxNum);

        billerNo= StringUtils.leftPad(billerNo, 5, "0");
        productId= StringUtils.leftPad(productId, 3, "0");

        if(maxNumList !=null && maxNumList.size()>0 && maxNumList.get(0)!=null){

            return billerNo+"/"+productId+"/"+StringUtils.leftPad(String.valueOf(maxNumList.stream().findFirst().get()), 6, "0");
        }
        else return null;
    }
}
