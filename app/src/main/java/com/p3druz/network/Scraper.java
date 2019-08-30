package com.p3druz.network;

import com.google.gson.JsonObject;
import com.p3druz.interfaces.ScraperListenerInterface;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

@SuppressWarnings("ALL")
public class Scraper {
    public static ScraperListenerInterface sli;

    /**
     * Gets the numbers associated with the @gameIds provided
     *
     * @param date
     * @param gameIds
     */
    static public void getData(String date, ArrayList gameIds) {
        final String url = "https://www.lottomaticaitalia.it/del/estrazioni-e-vincite/popup-pdf/estrazioni-giorno.html?data=" + date;

        new Thread(new Runnable() {
            @Override
            public void run() {
                Document d;

                try {
                    // Connect to website
                    d = Jsoup.connect(url).get();
                } catch (IOException IOEx) {
                    IOEx.printStackTrace();
                    return;
                }

                // Build selector based on gameIds
                String selector = "";
                for (int i = 0; i < gameIds.size(); i++) {
                    selector += String.format(Locale.getDefault(), "tr:eq(%d),", gameIds.get(i));
                }

                selector = selector.substring(0, selector.length() - 1);

                Elements rows = d.select(selector);

                JsonObject table = new JsonObject();

                int i = 1;

                for (Element row : rows) {
                    Elements values = row.select("td div.numeroEstratto");
                    String numbers = "";
                    for (Element el : values) {
                        numbers += el.text() + " ";
                    }
                    table.addProperty(String.valueOf(gameIds.get(i)), numbers);
                    i++;
                }

                sli.onCompleted(table);
            }
        }).start();
    }
}
