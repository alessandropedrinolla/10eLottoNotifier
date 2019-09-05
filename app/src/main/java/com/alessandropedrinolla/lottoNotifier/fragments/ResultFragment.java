package com.alessandropedrinolla.lottoNotifier.fragments;

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
import com.alessandropedrinolla.lottoNotifier.R;
import com.alessandropedrinolla.lottoNotifier.adapters.GamesAdapter;
import com.alessandropedrinolla.lottoNotifier.interfaces.GameAdapterListenerInterface;
import com.alessandropedrinolla.lottoNotifier.interfaces.ScraperListenerInterface;
import com.alessandropedrinolla.lottoNotifier.models.Config;
import com.alessandropedrinolla.lottoNotifier.models.Game;
import com.alessandropedrinolla.lottoNotifier.models.ScrapeData;
import com.alessandropedrinolla.lottoNotifier.network.Scraper;
import com.alessandropedrinolla.lottoNotifier.utils.IOUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

public class ResultFragment extends Fragment implements ScraperListenerInterface, GameAdapterListenerInterface {
    private ListView mListView;
    private GamesAdapter mGamesAdapter;
    private ArrayList<Game> mGames;
    private ProgressBar mProgressBar;
    private int mProgress;
    private int mProgressMax;
    private IOUtil mIoUtil;
    private Gson mGson;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGames = new ArrayList<>();
        mGson = new Gson();
        mIoUtil = new IOUtil(getContext());
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_result, container, false);

        inflatedView.findViewById(R.id.refresh_button).setOnClickListener(view -> {
            refreshList();
        });
        inflatedView.findViewById(R.id.check_button).setOnClickListener(view -> checkList());

        mProgressBar = inflatedView.findViewById(R.id.check_progress_bar);
        mListView = inflatedView.findViewById(R.id.list_view);

        setupListView();

        refreshList();

        return inflatedView;
    }

    private void refreshList() {
        mIoUtil.loadGames(mGames);
        mGamesAdapter.notifyDataSetChanged();
    }

    private void setupListView() {
        mGamesAdapter = new GamesAdapter(getContext(), mGames);
        mListView.setAdapter(mGamesAdapter);
        mGamesAdapter.gali = this;
    }

    private void checkList() {
        refreshList();

        // Game ids to check for:
        Hashtable<String, HashSet<Integer>> dateGameIdSet = new Hashtable<>();
        Scraper.sli = this;

        for (Game g : mGames) {
            if (g.getNumbersHit() == -1) {
                if (dateGameIdSet.containsKey(g.getDate())) {
                    dateGameIdSet.get(g.getDate()).add(g.getId());
                } else {
                    HashSet<Integer> n = new HashSet<>();
                    n.add(g.getId());
                    dateGameIdSet.put(g.getDate(), n);
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
        mIoUtil.persistScrapeData(scrapeData);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String date = scrapeData.getDate();

        // edit the mGames that have the resultJSON date that are not yet set
        for (Game g : mGames) {
            if (g.getNumbersHit() == -1 && g.getDate().equals(date)) {
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