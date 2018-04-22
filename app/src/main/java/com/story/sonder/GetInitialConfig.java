package com.story.sonder;

import android.content.Context;
import android.os.AsyncTask;
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

import lombok.AllArgsConstructor;

@AllArgsConstructor
class GetInitialConfig {
    private Context context;

    void fetchInitialModel() {
        File configFile = new File(context.getFilesDir(), Constants.configFile);
        File paramsFile = new File(context.getFilesDir(), Constants.paramsFile);

        try {
            boolean created = configFile.createNewFile();

            if (paramsFile.createNewFile() || created) {
                JsonObjectRequest request = new JsonObjectRequest(
                        Request.Method.GET,
                        Constants.serverUrl,
                        null,
                        response -> {
                            try {
                                Pair<JSONObject, JSONArray> model = ModelUtils.parseModel(response);
                                Util.writeToFile(configFile, model.first);
                                Util.writeToFile(paramsFile, model.second);

                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                            initializeModel();
                        },
                        error -> Toast.makeText(
                                context,
                                "Failed to fetch parameters",
                                Toast.LENGTH_SHORT
                        ).show()
                );
                SingletonRequest.getInstance(context.getApplicationContext())
                        .addToRequestQueue(request);
            }
            else {
                AsyncTask.execute(this::initializeModel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeModel() {
        try {
            JSONObject config = new JSONObject(Util.readFromFile(context, Constants.configFile));
            JSONArray parameters = new JSONArray(Util.readFromFile(context, Constants.paramsFile));
            AppResources.model = Util.createModelFromJSON(config);
            AppResources.model.getOptimizer().setParameters(
                    ModelUtils.jsonParametersToArray(parameters)
            );
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
