package com.p3druz.models;

import java.net.Inet4Address;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class Game {
    public final static String[] FIELDS = {"gameDate", "gameId", "gameNumbers", "numbersHit"};
    public final static String DATE_FORMAT = ("YYYYMMdd");
    public final static String DATE_LOCALE_FORMAT = ("dd/MM/YYYY");

    private Date mDate;
    private int mId;
    private Set<Integer> mNumbers;
    private int mNumbersHit;

    public Game(int id, Set<Integer> numbers, int numbersHit) {
        this.mId = id;
        this.mNumbers = numbers;
        this.mNumbersHit = numbersHit;
    }

    public Game(String date, int id, String numbers, int numbersHit) {
        try {
            this.mDate = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).parse(date);
        }catch (ParseException pe){
            pe.printStackTrace();
        }
        this.mId = id;
        String[] parts = numbers.split(" ");
        this.mNumbers = new HashSet<>();
        for (String part : parts)
            this.mNumbers.add(Integer.parseInt(part));
        this.mNumbersHit = numbersHit;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public Set<Integer> getNumbers() {
        return mNumbers;
    }

    public String getNumbersAsString() {
        String numbers = "";
        StringBuilder sb = new StringBuilder();
        for (int n : mNumbers) {
            sb.append(n);
            sb.append(" ");
        }
        return sb.toString();
    }

    public void setNumbers(Set<Integer> numbers) {
        this.mNumbers = numbers;
    }

    public int getNumbersHit() {
        return mNumbersHit;
    }

    public void setNumbersHit(int numbersHit) {
        this.mNumbersHit = numbersHit;
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

    public static String dateToLocaleFormat(String date){
        Date d;
        try {
            d = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).parse(date);
        }catch (ParseException pe){
            pe.printStackTrace();
            return null;
        }

        return new SimpleDateFormat(Game.DATE_LOCALE_FORMAT, Locale.getDefault()).format(d);
    }

    public static String dateToGameFormat(String date){
        Date d;
        try {
            // TODO FIX DIS
            d = new SimpleDateFormat(DATE_LOCALE_FORMAT, Locale.getDefault()).parse(date);
        }catch (ParseException pe){
            pe.printStackTrace();
            return null;
        }

        return new SimpleDateFormat(Game.DATE_FORMAT, Locale.getDefault()).format(d);
    }

    public Date getDate() {
        return mDate;
    }

    public String getDateAsString() {
        return new SimpleDateFormat(Game.DATE_FORMAT, Locale.getDefault()).format(mDate);
    }

    public String getDateAsStringLocale() {
        return new SimpleDateFormat(Game.DATE_LOCALE_FORMAT, Locale.getDefault()).format(mDate);
    }

    public void setDate(Date date) {
        this.mDate = date;
    }
}
