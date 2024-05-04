package ng.upperlink.nibss.cmms.dto.emandates;

import lombok.Data;
import lombok.NonNull;

import javax.persistence.Embedded;

@Data
public class EMandateRequestBody {


   private AuthParam auth;

    EmandateRequest emandateRequest;
}

