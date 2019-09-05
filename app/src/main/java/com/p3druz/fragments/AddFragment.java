package com.p3druz.fragments;

import android.app.DatePickerDialog;
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
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.p3druz.R;
import com.p3druz.models.Game;
import com.p3druz.utils.SharedPreferencesUtil;

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

public class AddFragment extends Fragment {
    private EditText mDateEditText;
    private EditText mGameId;
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
        mGameNumbers = inflatedView.findViewById(R.id.game_numbers);

        mAdapter = new SimpleAdapter(getActivity(), mFields, R.layout.game_row, new String[]{"gameId", "gameNumbers"}, new int[]{R.id.game_id_field, R.id.game_numbers_field});
        ((ListView) inflatedView.findViewById(R.id.list_view)).setAdapter(mAdapter);

        inflatedView.findViewById(R.id.random_button).setOnClickListener(v -> onRandomButtonClick());
        inflatedView.findViewById(R.id.add_button).setOnClickListener(v -> onAddButtonClick());
        inflatedView.findViewById(R.id.save_button).setOnClickListener(v -> onSaveButtonClick());

        return inflatedView;
    }

    private void onRandomButtonClick() {
        HashSet<Integer> randomNums = new HashSet<>();
        Random r = new Random();
        while(randomNums.size()<10){
            randomNums.add(r.nextInt(90)+1);
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
        mRow.put(Game.FIELDS[1], mGameId.getText().toString());
        mRow.put(Game.FIELDS[2], gameNumbersStr);
        mRow.put(Game.FIELDS[3], "-1");
        mFields.add(mRow);
        mAdapter.notifyDataSetChanged();

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

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

}