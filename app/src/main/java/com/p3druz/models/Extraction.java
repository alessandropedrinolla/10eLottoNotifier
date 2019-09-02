package com.p3druz.models;

import java.util.HashSet;
import java.util.Set;

public class Extraction {
    private int mId;
    private HashSet<Integer> mNumbers;

    public Extraction(int id, String numbers) {
        this.mId = id;
        String[] parts = numbers.split(" ");
        this.mNumbers = new HashSet<>();
        for (String part : parts) {
            this.mNumbers.add(Integer.parseInt(part));
        }
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
        StringBuilder sb = new StringBuilder();
        for (int n : mNumbers) {
            sb.append(n);
            sb.append(" ");
        }
        return sb.toString();
    }
}
