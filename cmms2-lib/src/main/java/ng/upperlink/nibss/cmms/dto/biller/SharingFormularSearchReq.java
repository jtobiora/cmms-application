package ng.upperlink.nibss.cmms.dto.biller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SharingFormularSearchReq {
    private String fee;
    private String beneficiaryId;
    private String billerId;
}
