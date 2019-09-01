package com.p3druz;

import com.p3druz.interfaces.ScraperListenerInterface;
import com.p3druz.models.Game;
import com.p3druz.network.Scraper;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class DateUnitTest {
    @Test
    public void dateConversionIsCorrect() {
        Date d = null;
        String DATE_FORMAT = ("yyyyMMdd");
        String DATE_LOCALE_FORMAT = ("dd/MM/yyyy");
        String date = "30/08/2019";
        try {
            d = new SimpleDateFormat(DATE_LOCALE_FORMAT, Locale.getDefault()).parse(date);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        assertEquals("20190830", new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(d));
    }
}