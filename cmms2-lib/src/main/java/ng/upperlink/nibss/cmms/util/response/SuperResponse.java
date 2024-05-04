package ng.upperlink.nibss.cmms.util.response;

import lombok.Data;

@Data
public class SuperResponse {

    public boolean status;
    public String message;
    public Object data;

}
