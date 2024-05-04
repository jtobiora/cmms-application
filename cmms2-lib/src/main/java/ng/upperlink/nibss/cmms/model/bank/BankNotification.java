package ng.upperlink.nibss.cmms.model.bank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.model.Notification;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="BankNotification",schema = Constants.SCHEMA_NAME)
public class BankNotification extends Notification implements Serializable {

//    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
//    @JoinColumn(referencedColumnName ="Code")
//    private Bank bank;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinColumn(name ="bank_id")
    private Bank bankEntity;
}
