package ng.upperlink.nibss.cmms.dto.response;

import lombok.Data;
import lombok.Setter;
import ng.upperlink.nibss.cmms.model.biller.Industry;

@Data
public class IndustryResponse {

    private Long id;
    private String name;

    public IndustryResponse(Industry industry) {
        setName(industry.getName());
        setId(industry.getId());
    }
}
