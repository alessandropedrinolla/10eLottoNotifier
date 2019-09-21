package com.alessandropedrinolla.lottoNotifier.models;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Extraction {
    protected int mId;
    protected HashSet<Integer> mNumbers;

    public Extraction(){}

    public Extraction(int id, String numbers) {
        this.mId = id;
        setNumbers(numbers);
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    /**
     * Error codes:<br />
     * 1 - there are not 10 elements<br />
     * 2 - an element is not a number<br />
     * 3 - a number is not in range 1-90<br />
     * 4 - numbers are not unique<br />
     *
     * @param numbers string of numbers to check
     * @return errorCode
     */
    public static int checkNumbersString(String numbers) {
        final String[] parts = numbers.split(" ");

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

    public String getNumbersAsString() {
        if(mNumbers == null)
            return null;

        StringBuilder sb = new StringBuilder();
        for (int n : mNumbers) {
            sb.append(n);
            sb.append(" ");
        }
        return sb.toString();
    }

    public void setNumbers(HashSet<Integer> numbers){
        this.mNumbers = numbers;
    }

    public void setNumbers(String numbers){
        String[] parts = numbers.split(" ");
        this.mNumbers = new HashSet<>();
        for (String part : parts) {
            this.mNumbers.add(Integer.parseInt(part));
        }
    }
}