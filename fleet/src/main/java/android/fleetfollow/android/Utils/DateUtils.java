package android.fleetfollow.android.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static String FromDateToString(Date date) throws ParseException {
        SimpleDateFormat formatter = null;
        formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        return formatter.format(date);
    }
}
