package com.steinbacher.storj_hoststats_app.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by georg on 09.11.17.
 */

public class TimestampConverter {
    private static final String TAG = "TimestampConverter";

    public static String getFormatedTimediff(Date startDate, Date endDate) {
        long timeDiff = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli =  secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = timeDiff / daysInMilli;
        timeDiff = timeDiff % daysInMilli;

        long elapsedHours = timeDiff / hoursInMilli;
        timeDiff = timeDiff % hoursInMilli;

        long elapsedMinutes = timeDiff / minutesInMilli;

        String onlineSinceString = "";

        if(elapsedDays > 0) {
            onlineSinceString += elapsedDays + "d ";
        }


        if(elapsedHours > 0) {
            onlineSinceString += elapsedHours + "h ";
        }

        if(elapsedMinutes > 0) {
            onlineSinceString += elapsedMinutes + "m";
        } else {
            onlineSinceString = "1m";
        }

        return onlineSinceString;
    }
}
