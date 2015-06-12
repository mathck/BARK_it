package com.barkitapp.android.com.barkitapp.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

// Uhrzeit minuten wenn < 60min, stunden wenn < 24h, danach tage
public class TimeConverter {

    private static final String MINUTES = " m";
    private static final String HOURS = " h";
    private static final String DAYS = " d";

    /**
     * Calculates the difference beteween the currentDate and postCreated, output = "42 m" | "11 h" | "5d"
     * @param postCreated createdDate of Post
     * @return minutes when < 60min, hours when < 24h, afterwards days
     */
    public static String getPostAge(Date postCreated) {
        long diff = GetUTCdatetimeAsDate().getTime() - postCreated.getTime();

        int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(diff);
        int hours = (int) TimeUnit.MILLISECONDS.toHours(diff);
        int days = (int) TimeUnit.MILLISECONDS.toDays(diff);

        if(days > 0) {
            return days + DAYS;
        }
        else if(hours > 0) {
            return hours + HOURS;
        }
        else {
            return minutes + MINUTES;
        }
    }

    // HELPER METHODS ----------------------------------------------------------------------

    private static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";

    private static Date GetUTCdatetimeAsDate()
    {
        return StringDateToDate(GetUTCdatetimeAsString());
    }

    private static String GetUTCdatetimeAsString()
    {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());

        return utcTime;
    }

    private static Date StringDateToDate(String StrDate)
    {
        Date dateToReturn = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);

        try
        {
            dateToReturn = (Date) dateFormat.parse(StrDate);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return dateToReturn;
    }
}
