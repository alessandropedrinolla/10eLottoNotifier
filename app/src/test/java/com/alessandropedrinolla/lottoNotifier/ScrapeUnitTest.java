package com.alessandropedrinolla.lottoNotifier;

import android.util.Log;

import com.google.gson.Gson;
import com.googlecode.junittoolbox.PollingWait;
import com.alessandropedrinolla.lottoNotifier.interfaces.ScraperListenerInterface;
import com.alessandropedrinolla.lottoNotifier.models.ScrapeData;
import com.alessandropedrinolla.lottoNotifier.network.Scraper;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class ScrapeUnitTest implements ScraperListenerInterface {
    private ArrayList<ScrapeData> scrapeData;
    private int dataScrapeRequestCount;
    private int dataScrapeRequestCompleted;
    private Gson gson = new Gson();

    @Test
    public void dataScrapeTest() {
        Scraper.sli = this;
        scrapeData = new ArrayList<>();
        dataScrapeRequestCompleted = 0;
        dataScrapeRequestCount = 1;
        PollingWait wait = new PollingWait().timeoutAfter(30, SECONDS)
                .pollEvery(100, MILLISECONDS);

        HashSet<Integer> indexes = new HashSet<>();
        for (int i = 1; i <= 288; i++) {
            indexes.add(i);
        }
        Scraper.getData("20190825", indexes);

        wait.until(() -> dataScrapeRequestCompleted == dataScrapeRequestCount);
    }

    @Override
    public void onCompleted(String resultsJson) {
        scrapeData.add(gson.fromJson(resultsJson, ScrapeData.class));
        dataScrapeRequestCompleted++;
        Log.println(Log.DEBUG, "Request completed:", String.valueOf(dataScrapeRequestCompleted));
        if (dataScrapeRequestCompleted == dataScrapeRequestCount) {
            Log.println(Log.DEBUG, "All request completed", String.valueOf(dataScrapeRequestCompleted));
        }
    }
}