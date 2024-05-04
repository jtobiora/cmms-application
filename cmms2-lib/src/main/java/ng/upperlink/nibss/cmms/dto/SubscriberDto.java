package ng.upperlink.nibss.cmms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SubscriberDto implements Serializable {

    public enum Report {
        FIRST_NAME("FIRST NAME", "firstName"),
        LAST_NAME("LAST NAME", "lastName"),
        STATE("STATE", "state"),
        LGA("LGA", "lga"),
        EMAIL("EMAIL", "emailAddress"),
        ROLE("ROLE", "roles"),
        BVN("BVN", "bvn"),
        AGENT_MANAGER_CODE("AGENT MANAGER CODE", "agentManagerCode"),
        DATE_CREATED("DATE CREATED", "dateCreated");


        private final String label;
        private final String propertyName;

        Report(String label, String propertyName) {

            this.label = label;
            this.propertyName = propertyName;
        }

        public String getLabel() {
            return label;
        }

        public String getPropertyName() {
            return propertyName;
        }
    }

    private long id;
    private String firstName;
    private String lastName;

    private String state;

    private String lga;
    private String email;
    private String role;
    private String bvn;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Africa/Lagos")
    private final Date dateCreated;

    private String agentManagerCode;


    public SubscriberDto(long id, String firstName, String lastName, String state, String lga,
                         String email, String bvn, String agentManagerCode, Date dateCreated) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.state = state;
        this.lga = lga;
        this.email = email;
        this.bvn = bvn;
        this.dateCreated = dateCreated;
        this.agentManagerCode = agentManagerCode;
    }


}
