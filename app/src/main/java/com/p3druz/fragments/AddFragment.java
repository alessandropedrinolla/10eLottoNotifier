package com.p3druz.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.p3druz.R;
import com.p3druz.models.Game;
import com.p3druz.models.PageViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class AddFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private NumberPicker mGameId;
    private EditText mGameNumbers;
    private SimpleAdapter mAdapter;
    private ArrayList<HashMap<String, String>> mFields;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFields = new ArrayList<>();

        PageViewModel pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
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
        View inflatedView = inflater.inflate(R.layout.fragment_add, container, false);

        mGameId = inflatedView.findViewById(R.id.game_id);
        setUpNumberPicker();
        mGameNumbers = inflatedView.findViewById(R.id.game_numbers);

        mAdapter = new SimpleAdapter(getActivity(), mFields, R.layout.game_row, new String[]{"gameId", "gameNumbers"}, new int[]{R.id.game_id_field, R.id.game_numbers_field});
        ((ListView)inflatedView.findViewById(R.id.list_view)).setAdapter(mAdapter);

        inflatedView.findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddButtonClick();
            }
        });

        inflatedView.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveButtonClick();
            }
        });

        return inflatedView;
    }

    private void setUpNumberPicker(){
        mGameId.setMinValue(1);
        mGameId.setMaxValue(288);
        mGameId.setWrapSelectorWheel(true);

        String[] values = new String[288];
        for (int i = 0; i < values.length; i++) {
            values[i] = String.valueOf(i+1);
        }

        mGameId.setDisplayedValues(values);
    }

    private void onAddButtonClick() {
        String gameNumbersStr = mGameNumbers.getText().toString();

        switch(Game.checkNumbersString(gameNumbersStr)){
            case 1: mGameNumbers.setError("Gli elementi non sono 10"); return;
            case 2: mGameNumbers.setError("Un elemento non è un numero"); return;
            case 3: mGameNumbers.setError("Un numero è fuori dal range 1-90"); return;
            case 4: mGameNumbers.setError("I numeri non sono univoci"); return;
        }

        HashMap<String, String> mRow = new HashMap<>();
        mRow.put(Game.fields[0], String.valueOf(mGameId.getValue()));
        mRow.put(Game.fields[1], gameNumbersStr);
        mRow.put(Game.fields[2], "not set");
        mFields.add(mRow);
        mAdapter.notifyDataSetChanged();

        mGameId.setValue(1);
        mGameNumbers.setText("");
    }

    private void onSaveButtonClick() {
        if(mFields.size() == 0) return;

        JsonArray jsonArray = new JsonArray();

        for(HashMap<String,String> row : mFields) {
            JsonObject jsonObj = new JsonObject();
            for(String field : Game.fields)
                jsonObj.addProperty(field, row.get(field));
            jsonArray.add(jsonObj);
        }

        // Problem: join two JsonArray - faster solution: join them as strings
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String jsonStrOld = sharedPreferences.getString("games", null);
        String jsonStrNew = jsonArray.toString();

        if(jsonStrOld != null) {
            // Removes first and last square bracket
            jsonStrOld = jsonStrOld.substring(1, jsonStrOld.length() - 1);
            // Remove last bracket
            jsonStrNew = jsonStrNew.substring(0, jsonStrNew.length() - 1);
            // Insert old json and close bracket
            jsonStrNew = jsonStrNew + "," + jsonStrOld + "]";
        }

        editor.putString("games", jsonStrNew);
        editor.apply();

        mFields.clear();
        mAdapter.notifyDataSetChanged();
    }
}