package com.alessandropedrinolla.lottoNotifier.models;

import java.util.Hashtable;

public class ScrapeData {
    private String mDate;
    private Hashtable<Integer, Extraction> mExtractions;

    public ScrapeData(String date, Hashtable<Integer, Extraction> extractions){
        this.mDate = date;
        this.mExtractions = extractions;
    }

    public String getDate() {
        return mDate;
    }

    public Hashtable<Integer, Extraction> getExtractions() {
        return mExtractions;
    }

}
