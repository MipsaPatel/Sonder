package com.story.sonder.model.layer;

import com.story.sonder.model.Tensor;

import java.util.ArrayList;
import java.util.List;

abstract class Layer implements ILayer {
    @Override
    public List<List<Tensor>> getParameters() {
        return new ArrayList<>();
    }
}
