package com.alessandropedrinolla.lottoNotifier.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.alessandropedrinolla.lottoNotifier.R;
import com.alessandropedrinolla.lottoNotifier.interfaces.GameAdapterListenerInterface;
import com.alessandropedrinolla.lottoNotifier.models.Game;

import java.util.ArrayList;

public class GamesAdapter extends ArrayAdapter<Game> {
    public GameAdapterListenerInterface gali;
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
        Button gameDelete = convertView.findViewById(R.id.game_delete);
        // Populate the data into the template view using the data object
        gameDateField.setText(Game.dateToLocaleFormat(game.getDate()));
        gameIdField.setText(String.valueOf(game.getId()));
        gameNumbersField.setText(game.getNumbersAsString());
        gameNumbersHit.setText(String.valueOf(game.getNumbersHit()));
        gameDelete.setOnClickListener(view -> gali.deleteGame(game));

        switch (game.getNumbersHit()){
            case -1: convertView.setBackgroundColor(Color.TRANSPARENT); break;
            case 1: case 2:case 3:case 4:convertView.setBackgroundColor(Color.RED);break;
            default: convertView.setBackgroundColor(Color.GREEN);
        }

        // Return the completed view to render on screen
        return convertView;
    }
}