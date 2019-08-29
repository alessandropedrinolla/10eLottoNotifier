package com.p3druz.models;

public class Game {
    private int mId;
    private int[] mNumbers;

    public Game(int id, int[] numbers){
        this.mId = id;
        this.mNumbers = numbers;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public int[] getNumbers() {
        return mNumbers;
    }

    public void setNumbers(int[] numbers) {
        this.mNumbers = numbers;
    }
}
