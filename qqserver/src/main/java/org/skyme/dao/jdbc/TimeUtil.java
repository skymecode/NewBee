package org.skyme.dao.jdbc;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author:Skyme
 * @create: 2023-08-16 19:04
 * @Description:
 */
public class TimeUtil {
    public  static String date2String(Date date) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String dateStr = sdf.format(date);

        return dateStr;

    }

}
