package ng.upperlink.nibss.cmms.util;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class MandateUtils {

    public static synchronized String getMandateCode(String num,String billerNo, String productId){

        num = num.substring(5);
        List<String> maxNumList = new ArrayList<>();
        maxNumList.add(num);

        billerNo= StringUtils.leftPad(billerNo, 2, "0");
        productId= StringUtils.leftPad(productId, 3, "0");

        if(maxNumList !=null && maxNumList.size()>0 && maxNumList.get(0)!=null){

            String mandateCode = billerNo + "/" + productId + "/" + String.valueOf(maxNumList.stream().findFirst().get());
            return mandateCode;
        }
        else return null;
    }

}
