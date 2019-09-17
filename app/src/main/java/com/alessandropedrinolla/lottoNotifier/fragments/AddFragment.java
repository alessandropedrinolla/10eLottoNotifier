package com.alessandropedrinolla.lottoNotifier.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.alessandropedrinolla.lottoNotifier.R;
import com.alessandropedrinolla.lottoNotifier.interfaces.ResultFragmentInterface;
import com.alessandropedrinolla.lottoNotifier.models.Game;
import com.alessandropedrinolla.lottoNotifier.utils.IOUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;

import static android.app.Activity.RESULT_OK;

public class AddFragment extends Fragment {
    private EditText mDateEditText;
    private EditText mGameId;
    private EditText mGameNumbers;
    private IOUtil mIoUtil;
    private Context mContext;
    public ResultFragmentInterface rfi;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mIoUtil = new IOUtil(context);
        mContext = context;
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

        mDateEditText.setOnClickListener(v -> new DatePickerDialog(mContext, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        mGameId = inflatedView.findViewById(R.id.game_id);
        mGameNumbers = inflatedView.findViewById(R.id.game_numbers);

        inflatedView.findViewById(R.id.ocr_button).setOnClickListener(v -> onOcrButtonClick());
        inflatedView.findViewById(R.id.random_button).setOnClickListener(v -> onRandomButtonClick());
        inflatedView.findViewById(R.id.save_button).setOnClickListener(v -> onSaveButtonClick());

        return inflatedView;
    }

    private void onOcrButtonClick() {
        // todo implement this
    }

    private void onRandomButtonClick() {
        mGameNumbers.setError(null);
        HashSet<Integer> randomNums = new HashSet<>();
        Random r = new Random();
        while (randomNums.size() < 10) {
            randomNums.add(r.nextInt(90) + 1);
        }

        ArrayList<Integer> nums = new ArrayList<>(randomNums);
        Collections.sort(nums);

        StringBuilder sb = new StringBuilder();
        for (Integer randomNum : nums) {
            sb.append(randomNum);
            sb.append(" ");
        }
        mGameNumbers.setText(sb.toString());
    }

    private void onSaveButtonClick() {
        String gameNumbersStr = mGameNumbers.getText().toString();

        String dateStr = Game.dateToGameFormat(mDateEditText.getText().toString());
        if (dateStr == null) {
            mDateEditText.setError("Data non impostata o nel formato errato");
            return;
        }

        int gameId = Integer.parseInt(mGameId.getText().toString());

        if (gameId < 1 || gameId > 288)
        {
            mGameId.setError("Numero estrazione fuori dal range 1-288");
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

        mDateEditText.setError(null);
        mGameId.setError(null);
        mGameNumbers.setError(null);
        mGameNumbers.setText("");

        Game game = new Game(dateStr, Integer.parseInt(mGameId.getText().toString()), gameNumbersStr, -1);
        mIoUtil.persistGame(game);

        rfi.refreshList();
    }
}