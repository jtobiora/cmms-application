package ng.upperlink.nibss.cmms.dto.search;

import lombok.Data;

import java.io.Serializable;

@Data
public class Search implements Serializable {

    private String param;

    private String status;

    public boolean getStatus(){
        return !isStatusEmpty() && status.equalsIgnoreCase("true");
    }

    public String getParam(){
        return "%"+param+"%";
    }

    public boolean isParamEmpty(){
        return null == param || param.isEmpty();
    }

    public boolean isStatusEmpty(){
        return null == status || status.isEmpty();
    }

    public boolean isStatusAndParamEmpty(){
        return isParamEmpty() && isStatusEmpty();
    }

}
