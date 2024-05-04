package ng.upperlink.nibss.cmms.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.embeddables.Name;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class SubscriberResponse implements Serializable {

    private long id;
    private ng.upperlink.nibss.cmms.embeddables.Name name;
    private String code;

    public SubscriberResponse(long id, Name name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }
}
