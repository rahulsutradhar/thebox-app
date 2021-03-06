package one.thebox.android.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by harsh on 12/12/15.
 */
public class DateTimeUtil {
    private static String TAG = DateTimeUtil.class.getSimpleName();
    private static final long fetch_after_miliseconds = 10000;

    public static String getNowDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String dateToString(Date date) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    public static Date stringToDate(String dateStr) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            //Log.e(TAG, "ParseException", e);
            return null;
        }
    }

    public static long getDifferenceAsDay(Date startDate, Date endDate) {
        long diff = endDate.getTime() - startDate.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public static long getDifferenceAsHours(Date startDate, Date endDate) {
        long diff = endDate.getTime() - startDate.getTime();
        return TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public static Date convertStringToDate(String date) {
//        2016-01-29T09:00:00.000+05:30

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        TimeZone timeZone = TimeZone.getDefault();
        sdf.setTimeZone(timeZone);

//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        try {
            Date d = sdf.parse(date);
            return d;
//            return formatter.parse(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean checkIsLieBetweenDates(Date date, Date dateStart, Date dateEnd) {
        return date.after(dateStart) && date.before(dateEnd);
    }

    public static boolean checkIfSameDayMonthYear(Date date1, Date date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
                && calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
                && calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);
    }

    public static boolean isArrivingToday(int hours) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, hours);
        cal.getTime();
        return cal.get(Calendar.DAY_OF_MONTH) == Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    public static boolean should_i_fetch_model_data_from_server(long timestamp) {
        long time_difference = (new Date(System.currentTimeMillis())).getTime() - timestamp;
        return (((time_difference > fetch_after_miliseconds)) ? true : false);
    }

    public static boolean checkCurrentDateIsWithinRange(Date startDate, Date endDate, Date currentDate) {

        return currentDate.getTime() >= startDate.getTime() && currentDate.getTime() <= endDate.getTime();
    }

}
