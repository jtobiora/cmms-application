package ng.upperlink.nibss.cmms.embeddables;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;


@Data
@Embeddable
@NoArgsConstructor
public class Name implements Serializable {


    @Column(name = "FirstName")
    private String firstName;

    @Column(name = "MiddleName")
    private String middleName;

    @Column(name = "LastName")
    private String lastName;

    public Name(ng.upperlink.nibss.cmms.dto.Name name) {
        this.setFirstName(name.getFirstName());
        this.setLastName(name.getLastName());
        this.setMiddleName(name.getMiddleName());

    }

    public Name(String firstName, String lastName, String middleName){
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setMiddleName(middleName);
    }
}
