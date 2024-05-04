package ng.upperlink.nibss.cmms.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import ng.upperlink.nibss.cmms.util.JsonDateTimeDeserializer;
import ng.upperlink.nibss.cmms.view.Views;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class DateAudit implements Serializable {

    @Column(name = "CreatedAt")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonDeserialize(using=JsonDateTimeDeserializer.class)
    @CreatedDate
    protected Date createdAt;

    @Column(name = "UpdatedAt")
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    protected Date updatedAt;


    @Column(name = "ApprovedAt")
    @JsonView(Views.Public.class)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonDeserialize(using=JsonDateTimeDeserializer.class)
    @LastModifiedDate
    protected Date approvedAt;

}
