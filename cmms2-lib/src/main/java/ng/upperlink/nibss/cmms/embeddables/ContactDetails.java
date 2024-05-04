package ng.upperlink.nibss.cmms.embeddables;

import ng.upperlink.nibss.cmms.model.contact.Country;
import ng.upperlink.nibss.cmms.model.contact.Lga;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.model.contact.State;

import javax.persistence.*;
import java.io.Serializable;


@Data
@Embeddable
//@AllArgsConstructor
@NoArgsConstructor
public class ContactDetails implements Serializable{

    @Column(name = "HouseNumber")
    private String houseNumber;

    @Column(name = "StreetName")
    @Lob
    private String streetName;

    @Column(name = "City")
    private String city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LgaId", referencedColumnName = "Id" )
    private Lga lga;

//    private State state;
//
//    private Country country;
    //@NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stateId", referencedColumnName = "Id")
    private State state;

//    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "countryId", referencedColumnName = "Id")
    private Country country;

    public ContactDetails(String houseNumber, String streetName, String city, Lga lga) {
        this.houseNumber = houseNumber;
        this.streetName = streetName;
        this.city = city;
        this.lga = lga;
    }

    public ContactDetails(String houseNumber, String streetName, String city, Lga lga,State state,Country country) {
        this.houseNumber = houseNumber;
        this.streetName = streetName;
        this.city = city;
        this.lga = lga;
        this.state = state;
        this.country = country;
    }

}
