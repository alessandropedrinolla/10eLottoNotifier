package com.p3druz;

import android.util.Log;

import androidx.annotation.VisibleForTesting;

import com.googlecode.junittoolbox.PollingWait;
import com.p3druz.interfaces.ScraperListenerInterface;
import com.p3druz.network.Scraper;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ScrapeUnitTest implements ScraperListenerInterface {
    private JSONObject resultsJSON;
    private int dataScrapeRequestCount;
    private int dataScrapeRequestCompleted;

    @Test
    public void dataScrapeTest() {
        // TODO test async method
        Scraper.sli = this;
        dataScrapeRequestCount = 288;
        PollingWait wait = new PollingWait().timeoutAfter(10, SECONDS)
                .pollEvery(100, MILLISECONDS);

        HashSet<Integer> indexes = new HashSet<>();
        for (int i = 0; i< 289;i++) {
            indexes.add(i);
        }
        Scraper.getData("20180825", indexes);
        wait.until(()->dataScrapeRequestCompleted==dataScrapeRequestCount);
    }

    @Override
    public void onCompleted(JSONObject results) {
        try {
            resultsJSON.put(results.getString("date"), results);
        }catch (JSONException jsonEx){
            jsonEx.printStackTrace();
        }finally {
            dataScrapeRequestCompleted++;
            Log.println(Log.DEBUG, "Request completed:",String.valueOf(dataScrapeRequestCompleted));
            if(dataScrapeRequestCompleted==dataScrapeRequestCount)
                Log.println(Log.DEBUG, "All request completed", String.valueOf(dataScrapeRequestCompleted));
        }
    }
}