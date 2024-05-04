package ng.upperlink.nibss.cmms.dto.search;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;


@Data
public class PageSearchWithDate extends PageSearch implements Serializable{

    public final static String DATE_PATTERN = "yyyy-MM-dd";

    @DateTimeFormat(pattern = DATE_PATTERN)
    private Date from;

    @DateTimeFormat(pattern = DATE_PATTERN)
    private Date to;

    public void setFrom(Date from) {
        if(null != from) {
            startOfDay(from);
        }
    }

    private void startOfDay(Date from) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(from);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND, 0);

        this.from = calendar.getTime();
    }

    public void setTo(Date to) {
        if( null != to) {
            endOfDay(to);
        }
    }

    private void endOfDay(Date to) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(to);
        cal.set(Calendar.HOUR, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);

        this.to = cal.getTime();
    }

    public boolean isDateEmpty() {
        return null == from || null == to || from.after(to);
    }

    public boolean isParamAndDateEmpty(){
        return isDateEmpty() && isParamEmpty();
    }

    public boolean isStatusAndDateEmpty(){
        return isStatusEmpty() && isDateEmpty();
    }

    public boolean isParamAndStatusAndDateEmpty(){
        return isDateEmpty() && isParamEmpty() && isStatusEmpty();
    }
}
