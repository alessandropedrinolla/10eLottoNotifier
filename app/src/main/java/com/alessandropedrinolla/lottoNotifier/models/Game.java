package com.alessandropedrinolla.lottoNotifier.models;

import org.hamcrest.core.IsNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

public class Game extends Extraction {
    public final static String[] FIELDS = {"gameDate", "gameId", "gameNumbers", "numbersHit"};

    private final static String DATE_FORMAT = ("yyyyMMdd");
    public final static String DATE_LOCALE_FORMAT = ("dd/MM/yyyy");

    private String mUUID = null;
    private String mDate;
    private int mNumbersHit;

    public Game() {
        super();
    }

    public Game(String date, int id, String numbers, int numbersHit) {
        super(id, numbers);
        this.mDate = date;
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

    public static String dateToGameFormat(String date) {
        Date d;
        try {
            d = new SimpleDateFormat(DATE_LOCALE_FORMAT, Locale.getDefault()).parse(date);
        } catch (ParseException | NullPointerException pEx) {
            pEx.printStackTrace();
            return null;
        }

        return new SimpleDateFormat(Game.DATE_FORMAT, Locale.getDefault()).format(d);
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

    public void checkNumbersHit(String numbers) {
        String[] parts = numbers.split(" ");

        HashSet<Integer> join = new HashSet<>();

        for (String s : parts) {
            join.add(Integer.parseInt(s));
        }

        join.addAll(mNumbers);

        this.mNumbersHit = 30 - join.size();
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public void setDateFromLocaleFormat(String date) {
        mDate = dateToGameFormat(date);
    }

    public String getUUID() {
        return mUUID;
    }

    public void setUUID(String UUID) {
        this.mUUID = UUID;
    }

    public boolean isRecognized() {
        return mDate != null && (mId >= 1 && mId <= 288) && mNumbers != null;
    }
}
