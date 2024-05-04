package ng.upperlink.nibss.cmms.dto;

import ng.upperlink.nibss.cmms.model.Settings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class SettingDto {


    private String name;
    private String value;

    public SettingDto(Settings settings){
        setName(settings.getName());
        setValue(settings.getValue());
    }
}
