package com.alessandropedrinolla.lottoNotifier.fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.alessandropedrinolla.lottoNotifier.R;
import com.alessandropedrinolla.lottoNotifier.activities.OcrActivity;
import com.alessandropedrinolla.lottoNotifier.interfaces.ResultFragmentInterface;
import com.alessandropedrinolla.lottoNotifier.models.Game;
import com.alessandropedrinolla.lottoNotifier.utils.IOUtil;
import com.alessandropedrinolla.lottoNotifier.utils.ImageAnalyzer;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;

import static android.app.Activity.RESULT_OK;
import static androidx.core.content.ContextCompat.checkSelfPermission;

public class AddFragment extends Fragment {
    private EditText mGameDateEditText;
    private EditText mGameIdEditText;
    private EditText mGameNumbersEditText;
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_add, container, false);

        final Calendar myCalendar = Calendar.getInstance();

        mGameDateEditText = inflatedView.findViewById(R.id.game_date);
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String myFormat = Game.DATE_LOCALE_FORMAT;
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

            mGameDateEditText.setText(sdf.format(myCalendar.getTime()));
        };

        mGameDateEditText.setOnClickListener(v -> new DatePickerDialog(mContext, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        mGameIdEditText = inflatedView.findViewById(R.id.game_id);
        mGameNumbersEditText = inflatedView.findViewById(R.id.game_numbers);

        inflatedView.findViewById(R.id.ocr_button).setOnClickListener(v -> onOcrButtonClick());
        inflatedView.findViewById(R.id.random_button).setOnClickListener(v -> onRandomButtonClick());
        inflatedView.findViewById(R.id.save_button).setOnClickListener(v -> onSaveButtonClick());
        inflatedView.findViewById(R.id.reset_button).setOnClickListener(v -> onResetButtonClick());

        return inflatedView;
    }

    private void onResetButtonClick() {
        mGameDateEditText.setText("");
        mGameIdEditText.setText("");
        mGameNumbersEditText.setText("");
    }

    private void onOcrButtonClick() {
        if (checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 1);
            return;
        }

        Intent i = new Intent();
        i.setClass(getContext(),OcrActivity.class);

        startActivityForResult(i,1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            Game g = new Gson().fromJson(data.getStringExtra("game"), Game.class);

            mGameDateEditText.setText(g.getDate());
            mGameIdEditText.setText(g.getId() + "");
            mGameNumbersEditText.setText(g.getNumbersAsString());
        }
    }

    private void onRandomButtonClick() {
        mGameNumbersEditText.setError(null);
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
        mGameNumbersEditText.setText(sb.toString());
    }

    private void onSaveButtonClick() {
        String gameNumbersStr = mGameNumbersEditText.getText().toString();

        String dateStr = Game.dateToGameFormat(mGameDateEditText.getText().toString());
        if (dateStr == null) {
            mGameDateEditText.setError("Data non impostata o nel formato errato");
            return;
        }

        int gameId = Integer.parseInt(mGameIdEditText.getText().toString());

        if (gameId < 1 || gameId > 288) {
            mGameIdEditText.setError("Numero estrazione fuori dal range 1-288");
            return;
        }

        switch (Game.checkNumbersString(gameNumbersStr)) {
            case 1:
                mGameNumbersEditText.setError("Gli elementi non sono 10");
                return;
            case 2:
                mGameNumbersEditText.setError("Un elemento non è un numero");
                return;
            case 3:
                mGameNumbersEditText.setError("Un numero è fuori dal range 1-90");
                return;
            case 4:
                mGameNumbersEditText.setError("I numeri non sono univoci");
                return;
        }

        mGameDateEditText.setError(null);
        mGameIdEditText.setError(null);
        mGameNumbersEditText.setError(null);
        mGameNumbersEditText.setText("");

        Game game = new Game(dateStr, Integer.parseInt(mGameIdEditText.getText().toString()), gameNumbersStr, -1);
        mIoUtil.persistGame(game);

        rfi.refreshList();
    }
}