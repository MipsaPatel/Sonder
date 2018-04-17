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

import java.io.File;
import java.io.IOException;

class GetInitialConfig {
    private Context context;

    GetInitialConfig(Context context) {
        this.context = context;
    }

    void fetchInitialModel() {
        File configFile = new File(context.getFilesDir(), Constants.configFile);
        File paramsFile = new File(context.getFilesDir(), Constants.paramsFile);

        try {
            boolean created = configFile.createNewFile();

            if (paramsFile.createNewFile() || created) {
                JsonObjectRequest objectRequest = new JsonObjectRequest(
                        Request.Method.GET,
                        Constants.serverUrl,
                        null,
                        response -> {
                            try {
                                Pair<JSONObject, JSONArray> parsedModel = ModelUtils.parseModel(response);
                                Util.writeToFile(configFile, parsedModel.first);
                                Util.writeToFile(paramsFile, parsedModel.second);

                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                            initializeModel();
                        },
                        error -> Toast.makeText(context, "Failed to fetch parameters", Toast.LENGTH_SHORT).show()
                );
                SingletonRequest.getInstance(context.getApplicationContext()).addToRequestQueue(objectRequest);
            } else {
                initializeModel();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeModel() {
        try {
            JSONObject config = new JSONObject(Util.readFromFile(context, Constants.configFile));
            JSONArray parameters = new JSONArray(Util.readFromFile(context, Constants.paramsFile));
            Constants.model = Util.createModelFromJSON(config);
            Constants.model.second.second.setParameters(ModelUtils.jsonParametersToArray(parameters));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
