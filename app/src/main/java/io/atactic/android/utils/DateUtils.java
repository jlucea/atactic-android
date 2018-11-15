package io.atactic.android.utils;

import android.view.View;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtils {

/*
    public void foo(String ){


        // Display remaining days
        String qDeadlineStr = participationDescription.getJSONObject("campaign")
                .getString("endDate");

        String endDateStr = qDeadlineStr.split("T")[0];
        // System.out.println("String to parse as date: "+ endDateStr);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.applyLocalizedPattern("yyyy-MM-dd");

            Date endDate = sdf.parse(endDateStr, new ParsePosition(0));



            Date now = Calendar.getInstance().getTime();

            long timeDiff = endDate.getTime() - now.getTime();
            long daysDiff = TimeUnit.MILLISECONDS.toDays(timeDiff);

            // System.out.println("daysDiff: "+daysDiff);
            questDeadlineTextView.setText("Quedan "+daysDiff+" días");

        }catch (Exception e){

            e.printStackTrace();
            questDeadlineTextView.setVisibility(View.INVISIBLE);
        }



    }

*/

    public final static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    /*
     * Removes 'Z' and [Timezone/Country] from a date in a format like
     * "2018-08-17T16:21:16.669+02:00[Europe/Madrid]"
     */
    private static String filterDateString(String dateString){
        String cleanDateString = dateString;
        if (dateString.contains("[")){
            cleanDateString = cleanDateString.substring(0,cleanDateString.indexOf("["));
        }
        if (cleanDateString.endsWith("Z")){
            cleanDateString = cleanDateString.substring(0,cleanDateString.length()-1);
        }
        return cleanDateString;
    }

    public static Date parseDate(String dateString) throws ParseException {
        String parseThis = filterDateString(dateString);
        SimpleDateFormat sdf  = new SimpleDateFormat(DATE_FORMAT);
        return sdf.parse(parseThis);
    }


    // TODO Days Diff

    // TODO Hours Diff

}
