package com.story.sonder.model.layer;

import android.support.v4.util.Pair;

import com.story.sonder.model.Tensor;

import java.util.List;

public interface ILayer {
    Pair<Tensor, Object> forward(Tensor input);

    Tensor backward(Tensor gradInput, Object backInput);

    List<List<Tensor>> getParameters();
}
