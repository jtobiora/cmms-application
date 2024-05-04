package ng.upperlink.nibss.cmms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.model.mandate.MandateStatus;

import javax.persistence.*;
import java.io.Serializable;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="NotificationsConfig",schema = Constants.SCHEMA_NAME)
public class NotificationsConfig implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(optional=false)
    @JoinColumn(unique=true)
    private MandateStatus mandateStatus;

    private int billerAllowedCount;

    private int bankAllowedCount;
}
