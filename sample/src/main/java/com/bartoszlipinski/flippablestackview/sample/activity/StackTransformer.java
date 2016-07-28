package com.bartoszlipinski.flippablestackview.sample.activity;

/**
 * Created by Dell on 7/26/2016.
 */

import android.view.View;

public class StackTransformer {

    private int numberOfStacked;
    private float overlapFactor;
    private float defaultOverlapFactor;
    private int currentFormIndex;


    public StackTransformer(int numberOfStacked, float overlapFactor) {

        this.numberOfStacked = numberOfStacked;
        this.overlapFactor = overlapFactor;
        this.defaultOverlapFactor = overlapFactor;
        currentFormIndex = 0;
    }

    public StackTransformer() {
    }

    public void transformPage(View view, float position) {

        int dimen;

        dimen = view.getWidth();

        float shiftTranslation = position * overlapFactor;

        if (currentFormIndex != 0 && currentFormIndex != numberOfStacked - 1)
            if (position > currentFormIndex)
                shiftTranslation = shiftTranslation + (dimen * .8f);


        view.animate().translationX(shiftTranslation);

        if(position==numberOfStacked-1)
        {
            currentFormIndex=0;
        }


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

    public int getCurrentFormIndex() {
        return currentFormIndex;
    }

    public void setCurrentFormIndex(int currentFormIndex) {
        this.currentFormIndex = currentFormIndex;
    }
}
