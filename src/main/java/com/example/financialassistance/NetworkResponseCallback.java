package com.example.financialassistance;

import org.json.JSONObject;

public interface NetworkResponseCallback {
    public void success(JSONObject json);
    public void failure();
}