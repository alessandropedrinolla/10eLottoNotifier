package com.p3druz.network;

import com.p3druz.interfaces.ScraperListenerInterface;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

@SuppressWarnings("ALL")
public class Scraper {
    public static ScraperListenerInterface sli;

    /**
     * Gets the extracted numbers associated with the @gameIds provided
     *
     * @param date
     * @param gameIds
     */
    static public void getData(String date, HashSet<Integer> gameIds) {
        final String url = "https://www.lottomaticaitalia.it/del/estrazioni-e-vincite/popup-pdf/estrazioni-giorno.html?data=" + date;

        new Thread(new Runnable() {
            @Override
            public void run() {
                Document d;

                try {
                    // Connect to website
                    d = Jsoup.connect(url).timeout(10 * 1000).get();
                } catch (IOException IOEx) {
                    IOEx.printStackTrace();
                    return;
                }

                // Build selector based on gameIds
                String selector = "";
                Iterator it = gameIds.iterator();
                while (it.hasNext()) {
                    selector += String.format(Locale.getDefault(), "tr:eq(%d),", it.next());
                }

                selector = selector.substring(0, selector.length() - 1);

                Elements rows = d.select(selector);

                JSONObject data = new JSONObject();
                JSONObject estractions = new JSONObject();

                int currentGameId;

                for (Element row : rows) {
                    estractions = new JSONObject();
                    it = gameIds.iterator();
                    while (it.hasNext()) {
                        Elements columns = row.select("td div.numeroEstratto");
                        String numbers = "";
                        for (Element el : columns) {
                            numbers += el.text() + " ";
                        }
                        try {
                            estractions.put(String.valueOf(it.next()), numbers);
                        } catch (JSONException jsonEx) {
                            jsonEx.printStackTrace();
                        }
                    }
                }

                try {
                    data.put("date", date);
                    data.put("extractions", estractions);
                } catch (JSONException jsonEx) {
                    jsonEx.printStackTrace();
                }

                sli.onCompleted(data);
            }
        }).start();
    }
}
