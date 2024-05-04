package ng.upperlink.nibss.cmms.model.emandate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.model.SuperModel;
import ng.upperlink.nibss.cmms.model.User;
import ng.upperlink.nibss.cmms.model.authorization.AuthorizationTable;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "EmandateConfiguration",schema = Constants.SCHEMA_NAME)
public class EmandateConfig extends AuthorizationTable {
    @Column
    private String username;

    @Column
    private String domainName;

    @Column(name = "notificationUrl")
    private String notificationUrl;

    @JsonIgnore
    @Column
    private String clientPassKey;

    @JsonIgnore
    @Column
    private String password;
}
