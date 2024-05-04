package ng.upperlink.nibss.cmms.dashboard;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by stanlee on 23/05/2018.
 */
@Data
@NoArgsConstructor
public class SyncActivityDashboard implements Serializable{

    private String countOfActive;
    private String countOfInActive;

    public SyncActivityDashboard(String countOfActive, String countOfInActive) {
        this.countOfActive = countOfActive;
        this.countOfInActive = countOfInActive;
    }
}
