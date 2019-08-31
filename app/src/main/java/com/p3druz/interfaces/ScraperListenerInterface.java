package com.p3druz.interfaces;

import org.json.JSONObject;

public interface ScraperListenerInterface {
    void onCompleted(JSONObject results);
}
