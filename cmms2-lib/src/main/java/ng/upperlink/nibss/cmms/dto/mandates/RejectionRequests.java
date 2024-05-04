package ng.upperlink.nibss.cmms.dto.mandates;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RejectionRequests {
    private Long rejectionId;
    private String comment;
}
