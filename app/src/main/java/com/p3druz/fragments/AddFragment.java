package com.p3druz.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.p3druz.R;
import com.p3druz.models.Config;
import com.p3druz.models.Game;
import com.p3druz.utils.SharedPreferencesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class AddFragment extends Fragment {
    private EditText mDateEditText;
    private NumberPicker mGameId;
    private EditText mGameNumbers;
    private SimpleAdapter mAdapter;
    private ArrayList<HashMap<String, String>> mFields;
    private SharedPreferencesUtil mSharedPreferencesUtil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFields = new ArrayList<>();
        mSharedPreferencesUtil = new SharedPreferencesUtil(getActivity());
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_add, container, false);

        final Calendar myCalendar = Calendar.getInstance();

        mDateEditText = inflatedView.findViewById(R.id.game_date);
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String myFormat = Game.DATE_LOCALE_FORMAT;
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

            mDateEditText.setText(sdf.format(myCalendar.getTime()));
        };

        mDateEditText.setOnClickListener(v -> new DatePickerDialog(getContext(), date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        mGameId = inflatedView.findViewById(R.id.game_id);
        setUpNumberPicker();
        mGameNumbers = inflatedView.findViewById(R.id.game_numbers);
        mGameNumbers.setText("10 12 33 45 54 66 67 78 88 90");

        mAdapter = new SimpleAdapter(getActivity(), mFields, R.layout.game_row, new String[]{"gameId", "gameNumbers"}, new int[]{R.id.game_id_field, R.id.game_numbers_field});
        ((ListView) inflatedView.findViewById(R.id.list_view)).setAdapter(mAdapter);

        inflatedView.findViewById(R.id.add_button).setOnClickListener(v -> onAddButtonClick());

        inflatedView.findViewById(R.id.save_button).setOnClickListener(v -> onSaveButtonClick());

        return inflatedView;
    }

    private void setUpNumberPicker() {
        mGameId.setMinValue(1);
        mGameId.setMaxValue(288);
        mGameId.setWrapSelectorWheel(true);

        String[] values = new String[287];
        for (int i = 0; i < values.length; i++) {
            values[i] = String.valueOf(i + 1);
        }

        mGameId.setDisplayedValues(values);
    }

    private void onAddButtonClick() {
        String gameNumbersStr = mGameNumbers.getText().toString();

        String dateStr = Game.dateToGameFormat(mDateEditText.getText().toString());
        if (dateStr == null) {
            mDateEditText.setError("Data non impostata o sbagliata");
            return;
        }

        switch (Game.checkNumbersString(gameNumbersStr)) {
            case 1:
                mGameNumbers.setError("Gli elementi non sono 10");
                return;
            case 2:
                mGameNumbers.setError("Un elemento non è un numero");
                return;
            case 3:
                mGameNumbers.setError("Un numero è fuori dal range 1-90");
                return;
            case 4:
                mGameNumbers.setError("I numeri non sono univoci");
                return;
        }

        HashMap<String, String> mRow = new HashMap<>();
        mRow.put(Game.FIELDS[0], dateStr);
        mRow.put(Game.FIELDS[1], String.valueOf(mGameId.getValue()));
        mRow.put(Game.FIELDS[2], gameNumbersStr);
        mRow.put(Game.FIELDS[3], "-1");
        mFields.add(mRow);
        mAdapter.notifyDataSetChanged();

        mGameId.setValue(1);
        mGameNumbers.setText("");
    }

    private void onSaveButtonClick() {
        if (mFields.size() == 0) return;
        ArrayList<Game> games = new ArrayList<>();
        for (HashMap<String, String> row : mFields) {
            Game g = new Game(row.get(Game.FIELDS[0]),Integer.parseInt(row.get(Game.FIELDS[1])), row.get(Game.FIELDS[2]), Integer.parseInt(row.get(Game.FIELDS[3])));
            games.add(g);
        }
        mSharedPreferencesUtil.saveGames(games);
        mFields.clear();
        mAdapter.notifyDataSetChanged();
    }
}