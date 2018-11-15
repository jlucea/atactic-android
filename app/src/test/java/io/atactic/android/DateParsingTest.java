package io.atactic.android;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import io.atactic.android.utils.DateUtils;

public class DateParsingTest {


    @Test
    public void parseSampleDate() throws ParseException {

        String dateString = "2018-08-17T16:21:16.669+02:00[Europe/Madrid]";
        String dateString2 = "2018-12-30T16:21:16.669+01:00[Europe/Madrid]";

        String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

        String parseThis = dateString2;
        if (parseThis.contains("[")){
            parseThis = parseThis.substring(0,parseThis.indexOf("["));
        }

        System.out.println("Trying to parse: " + parseThis);

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        // sdf.setTimeZone(TimeZone.getDefault());

        Date date = sdf.parse(parseThis);

        System.out.println(date);
    }

}
