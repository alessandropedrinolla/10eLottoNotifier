package com.alessandropedrinolla.lottoNotifier.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.alessandropedrinolla.lottoNotifier.models.Config;
import com.alessandropedrinolla.lottoNotifier.models.Game;
import com.alessandropedrinolla.lottoNotifier.models.ScrapeData;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

public class SharedPreferencesUtil {
    private static Gson mGson = new Gson();
    private Activity mActivity;

    public SharedPreferencesUtil(Activity activity) {
        this.mActivity = activity;
    }

    public void persistGames(ArrayList<Game> games) {
        SharedPreferences sharedPreferences = mActivity.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String oldGamesStr = sharedPreferences.getString(Config.USER_DATA, null);
        ArrayList<Game> oldGames = gson.fromJson(oldGamesStr, new TypeToken<ArrayList<Game>>() {
        }.getType());

        if (oldGames != null) {
            games.addAll(oldGames);
        }

        editor.putString(Config.USER_DATA, gson.toJson(games));
        editor.apply();
    }

    public void persistScrapeData(ScrapeData scrapeData) {
        Gson gson = new Gson();

        SharedPreferences sharedPreferences = mActivity.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String localDataJsonStr = sharedPreferences.getString(Config.SCRAPE_DATA, null);
        Hashtable<String, ScrapeData> localScrapeDataHashTable = mGson.fromJson(localDataJsonStr, new TypeToken<Hashtable<String, ScrapeData>>() {
        }.getType());

        String date = scrapeData.getDate();

        if (localScrapeDataHashTable.containsKey(date)) {
            // Add all the missing extraction from the scraped ones
            Set<Integer> keySet = scrapeData.getExtractions().keySet();

            for (Integer key : keySet) {
                int gameId = scrapeData.getExtractions().get(key).getId();
                if (!localScrapeDataHashTable.get(date).getExtractions().containsKey(gameId)) {
                    // If there is not extraction with gameId save it
                    localScrapeDataHashTable.get(date).getExtractions().put(gameId, scrapeData.getExtractions().get(gameId));
                }
            }
        } else {
            // Insert all scraped extraction if there wasn't any within the specific date
            localScrapeDataHashTable.put(date, scrapeData);
        }

        editor.putString(Config.SCRAPE_DATA, gson.toJson(localScrapeDataHashTable));
        editor.apply();
    }

    public void loadGames(ArrayList<Game> games) {
        SharedPreferences sharedPref = mActivity.getSharedPreferences("data", Context.MODE_PRIVATE);
        String jsonStr = sharedPref.getString(Config.USER_DATA, null);
        if (jsonStr == null) {
            return;
        }
        games.clear();
        games.addAll(mGson.fromJson(jsonStr, new TypeToken<ArrayList<Game>>() {}.getType()));
    }
}
