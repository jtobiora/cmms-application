package ng.upperlink.nibss.cmms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.enums.Constants;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="WebServiceNotification",schema = Constants.SCHEMA_NAME)
public class WebServiceNotification implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int bankId;

    private int billerId;

    private int notificationType;

    private Long mandateId;

    private int bankNotified;

    private int billerNotified;

    private int billerNotificationCounter;

    private int bankNotificationCounter;
}
