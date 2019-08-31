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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.p3druz.R;
import com.p3druz.adapters.GamesAdapter;
import com.p3druz.interfaces.ScraperListenerInterface;
import com.p3druz.models.Game;
import com.p3druz.network.Scraper;

import java.util.ArrayList;

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
        Gson g = new Gson();
        JsonArray jsonArray = g.fromJson(jsonStr, JsonArray.class);

        if (jsonArray == null) return;

        mGames.clear();

        Game.listFromJsonArray(mGames, jsonArray);

        mGamesAdapter.notifyDataSetChanged();
    }

    private void checkList() {
        Scraper.sli = this;
        SharedPreferences sharedPref = getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        String jsonStr = sharedPref.getString("games", null);
        Gson g = new Gson();
        JsonArray jsonArray = g.fromJson(jsonStr, JsonArray.class);

        Game.listFromJsonArray(mGames,jsonArray);

        // TODO check for unset results in list
        // Game ids to check for:
        ArrayList<Integer> gameIds = new ArrayList<>();

        Scraper.getData("20190829", gameIds);
    }

    @Override
    public void onCompleted(JsonObject jsonObject) {

    }
}