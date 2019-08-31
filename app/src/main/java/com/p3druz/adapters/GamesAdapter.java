package com.p3druz.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.p3druz.R;
import com.p3druz.models.Game;

import java.util.ArrayList;
import java.util.List;

public class GamesAdapter extends ArrayAdapter<Game> {
    public GamesAdapter(Context context, ArrayList<Game> games) {
        super(context, 0, games);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Game game = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.result_row, parent, false);
        }
        // Lookup view for data population
        TextView gameDateField = convertView.findViewById(R.id.game_date_field);
        TextView gameIdField = convertView.findViewById(R.id.game_id_field);
        TextView gameNumbersField = convertView.findViewById(R.id.game_numbers_field);
        TextView gameNumbersHit = convertView.findViewById(R.id.game_numbers_hit);
        // Populate the data into the template view using the data object
        gameDateField.setText(game.getDateAsStringLocale());
        gameIdField.setText(String.valueOf(game.getId()));
        gameNumbersField.setText(game.getNumbersAsString());
        gameNumbersHit.setText(String.valueOf(game.getNumbersHit()));
        // Return the completed view to render on screen
        return convertView;
    }
}