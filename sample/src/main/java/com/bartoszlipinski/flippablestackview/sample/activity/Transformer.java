package com.bartoszlipinski.flippablestackview.sample.activity;

/**
 * Created by Dell on 7/26/2016.
 */

import android.view.View;

public class Transformer {


    private int numberOfStacked;
    private float overlapFactor;
    private float defaultOverlapFactor;


    public Transformer(int numberOfStacked, float overlapFactor) {

        this.numberOfStacked = numberOfStacked;
        this.overlapFactor = overlapFactor;
        this.defaultOverlapFactor = overlapFactor;
    }

    public Transformer() {
    }

    public void transformPage(View view, float position) {

        int dimen;

        dimen = view.getWidth();

        float shiftTranslation = position * overlapFactor;

        view.animate().translationX(shiftTranslation);

    }

    public int getNumberOfStacked() {
        return numberOfStacked;
    }

    public void setNumberOfStacked(int numberOfStacked) {
        this.numberOfStacked = numberOfStacked;
    }

    public float getOverlapFactor() {
        return overlapFactor;
    }

    public void setOverlapFactor(float overlapFactor) {
        this.overlapFactor = overlapFactor;
    }

    public float getDefaultOverlapFactor() {
        return defaultOverlapFactor;
    }

    public void setDefaultOverlapFactor(float defaultOverlapFactor) {
        this.defaultOverlapFactor = defaultOverlapFactor;
    }
}
