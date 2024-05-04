package ng.upperlink.nibss.cmms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.model.mandate.MandateStatus;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "Notification",schema = Constants.SCHEMA_NAME)
public class Notification extends SuperModel implements Serializable{
    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name="mandate_status_id")
    private MandateStatus mandateStatus;

    @Column(name="notification_type")
    private int notificationType;

    @Email
    @Column(nullable=false, name="emailAddress")
    private String emailAddress;

    @ManyToOne
    private User createdBy;
}
