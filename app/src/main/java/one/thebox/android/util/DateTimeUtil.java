package one.thebox.android.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by harsh on 12/12/15.
 */
public class DateTimeUtil {
    private static String TAG = DateTimeUtil.class.getSimpleName();

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
            Log.e(TAG, "ParseException", e);
            return null;
        }
    }

    public static long getDifferenceAsDay(Date startDate, Date endDate) {
        long diff = startDate.getTime() - endDate.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public static Date convertStringToDate(String date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return formatter.parse(date);
    }

    public static boolean checkIsLieBetweenDates(Date date, Date dateStart, Date dateEnd) {
        return date.after(dateStart) && date.before(dateEnd);
    }
}
