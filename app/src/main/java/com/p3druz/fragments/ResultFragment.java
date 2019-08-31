package com.p3druz.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.p3druz.R;
import com.p3druz.adapters.GamesAdapter;
import com.p3druz.interfaces.ScraperListenerInterface;
import com.p3druz.models.Game;
import com.p3druz.network.Scraper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

public class ResultFragment extends Fragment implements ScraperListenerInterface {
    private ListView mListView;
    private GamesAdapter mGamesAdapter;
    private ArrayList<Game> mGames;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGames = new ArrayList<>();
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_result, container, false);

        inflatedView.findViewById(R.id.refresh_button).setOnClickListener(view -> loadListView());

        inflatedView.findViewById(R.id.check_button).setOnClickListener(view -> checkList());

        mListView = inflatedView.findViewById(R.id.list_view);

        setupListView();
        loadListView();

        return inflatedView;
    }

    private void setupListView() {
        mGamesAdapter = new GamesAdapter(getContext(), mGames);
        mListView.setAdapter(mGamesAdapter);
    }

    private void loadListView() {
        SharedPreferences sharedPref = getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        String jsonStr = sharedPref.getString("games", null);

        if (jsonStr == null) return;

        JSONArray jsonArray;

        try {
            jsonArray = new JSONArray(jsonStr);
        } catch (JSONException jsonEx) {
            jsonEx.printStackTrace();
            return;
        }

        mGames.clear();

        Game.listFromJSONArray(mGames, jsonArray);

        mGamesAdapter.notifyDataSetChanged();
    }

    private void checkList() {
        loadListView();

        // TODO check for unset results in list
        // Game ids to check for:
        Hashtable<String, HashSet<Integer>> dateGameIdSet = new Hashtable<>();
        Scraper.sli = this;

        for (Game g : mGames) {
            if (g.getNumbersHit() == -1) {
                if (dateGameIdSet.containsKey(g.getDateAsString())) {
                    dateGameIdSet.get(g.getDateAsString()).add(g.getId());
                } else {
                    HashSet<Integer> n = new HashSet<>();
                    n.add(g.getId());
                    dateGameIdSet.put(g.getDateAsString(), n);
                }
            }
        }

        for (String key : dateGameIdSet.keySet()) {
            Scraper.getData(key, dateGameIdSet.get(key));
        }
    }

    @Override
    public void onCompleted(JSONObject resultJSON) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String localDataStr = sharedPreferences.getString("extractions", null);
        JSONObject localDataJSON = null;
        String date = null;
        try {
            if (localDataStr == null) {
                localDataJSON = new JSONObject();
            } else {
                localDataJSON = new JSONObject(localDataStr);
            }
            date = resultJSON.getString("date");
            JSONObject scrapedExtractionsJSON = resultJSON.getJSONObject("extractions");

            if (localDataJSON.has(date)) {
                // Add all the missing extraction from the scraped ones
                JSONObject localExtractionsJSON = localDataJSON.getJSONObject(date);

                Iterator it = scrapedExtractionsJSON.keys();
                while (it.hasNext()) {
                    String gameId = (String) it.next();
                    if (!localExtractionsJSON.has(gameId)) {
                        // If there is not extraction with gameId save it
                        localExtractionsJSON.put(gameId, localExtractionsJSON.get(gameId));
                    }
                }
            } else {
                // Insert all scraped extraction if there wasn't any within the specific date
                localDataJSON.put(date, scrapedExtractionsJSON);
            }
        } catch (JSONException jsonEx) {
            jsonEx.printStackTrace();
        }

        editor.putString("extractions", localDataJSON.toString());
        editor.apply();

        // edit the mGames that have the resultJSON date that are not yet set
        for (Game g : mGames) {
            if (g.getNumbersHit() == -1 && g.getDateAsString().equals(date)) {
                try {
                    String numbers = localDataJSON.getJSONObject(date).getString(String.valueOf(g.getId()));

                } catch (JSONException jsonEx) {
                    jsonEx.printStackTrace();
                    return;
                }
            }
        }

        mGamesAdapter.notifyDataSetChanged();
    }
}