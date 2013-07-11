package com.arqui.listeners;
import org.json.JSONObject;

public interface HttpResponseListener {
    public void onResponse(JSONObject result, Integer status);
}