package com.p3druz.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.p3druz.R;
import com.p3druz.interfaces.ScraperListenerInterface;
import com.p3druz.models.Game;
import com.p3druz.models.PageViewModel;
import com.p3druz.network.Scraper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ResultFragment extends Fragment implements ScraperListenerInterface {
    private static final String ARG_SECTION_NUMBER = "section_number";

    private ListView mListView;
    private SimpleAdapter mAdapter;
    private ArrayList<Game> mGames;
    private ArrayList<HashMap<String, String>> mFields;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PageViewModel pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);

        mFields = new ArrayList<>();
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_result, container, false);

        inflatedView.findViewById(R.id.refresh_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadListView();
            }
        });

        inflatedView.findViewById(R.id.check_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkList();
            }
        });

        mListView = inflatedView.findViewById(R.id.list_view);

        setupListView();
        loadListView();

        return inflatedView;
    }

    private void setupListView() {
        mAdapter = new SimpleAdapter(getActivity(), mFields, R.layout.result_row, new String[]{Game.fields[0], Game.fields[1], Game.fields[2]}, new int[]{R.id.game_id_field, R.id.game_numbers_field, R.id.game_numbers_hit});
        mListView.setAdapter(mAdapter);
    }

    private void loadListView() {
        SharedPreferences sharedPref = getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        String jsonStr = sharedPref.getString("games", null);
        Gson g = new Gson();
        JsonArray jsonArray = g.fromJson(jsonStr, JsonArray.class);

        if (jsonArray == null) return;

        mFields.clear();

        mFields.add(new HashMap<String, String>() {{
            put(Game.fields[0], "N° estrazione");
            put(Game.fields[1], "Numeri giocati");
            put(Game.fields[2], "Numeri indovinati");
        }});

        for (JsonElement jsonElem : jsonArray) {
            JsonObject jsonObject = jsonElem.getAsJsonObject();
            HashMap<String, String> mRow = new HashMap<>();
            for (String field : Game.fields)
                mRow.put(field, jsonObject.get(field).toString());
            mFields.add(mRow);
        }

        mAdapter.notifyDataSetChanged();
    }

    private void checkList() {
        // TODO set up dis shit
        Scraper.sli = this;
        SharedPreferences sharedPref = getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        String jsonStr = sharedPref.getString("games", null);
        Gson g = new Gson();
        JsonArray jsonArray = g.fromJson(jsonStr, JsonArray.class);

        // Game ids to check for:
        ArrayList<Integer> gameIds = new ArrayList<>();
        ArrayList<Game> games = new ArrayList<>();

        for (JsonElement e : jsonArray) {
            JsonObject jsonObject = e.getAsJsonObject();
            Game game = new Game(jsonObject.get(Game.fields[0]).getAsInt(), jsonObject.get(Game.fields[1]).getAsString(), jsonObject.get(Game.fields[2]).getAsInt());
            games.add(game);

            if (game.getNumbersHit() == -1)
                gameIds.add(game.getId());
        }

        Scraper.getData("20190829", gameIds);
    }

    @Override
    public void onCompleted(JsonObject jsonObject) {

    }
}