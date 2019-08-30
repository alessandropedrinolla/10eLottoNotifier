package com.p3druz.interfaces;

import com.google.gson.JsonObject;

public interface ScraperListenerInterface {
    void onCompleted(JsonObject results);
}
