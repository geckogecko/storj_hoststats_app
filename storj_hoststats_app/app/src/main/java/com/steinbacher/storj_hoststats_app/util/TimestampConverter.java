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
            onlineSinceString += elapsedDays;

            if(elapsedDays == 1) {
                onlineSinceString += " day ";
            } else {
                onlineSinceString += " days ";
            }
        }


        if(elapsedHours > 0) {
            onlineSinceString += elapsedHours;

            if(elapsedHours == 1) {
                onlineSinceString += " hour ";
            } else {
                onlineSinceString += " hours ";
            }
        }

        if(elapsedMinutes > 0) {
            onlineSinceString += elapsedMinutes;

            if(elapsedMinutes == 1) {
                onlineSinceString += " minute ";
            } else {
                onlineSinceString += " minutes ";
            }
        } else {
            onlineSinceString = "1 minute";
        }

        return onlineSinceString;
    }
}
