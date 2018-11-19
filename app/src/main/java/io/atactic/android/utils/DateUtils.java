package io.atactic.android.utils;

import android.view.View;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
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


    final static String ZONED_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    final static String BASE_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    /**
     * Will try to parse the string into a date, trying with different formats:
     *  "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", then "yyyy-MM-dd'T'HH:mm:ss.SSS"
     *
     * If the original String contains 'Z' and/or [TimeZone], these elements will be filtered.
     *
     * @param dateString String describing a date
     * @return Date object
     * @throws ParseException In case it's unable to parse the date
     */
    public static Date parseDate(String dateString) throws ParseException {
        String parseThis = filterDateString(dateString);

        SimpleDateFormat sdf;
        Date parsed;

        try {
            sdf = new SimpleDateFormat(ZONED_DATE_FORMAT, Locale.getDefault());
            parsed = sdf.parse(parseThis);

        }catch (ParseException err){
            sdf = new SimpleDateFormat(BASE_DATE_FORMAT, Locale.getDefault());
            parsed = sdf.parse(parseThis);
        }

        return parsed;
    }


    // TODO Days Diff

    // TODO Hours Diff

}
