package ng.upperlink.nibss.cmms.dto;

import ng.upperlink.nibss.cmms.enums.Module;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponse implements Serializable {

    private Long id;

    private String name;

    private String url;

    private Module module;

    private String description;

    private boolean activated = true;

}
