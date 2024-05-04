package ng.upperlink.nibss.cmms.model;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.view.Views;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="Rejection",schema = Constants.SCHEMA_NAME)
public class Rejection implements Serializable {
    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private Long id;

    @JsonView(Views.Public.class)
    private String comment;


//    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
//    private User user;

    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private RejectionReason rejectionReason;

    private Date dateRejected;

    public Rejection(String comment, RejectionReason rejectionReason, Date dateRejected) {
        this.comment = comment;
        this.rejectionReason = rejectionReason;
        this.dateRejected = dateRejected;
    }

}
