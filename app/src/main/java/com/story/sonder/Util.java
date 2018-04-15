package com.story.sonder;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.util.Pair;
import android.view.Window;

import com.story.sonder.model.ModelUtils;
import com.story.sonder.model.layer.ILayer;
import com.story.sonder.model.layer.Reshape;
import com.story.sonder.model.layer.Sequence;
import com.story.sonder.model.loss.ILoss;
import com.story.sonder.model.optimizer.IOptimizer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

class Util {
    static Dialog createDialog(Context context, int layoutId) {
        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow())
                .setBackgroundDrawable(new ColorDrawable(Color.rgb(30, 30, 30)));
        dialog.setContentView(layoutId);
        return dialog;
    }

    static Pair<ILayer, Pair<ILoss, IOptimizer>> createModelFromJSON(JSONObject conf) throws JSONException {
        Sequence features = (Sequence) ModelUtils.getLayer("Sequence", conf.getJSONObject("features"));
        Reshape reshape = ModelUtils.reshape(conf.getJSONArray("reshape"));
        Sequence classifier = (Sequence) ModelUtils.getLayer("Sequence", conf.getJSONObject("classifier"));
        ILayer model = new Sequence(features, reshape, classifier);

        JSONObject lossObject = conf.getJSONObject("loss");
        ILoss loss = ModelUtils.getLoss(lossObject.getString("name"));

        JSONObject optimizerObject = conf.getJSONObject("optimizer");
        IOptimizer optimizer = ModelUtils.getOptimizer(
                optimizerObject.getString("name"),
                model,
                optimizerObject.getJSONObject("parameters"));
        return Pair.create(model, Pair.create(loss, optimizer));
    }

    static Pair<ILayer, Pair<ILoss, IOptimizer>> createModelFromJSONString(String json) throws JSONException {
        return createModelFromJSON(new JSONObject(json));
    }
}
