package ng.upperlink.nibss.cmms.api;

import lombok.Data;

@Data
public class ReportSearchWithParam extends ReportSearch {

    private String param = null;

    public String getParam() {
        return "%"+param+"%";
    }

}
