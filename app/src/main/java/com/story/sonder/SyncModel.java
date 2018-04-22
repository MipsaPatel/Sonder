package com.story.sonder;

import android.content.Context;
import android.support.v4.util.Pair;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.story.sonder.model.ModelUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import lombok.AllArgsConstructor;

@AllArgsConstructor
class SyncModel {
    private static final String fetchSuccess = "Parameters fetched from server";
    private static final String fetchFail = "Failed to fetch parameters";

    private static final String sendSuccess = "Parameters sent to server";
    private static final String sendFail = "Failed to send parameters";

    private final Context context;

    void fetchParameters() {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                Constants.serverUrl,
                null,
                response -> {
                    try {
                        Pair<JSONObject, JSONArray> model = ModelUtils.parseModel(response);
                        AppResources.model.mergeParameters(
                                ModelUtils.jsonParametersToArray(model.second)
                        );
                        Constants.saveToFile = true;
                        Toast.makeText(context, fetchSuccess, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(context, fetchFail, Toast.LENGTH_SHORT).show();
                    Constants.syncSuccessful = false;
                }
        );
        SingletonRequest.getInstance(context.getApplicationContext()).addToRequestQueue(request);
    }

    void sendParameters() {
        JSONObject parameters = new JSONObject();

        try {
            JSONArray params = ModelUtils.parametersToJSONArray(AppResources.model.getParameters());
            parameters.put("params", params);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Constants.serverUrl,
                parameters,
                response -> {
                    Toast.makeText(context, sendSuccess, Toast.LENGTH_SHORT).show();
                    Constants.syncSuccessful = true;
                },
                error -> {
                    Toast.makeText(context, sendFail, Toast.LENGTH_SHORT).show();
                    Constants.syncSuccessful = false;
                }
        );
        SingletonRequest.getInstance(context.getApplicationContext()).addToRequestQueue(request);
    }
}
