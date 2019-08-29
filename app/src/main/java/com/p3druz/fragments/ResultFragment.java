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
import com.google.gson.JsonObject;
import com.p3druz.R;
import com.p3druz.models.Game;
import com.p3druz.models.PageViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ResultFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;
    private ListView mListView;
    private SimpleAdapter mAdapter;
    private ArrayList<HashMap<String, String>> mFields;

    public static ResultFragment newInstance(int index) {
        ResultFragment fragment = new ResultFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_result, container, false);

        mListView = inflatedView.findViewById(R.id.list_view);

        loadListView();

        return inflatedView;
    }

    private void loadListView() {
        SharedPreferences sharedPref = getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        String jsonStr = sharedPref.getString("games", null);
        Gson g = new Gson();
        JSONArray jsonArray = g.fromJson(jsonStr, JSONArray.class);

        if(jsonArray == null) return;

        /*Iterator<String> keys = jsonData.keys();

        while(keys.hasNext()) {
            String key = keys.next();
            try {
                if (jsonData.get(key) instanceof JSONObject) {
                    HashMap<String, String> mRow = new HashMap<>();
                    mRow.put("gameId", String.valueOf(jsonData.get(key)));
                    mRow.put("gameNumbers", String.valueOf(jsonData.get(key)));
                    mRow.put("numberHit", "0");
                    mFields.add(mRow);
                }
            }catch (JSONException jsonEx){
                jsonEx.printStackTrace();
            }
        }*/

        mAdapter = new SimpleAdapter(getActivity(), mFields, R.layout.game_row, new String[]{"gameId", "gameNumbers","numberHit"}, new int[]{R.id.game_id_field, R.id.game_numbers_field, R.id.number_hit});
        mListView.setAdapter(mAdapter);
    }
}