package vakulenko.pi.nure.notesapp;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class DateUtils {

    public static String getTodayDate() {
        StringBuilder sb = new StringBuilder();
        Calendar c = GregorianCalendar.getInstance();

        sb.append(c.get(Calendar.YEAR)).append("-");
        sb.append(c.get(Calendar.MONTH) + 1).append("-");
        sb.append(c.get(Calendar.DAY_OF_MONTH));

        return sb.toString();
    }
}
