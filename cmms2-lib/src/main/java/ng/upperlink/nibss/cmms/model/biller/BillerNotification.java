package ng.upperlink.nibss.cmms.model.biller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.model.Notification;
import ng.upperlink.nibss.cmms.model.biller.Biller;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="BillerNotification",schema = Constants.SCHEMA_NAME)
public class BillerNotification extends Notification implements Serializable {

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    private Biller biller;

}
