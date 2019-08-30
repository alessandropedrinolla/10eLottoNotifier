package com.p3druz.models;

import java.net.Inet4Address;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Game {
    private int mId;
    private Set<Integer> mNumbers;
    private int mNumbersHit;
    public final static String[] fields = {"gameId","gameNumbers","numbersHit"};

    public Game(int id, Set<Integer> numbers, int numbersHit){
        this.mId = id;
        this.mNumbers = numbers;
        this.mNumbersHit = numbersHit;
    }

    public Game(int id, String numbers, int numbersHit){
        this.mId = id;
        String[] parts = numbers.split(" ");
        for(String part : parts)
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
     * @param str
     * @return errorCode
     */
    public static int checkNumbersString(String str){
        final String[] parts = str.split(" ");

        if (parts.length != 10) {
            return 1;
        } else {
            for(String part : parts){
                int num;
                try {
                    num = Integer.parseInt(part);
                }catch (NumberFormatException nfe){
                    return 2;
                }
                if(num < 1 || num > 90){
                    return 3;
                }
            }

            if(parts.length != new HashSet<>(Arrays.asList(parts)).size())
                return 4;
        }
        return 0;
    }
}
