package com.predisw.common.util;

import com.predisw.common.util.DateFormatUtil;
import org.junit.Test;

import java.text.ParseException;

/**
 * Created by eggnwwg on 9/11/2017.
 */
public class DateFormatUtilT {



    @Test
    public void getTime() throws ParseException {
        long time =DateFormatUtil.parse("2019-10-01 10:31:56","yyyy-MM-dd HH:mm:ss").getTime();

        System.out.println(time);  // output 1569897116000

    }

}
