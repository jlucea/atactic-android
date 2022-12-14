package io.atactic.android.utils;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

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

    private final static String BASE_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    private final static String NOMILIS_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private final static String NOSECS_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm";
    private final static String ZONED_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    private final static String ZONED_DATE_FORMAT2 = "yyyy-MM-dd'T'HH:mm:ssXXX";

    /**
     * Will try to parse the string into a date, trying with different formats:
     *  "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", then "yyyy-MM-dd'T'HH:mm:ss.SSS"
     *
     * If the original String contains 'Z' and/or [TimeZone], these elements will be filtered.
     *
     * The parsed Date will be in UTC! Apply TimeZone before displaying it!
     *
     * @param dateString String describing a date
     * @return Date object
     * @throws ParseException In case it's unable to parse the date
     */
    public static Date parseDate(String dateString) throws ParseException {
        String parseThis = filterDateString(dateString);

        SimpleDateFormat sdf;
        // TimeZone timeZone = TimeZone.getDefault();
        TimeZone sourcetimeZone = TimeZone.getTimeZone("UTC");
        Date parsed;

        try {
            sdf = new SimpleDateFormat(ZONED_DATE_FORMAT, Locale.getDefault());

            sdf.setTimeZone(sourcetimeZone);
            parsed = sdf.parse(parseThis);

        } catch (Exception ex1) {
            try {
                sdf = new SimpleDateFormat(NOMILIS_DATE_FORMAT, Locale.getDefault());
                sdf.setTimeZone(sourcetimeZone);
                parsed = sdf.parse(parseThis);

            } catch (Exception ex2) {
                try {
                    sdf = new SimpleDateFormat(BASE_DATE_FORMAT, Locale.getDefault());
                    sdf.setTimeZone(sourcetimeZone);
                    parsed = sdf.parse(parseThis);

                } catch (Exception ex3) {
                    try {
                        sdf = new SimpleDateFormat(ZONED_DATE_FORMAT2, Locale.getDefault());
                        sdf.setTimeZone(sourcetimeZone);
                        parsed = sdf.parse(parseThis);

                    }catch (Exception ex4){
                        sdf = new SimpleDateFormat(NOSECS_DATE_FORMAT, Locale.getDefault());
                        sdf.setTimeZone(sourcetimeZone);
                        parsed = sdf.parse(parseThis);
                    }
                }
            }
        }
        return parsed;
    }




    // TODO Days Diff

    // TODO Hours Diff

}
