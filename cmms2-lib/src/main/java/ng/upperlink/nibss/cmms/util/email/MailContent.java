package ng.upperlink.nibss.cmms.util.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailContent {

    private String from;
    private String[] to;
    private String subject;
    private List<Object> attachments;
    private Map<String, Object> model;

}
