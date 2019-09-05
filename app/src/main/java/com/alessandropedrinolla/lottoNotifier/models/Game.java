package com.alessandropedrinolla.lottoNotifier.models;

import android.media.MediaPlayer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

public class Game extends Extraction{
    public final static String[] FIELDS = {"gameDate", "gameId", "gameNumbers", "numbersHit"};

    private final static String DATE_FORMAT = ("yyyyMMdd");
    public final static String DATE_LOCALE_FORMAT = ("dd/MM/yyyy");

    private String mDate;
    private int mNumbersHit;

    public Game(String date, int id, String numbers, int numbersHit) {
        super(id,numbers);
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

    public String getDateLocaleFormat() {
        Date d;
        try {
            d = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).parse(mDate);
        }
        catch (ParseException pe){
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
}
