package com.p3druz.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.p3druz.R;
import com.p3druz.adapters.GamesAdapter;
import com.p3druz.interfaces.GameAdapterListenerInterface;
import com.p3druz.interfaces.ScraperListenerInterface;
import com.p3druz.models.Config;
import com.p3druz.models.Game;
import com.p3druz.models.ScrapeData;
import com.p3druz.network.Scraper;
import com.p3druz.utils.SharedPreferencesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

public class ResultFragment extends Fragment implements ScraperListenerInterface, GameAdapterListenerInterface {
    private ListView mListView;
    private GamesAdapter mGamesAdapter;
    private ArrayList<Game> mGames;
    private ProgressBar mProgressBar;
    private int mProgress;
    private int mProgressMax;
    private SharedPreferencesUtil mSharedPreferencesUtil;
    private Gson mGson;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGames = new ArrayList<>();
        mGson = new Gson();
        mSharedPreferencesUtil = new SharedPreferencesUtil(getActivity());
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_result, container, false);

        inflatedView.findViewById(R.id.refresh_button).setOnClickListener(view -> mSharedPreferencesUtil.loadGames());

        inflatedView.findViewById(R.id.check_button).setOnClickListener(view -> checkList());

        mProgressBar = inflatedView.findViewById(R.id.check_progress_bar);
        mListView = inflatedView.findViewById(R.id.list_view);

        setupListView();

        mGames = mSharedPreferencesUtil.loadGames();
        mGamesAdapter.notifyDataSetChanged();

        return inflatedView;
    }

    private void setupListView() {
        mGamesAdapter = new GamesAdapter(getContext(), mGames);
        mListView.setAdapter(mGamesAdapter);
        mGamesAdapter.gali = this;
    }

    private void checkList() {
        mGames = mSharedPreferencesUtil.loadGames();
        mGamesAdapter.notifyDataSetChanged();

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

        for (String date : dateGameIdSet.keySet()) {
            Scraper.getData(date, dateGameIdSet.get(date));
        }

        mProgress = 0;
        mProgressMax = dateGameIdSet.size();
        mProgressBar.setProgress(0);
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setMax(mProgressMax);
    }

    @Override
    public void onCompleted(String resultJSON) {
        ScrapeData scrapeData = mGson.fromJson(resultJSON, ScrapeData.class);
        mSharedPreferencesUtil.persistScrapeData(scrapeData);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String date = scrapeData.getDate();

        // edit the mGames that have the resultJSON date that are not yet set
        for (Game g : mGames) {
            if (g.getNumbersHit() == -1 && g.getDateAsString().equals(date)) {
                String numbers = scrapeData.getExtractions().get(g.getId()).getNumbersAsString();
                Runnable runnable = () -> {
                    g.checkNumbersHit(numbers);
                    mGamesAdapter.notifyDataSetChanged();
                    if (mProgress == mProgressMax) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                };
                Handler handler = new Handler(getContext().getMainLooper());
                handler.post(runnable);
            }
        }

        editor.putString(Config.USER_DATA, mGson.toJson(mGames));
        editor.apply();

        // Progress bar advance
        mProgress++;
        mProgressBar.setProgress(mProgress);
    }

    private void persistGames() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Config.USER_DATA, mGson.toJson(mGames));
        editor.apply();
        mGamesAdapter.notifyDataSetChanged();
    }

    @Override
    public void deleteGame(Game game) {
        mGames.remove(game);
        persistGames();
    }


}