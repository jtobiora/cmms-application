package ng.upperlink.nibss.cmms.embeddables;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import java.io.Serializable;


@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Image implements Serializable{

    @Column(name = "Image")
    @Lob
    private String image; //base64

    @Column(name = "ContentType", length = 128)
    private String content_type;

    @Column(name = "Size")
    private Double size;

}
