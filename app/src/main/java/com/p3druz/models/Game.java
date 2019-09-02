package com.p3druz.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class Game extends Extraction{
    public final static String[] FIELDS = {"gameDate", "gameId", "gameNumbers", "numbersHit"};

    private final static String DATE_FORMAT = ("yyyyMMdd");
    public final static String DATE_LOCALE_FORMAT = ("dd/MM/yyyy");

    private Date mDate;
    private int mId;
    private Set<Integer> mNumbers;
    private int mNumbersHit;

    public Game(String date, int id, String numbers, int numbersHit) {
        super(id,numbers);

        try {
            this.mDate = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).parse(date);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        this.mNumbersHit = numbersHit;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public int getNumbersHit() {
        return mNumbersHit;
    }

    /**
     * Error codes:
     * 1 - there are not 10 elements
     * 2 - an element is not a number
     * 3 - a number is not in range 1-90
     * 4 - numbers are not unique
     *
     * @param str
     * @return errorCode
     */
    public static int checkNumbersString(String str) {
        final String[] parts = str.split(" ");

        if (parts.length != 10) {
            return 1;
        } else {
            for (String part : parts) {
                int num;
                try {
                    num = Integer.parseInt(part);
                } catch (NumberFormatException nfe) {
                    return 2;
                }
                if (num < 1 || num > 90) {
                    return 3;
                }
            }

            if (parts.length != new HashSet<>(Arrays.asList(parts)).size())
                return 4;
        }
        return 0;
    }

    public static String dateToLocaleFormat(String date) {
        Date d;
        try {
            d = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).parse(date);
        } catch (ParseException pe) {
            pe.printStackTrace();
            return null;
        }

        return new SimpleDateFormat(Game.DATE_LOCALE_FORMAT, Locale.getDefault()).format(d);
    }

    public static String dateToGameFormat(String date) {
        Date d;
        try {
            d = new SimpleDateFormat(DATE_LOCALE_FORMAT, Locale.getDefault()).parse(date);
        } catch (ParseException pe) {
            pe.printStackTrace();
            return null;
        }

        return new SimpleDateFormat(Game.DATE_FORMAT, Locale.getDefault()).format(d);
    }

    public String getDateAsString() {
        return new SimpleDateFormat(Game.DATE_FORMAT, Locale.getDefault()).format(mDate);
    }

    public String getDateAsStringLocale() {
        return new SimpleDateFormat(Game.DATE_LOCALE_FORMAT, Locale.getDefault()).format(mDate);
    }

    public static void JSONArrayToList(ArrayList<Game> gameList, JSONArray ja) {
        for (int i = 0; i < ja.length(); i++) {
            Game game;
            try {
                game = new Game(ja.getJSONObject(i).getString(Game.FIELDS[0]), ja.getJSONObject(i).getInt(Game.FIELDS[1]), ja.getJSONObject(i).getString(Game.FIELDS[2]).replace("\"", ""), ja.getJSONObject(i).getInt(Game.FIELDS[3]));
            } catch (JSONException jsonEx) {
                jsonEx.printStackTrace();
                return;
            }

            gameList.add(game);
        }
    }

    public static JSONArray ListToJSONArray(ArrayList<Game> gameList) {
        JSONArray jsonArray = new JSONArray();

        for (Game g : gameList) {
            JSONObject jsonObj = new JSONObject();

            try {
                jsonObj.put(FIELDS[0], g.getDateAsString());
                jsonObj.put(FIELDS[1], g.getId());
                jsonObj.put(FIELDS[2], g.getNumbersAsString());
                jsonObj.put(FIELDS[3], g.getNumbersHit());
            } catch (JSONException jsonEx) {
                jsonEx.printStackTrace();
                return null;
            }

            jsonArray.put(jsonObj);
        }
        return jsonArray;
    }

    public void checkNumbersHit(String numbers) {
        String[] parts = numbers.split(" ");

        HashSet<Integer> join = new HashSet<>();

        for (String s : parts) {
            join.add(Integer.parseInt(s));
        }

        join.addAll(mNumbers);

        this.mNumbersHit = 30 - join.size();
    }
}
