package nl.bamischrijft.blackmarket.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static String format(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    public static Date getDateOffset(Date input, int i, int unit) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(input);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(unit, i);
        return calendar.getTime();
    }

    public static String getDateFormatting(Date oldDate, Date newDate) {
        long different = newDate.getTime() - oldDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        if (elapsedDays == 0 && elapsedHours == 0 && elapsedMinutes == 0) {
            return String.format("%d " + (elapsedSeconds == 1 ? "seconde" : "seconden"), elapsedSeconds);
        }

        if (elapsedDays == 0 && elapsedHours == 0) {
            return String.format("%d " + (elapsedMinutes == 1 ? "minuut" : "minuten") + " en %d " + (elapsedSeconds == 1 ? "seconde" : "seconden"), elapsedMinutes, elapsedSeconds);
        }

        if (elapsedDays == 0) {
            return String.format("%d " + (elapsedHours == 1 ? "uur" : "uren" ) + ", %d " + (elapsedMinutes == 1 ? "minuut" : "minuten") + " en %d " + (elapsedSeconds == 1 ? "seconde" : "seconden"), elapsedHours, elapsedMinutes, elapsedSeconds);
        }

        return String.format("%d " + (elapsedDays == 1 ? "dag" : "dagen") +", %d " +
                (elapsedHours == 1 ? "uur" : "uren" )+ ", %d " + (elapsedMinutes == 1 ? "minuut" : "minuten") + " en %d " + (
                elapsedSeconds == 1 ? "seconde" : "seconden"
        ), elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
    }

}
