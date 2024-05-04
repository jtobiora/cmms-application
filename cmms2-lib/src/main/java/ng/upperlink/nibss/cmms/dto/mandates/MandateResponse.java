package ng.upperlink.nibss.cmms.dto.mandates;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.model.mandate.Mandate;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MandateResponse implements Serializable{
   private String requestStatus;
   private Mandate mandate;
   //private ng.upperlink.nibss.cmms.dto.mandates.Mandate mn;
   private String mandateImageInBase64;

   public MandateResponse(String requestStatus,Mandate mandate){
      this.requestStatus = requestStatus;
      this.mandate = mandate;
   }

   public MandateResponse(Mandate mandate,String mandateImageInBase64){
      this.mandateImageInBase64 = mandateImageInBase64;
      this.mandate = mandate;
   }
}
