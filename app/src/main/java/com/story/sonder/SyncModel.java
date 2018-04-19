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

class SyncModel {
    private Context context;

    SyncModel(Context context) {
        this.context = context;
    }

    void fetchParameters() {
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                Constants.serverUrl, null,
                response -> {
                    try {
                        Pair<JSONObject, JSONArray> parsedModel = ModelUtils.parseModel(response);
                        Constants.model.second.second.mergeParameters(ModelUtils.jsonParametersToArray(parsedModel.second), Constants.alpha);
                        Constants.saveToFile = true;
                        Toast.makeText(context, "Parameters fetched from server", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(context, "Failed to fetch parameters", Toast.LENGTH_SHORT).show();
                    Constants.syncSuccessful = false;
                }
        );
        SingletonRequest.getInstance(context.getApplicationContext()).addToRequestQueue(objectRequest);
    }

    void sendParameters() {
        JSONObject parameters = new JSONObject();

        try {
            parameters.put("params", ModelUtils.parametersToJSONArray(Constants.model.second.second.getParameters()));
            JsonObjectRequest objectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    Constants.serverUrl,
                    parameters,
                    response -> {
                        Toast.makeText(context, "Parameters sent to server", Toast.LENGTH_SHORT).show();
                        Constants.syncSuccessful = true;
                    },
                    error -> {
                        Toast.makeText(context, "Failed to send parameters", Toast.LENGTH_SHORT).show();
                        Constants.syncSuccessful = false;
                    }
            );
            SingletonRequest.getInstance(context.getApplicationContext()).addToRequestQueue(objectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
