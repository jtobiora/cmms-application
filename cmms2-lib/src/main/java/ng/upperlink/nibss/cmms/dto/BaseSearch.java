package ng.upperlink.nibss.cmms.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@Data
public class BaseSearch  implements Serializable {

    public final static String DATE_PATTERN = "yyyy-MM-dd";

    private final static int MIN_DATA_SIZE = 10;

    private final static int MAX_DATA_SIZE = 100;

    protected int pageNumber;

    protected int pageSize;

    @DateTimeFormat(pattern = DATE_PATTERN)
    private Date from;

    @DateTimeFormat(pattern = DATE_PATTERN)
    private Date to;


    public int getDataSize() {
        if (pageSize <= 0)
            return MIN_DATA_SIZE;
        if( pageSize > MAX_DATA_SIZE)
            return MAX_DATA_SIZE;

        return pageSize;
    }

    public void setFrom(Date from) {
        if(null != from) {
            startOfDay(from);
        }
    }

    private void startOfDay(Date from) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(from);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
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
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);

        this.to = cal.getTime();
    }


    public int getDataPageNo() {
        if (pageNumber <= 0)
            return 0;

        int page = pageNumber; //- 1;

        return page < 0 ? 0 : page;
    }

    public boolean isDateEmpty() {
        return null == from || null == to || from.after(to);
    }
}
