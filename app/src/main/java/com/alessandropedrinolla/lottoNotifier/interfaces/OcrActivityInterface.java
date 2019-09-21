package com.alessandropedrinolla.lottoNotifier.interfaces;

public interface OcrActivityInterface {
    void onDateRecognized(String date);
    void onIdRecognized(int id);
    void onNumbersRecognized(String numbers);

    void onAnalysisCompleted();
}
