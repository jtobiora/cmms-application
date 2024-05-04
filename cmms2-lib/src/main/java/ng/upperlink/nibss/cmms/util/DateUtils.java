package ng.upperlink.nibss.cmms.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class DateUtils {
    private static Log logger = LogFactory.getLog( DateUtils.class );


    public static Date convertStringToDate(String dateString ) {
        logger.trace( "entering :convertStringToDate . dateString [" + dateString + "]" );

        if( dateString == null || dateString.length() == 0 ) {
            logger.debug( "Returning null date" );
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat( "dd/MM/yyyy" );
        Date date = null;
        try {
            date = sdf.parse( dateString );
        }
        catch( ParseException e ) {
            logger.error( "Date Time provided not Valid [ " + e + "]" );
			/*
			 * @TODO throw exception
			 */
        }

        return date;
    }

    public static String convertDateToDBDate( String inputDate ) {

        if( inputDate != null && inputDate.trim().length() > 0 ) {
            SimpleDateFormat sm = new SimpleDateFormat( "dd-MMM-yy" );
            inputDate = sm.format( DateUtils.convertStringToDate( inputDate ) );
            inputDate = inputDate.toUpperCase();
        }
        return inputDate;
    }

    public static String convertDBDateToDate( Date dbDate ) {
        String date="";
        if( dbDate != null) {
            SimpleDateFormat sm = new SimpleDateFormat( "dd/MM/yyyy hh:mm:ss" );
            date = sm.format( dbDate );
            //logger.debug( "date in convertDbToDate dbDate[ "+dbDate+" ]converted date[ "+date+" ]" );
        }
        return date;
    }

    public static Date lastSecondOftheDay( Date dateTime ) {

        if( dateTime == null ) {
            throw new IllegalArgumentException( "Null Date argument could not be changed" );
        }

        Calendar calender = new GregorianCalendar();
        calender.setTime( dateTime );

        // Remove the hours, minutes, seconds and milliseconds
        calender.set( Calendar.HOUR_OF_DAY, 23 );
        calender.set( Calendar.MINUTE, 59 );
        calender.set( Calendar.SECOND, 59 );
        calender.set( Calendar.MILLISECOND, 0 );

        return calender.getTime();
    }


    public static Date nullifyTime( Date dateTime ) {
        if( dateTime == null ) {
            throw new IllegalArgumentException( "Null Date argument could not be nullified!" );
        }

        Calendar calender = new GregorianCalendar();
        calender.setTime( dateTime );

        // Remove the hours, minutes, seconds and milliseconds
        calender.set( Calendar.HOUR_OF_DAY, 0 );
        calender.set( Calendar.MINUTE, 0 );
        calender.set( Calendar.SECOND, 0 );
        calender.set( Calendar.MILLISECOND, 0 );

        return calender.getTime();
    }

    public static String convertMillisecondsToTime( long miliseconds ) {

        long seconds = miliseconds / 1000;
        String sec = "00";
        String mins = "00";
        String hours = "00";

        // for seconds
        if( seconds > 0 ) {
            sec = "" + ( seconds % 60 );
            if( seconds % 60 < 10 ) {
                sec = "0" + ( seconds % 60 );
            }
        }

        // for mins
        if( seconds > 60 ) {
            mins = "" + ( seconds / 60 % 60 );
            if( ( seconds / 60 % 60 ) < 10 ) {
                mins = "0" + ( seconds / 60 % 60 );
            }
        }

        // for hours
        if( seconds / 60 > 60 ) {
            hours = "" + ( seconds / 60 / 60 );
            if( ( seconds / 60 / 60 ) < 10 ) {
                hours = "0" + ( seconds / 60 / 60 );
            }

        }

        return "" + hours + ":" + mins + ":" + sec; //HH:MM:SS

    }

    public synchronized static Date calculateNextDebitDate(Date startDate,Date endDate, int weekFrequency){
        Date nextDebitDate = null;
        final Calendar startCalendar = Calendar.getInstance();
        final Calendar endCalendar = Calendar.getInstance();

        startCalendar.setTime(nullifyTime(startDate));
        endCalendar.setTime(nullifyTime(endDate));

        LocalDate locStartDate = dateToLocalDate(startDate);
        LocalDate locEndDate = dateToLocalDate(endDate);

        long days= endDate.getTime()-startDate.getTime();
        days= TimeUnit.DAYS.convert(days, TimeUnit.MILLISECONDS);

        //get the number of weeks debit would run
        long noOfWeeks=Math.floorDiv(days,7);

        logger.debug("noOfWeeks::"+noOfWeeks);

        for(int i=1;i<=noOfWeeks; i++){
            if(locStartDate.isAfter(LocalDate.now()))
                return startDate;
            logger.info("running loop for week "+i);
            LocalDate tempLocalDate=locStartDate.plusWeeks(i*weekFrequency);
            if(tempLocalDate.isAfter(LocalDate.now())){
                logger.info("we are in period "+i+" of the debit");
                if(tempLocalDate.isAfter(locEndDate))
                    return null;
                nextDebitDate=localDateToDate(tempLocalDate);
                //recalculate final date for monthly mandates
                if(weekFrequency%4==0){
                    int day = startCalendar.get(Calendar.DAY_OF_MONTH);
                    logger.info("day of month debit started::"+day);
                    final Calendar nextDebitDateCalendar = Calendar.getInstance();
                    nextDebitDateCalendar.setTime(nextDebitDate);
                    //get the actual day max day in the new month
                    int actualMaxDayOfMonth=nextDebitDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    if(day>actualMaxDayOfMonth)
                        day=actualMaxDayOfMonth;
                    nextDebitDateCalendar.set(Calendar.DAY_OF_MONTH, day);
                    if(nextDebitDateCalendar.before(Calendar.getInstance())){
                        //increase the debit date to next month
                        nextDebitDateCalendar.add(Calendar.MONTH, 1);
                        if(nextDebitDateCalendar.after(endCalendar))
                            return null;
                    }
                    nextDebitDate=nextDebitDateCalendar.getTime();
                }
                break;
            }
        }
        return nextDebitDate;
    }


    /*Converts Java 8 localDate to DateObject*/
    public static LocalDate dateToLocalDate(Date date){
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();

    }

//    public static void main(String[] args) throws ParseException {
//        Date startDate=new SimpleDateFormat("yyyy-MM-dd").parse("2015-09-01");
//        Date endDate=new SimpleDateFormat("yyyy-MM-dd").parse("2015-10-01");
//        System.out.println(calculateNextDebitDate(startDate,endDate,4));
//    }


    /*Converts  DateObject to Java 8 localDate*/
    public static Date localDateToDate(LocalDate localDate){
        Instant instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Date res = Date.from(instant);

        return res;
    }
}
