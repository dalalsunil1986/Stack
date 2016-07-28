package com.bartoszlipinski.flippablestackview.sample.activity;

/**
 * Created by Dell on 7/26/2016.
 */

import android.view.View;

/***
 * Handles all animation and translation related to the stacking of forms.
 */
public class StackTransformer {

    private static final float DEFAULT_SWIPE_OVERLAY_FACTOR = 40;
    private int numberOfStacked;
    private float overlapFactor;
    private float defaultOverlapFactor;
    private int currentFormIndex;


    /***
     * Used to set the current number of forms and the overlay factor.
     * @param numberOfStacked
     * @param overlapFactor
     */
    public StackTransformer(int numberOfStacked, float overlapFactor) {

        this.numberOfStacked = numberOfStacked;
        this.overlapFactor = overlapFactor;
        defaultOverlapFactor = DEFAULT_SWIPE_OVERLAY_FACTOR;
        currentFormIndex = 0;
    }


    public void transformPage(View view, float position) {

        int dimen;

        dimen = view.getWidth();

        /***
         * Calculates how far along the x axis a particular form should shift based on
         * the position and the overlay factor that's being used.
         */
        float shiftTranslation = position * overlapFactor;

        /***
         * Increases the shift translation if there was a click event that set a form as the current one
         * in the stack and it has elements that are currently ahead of it.
         */
        if (currentFormIndex != 0 && currentFormIndex != numberOfStacked - 1)
            if (position > currentFormIndex)
                shiftTranslation = shiftTranslation + (dimen * .8f);


        view.animate().translationX(shiftTranslation);

        /***
         * Sets the current form index back to zero once the entire stack of forms has been animated.
         */
        if (position == numberOfStacked - 1) {
            currentFormIndex = 0;
        }

    }

    public void setNumberOfStacked(int numberOfStacked) {
        this.numberOfStacked = numberOfStacked;
    }


    public void setOverlapFactor(float overlapFactor) {
        this.overlapFactor = overlapFactor;
    }

    public float getDefaultOverlapFactor() {
        return defaultOverlapFactor;
    }


    public void setCurrentFormIndex(int currentFormIndex) {
        this.currentFormIndex = currentFormIndex;
    }
}
