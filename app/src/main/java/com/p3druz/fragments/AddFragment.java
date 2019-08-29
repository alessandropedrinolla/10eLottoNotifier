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

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.p3druz.R;
import com.p3druz.models.PageViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AddFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;
    private NumberPicker mGameId;
    private EditText mGameNumbers;
    private SimpleAdapter mAdapter;
    private ArrayList<HashMap<String, String>> mFields;

    public static AddFragment newInstance(int index) {
        AddFragment fragment = new AddFragment();
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
        View inflatedView = inflater.inflate(R.layout.fragment_add, container, false);

        mGameId = inflatedView.findViewById(R.id.game_id);

        mGameId.setMinValue(1);
        mGameId.setMaxValue(288);
        mGameId.setWrapSelectorWheel(true);

        String[] values = new String[288];
        for (int i = 0; i < values.length; i++) {
            values[i] = String.valueOf(i+1);
        }

        mGameId.setDisplayedValues(values);

        /*np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                //Display the newly selected number from picker
                tv.setText("Selected Number : " + newVal);
            }
        });*/

        mGameNumbers = inflatedView.findViewById(R.id.game_numbers);

        ListView lv = inflatedView.findViewById(R.id.list_view);

        mFields = new ArrayList<>();

        mAdapter = new SimpleAdapter(getActivity(), mFields, R.layout.game_row, new String[]{"gameId", "gameNumbers"}, new int[]{R.id.game_id_field, R.id.game_numbers_field});
        lv.setAdapter(mAdapter);

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

    // add listview items in sharedpreferences
    private void onAddButtonClick(){
        HashMap<String, String> mRow = new HashMap<>();
        mRow.put("gameId", String.valueOf(mGameId.getValue()));
        mRow.put("gameNumbers", mGameNumbers.getText().toString());
        mFields.add(mRow);
        mAdapter.notifyDataSetChanged();

        mGameId.setValue(1);
        mGameNumbers.setText("");
    }

    private void onSaveButtonClick() {
        JSONArray jsonArray = new JSONArray();

        for(HashMap<String,String> row : mFields){
            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put("gameId",row.get("gameId"));
                jsonObj.put("gameNumbers",row.get("gameNumbers"));
                jsonArray.put(jsonObj);
            }catch (JSONException jsonEx)
            {
                jsonEx.printStackTrace();
            }
        }

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("games", jsonArray.toString());
        editor.apply();

        mFields.clear();
        mAdapter.notifyDataSetChanged();
    }
}