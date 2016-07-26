package com.bartoszlipinski.flippablestackview.sample.activity;

/**
 * Created by Dell on 7/26/2016.
 */
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.bartoszlipinski.flippablestackview.utilities.ValueInterpolator;

/**
 * Created by Bartosz Lipinski
 * 28.01.15
 */
public class Transformer {


    public enum Gravity {
        TOP, CENTER, BOTTOM
    }

    private int mNumberOfStacked;

    private float mZeroPositionScale;
    private float mStackedScaleFactor;
    private float mOverlapFactor;
    private float mOverlap;
    private float mAboveStackSpace;
    private float mBelowStackSpace;

    private boolean mInitialValuesCalculated = false;

    private Gravity mGravity;

    private Interpolator mScaleInterpolator;

    private ValueInterpolator mValueInterpolator;

    /**
     * Used to construct the basic method for visual transformation in <code>FlippableStackView</code>.
     *
     * @param numberOfStacked  Number of pages stacked under the current page.
     * @param currentPageScale Scale of the current page. Must be a value from (0, 1].
     * @param topStackedScale  Scale of the top stacked page. Must be a value from
     *                         (0, <code>currentPageScale</code>].
     * @param overlapFactor    Defines the usage of available space for the overlapping by stacked
     *                         pages. Must be a value from [0, 1]. Value 1 means that the whole
     *                         available space (obtained due to the scaling with
     *                         <code>currentPageScale</code>) will be used for the purpose of displaying
     *                         stacked views. Value 0 means that no space will be used for this purpose
     *                         (in other words - no stacked views will be visible).
     * @param gravity          Specifies the alignment of the stack (vertically) withing <code>View</code>
     *                         bounds.
     */
    public Transformer(int numberOfStacked, float currentPageScale, float topStackedScale, float overlapFactor, Gravity gravity) {
        validateValues(currentPageScale, topStackedScale, overlapFactor);

        mNumberOfStacked = numberOfStacked;
        mZeroPositionScale = currentPageScale;
        mStackedScaleFactor = (currentPageScale - topStackedScale) / mNumberOfStacked;
        mOverlapFactor = overlapFactor;
        mGravity = gravity;

        mScaleInterpolator = new DecelerateInterpolator(1.3f);
        mValueInterpolator = new ValueInterpolator(0, 1, 0, mZeroPositionScale);
    }

    public void transformPage(View view, float position) {

        Log.v("Transformer","Page Tranformer called for page "+position);

        int dimen;

        dimen = view.getWidth();


        if (!mInitialValuesCalculated) {
            mInitialValuesCalculated = true;
            calculateInitialValues(dimen);
        }


        view.setPivotX(dimen / 2f);
        view.setPivotY(view.getHeight() / 2f);


        if (position < -mNumberOfStacked - 1) {
            view.setAlpha(0f);
        } else if (position == 0) {
            float scale = mZeroPositionScale + (position * mStackedScaleFactor);
            float baseTranslation = (-position * dimen);
            float shiftTranslation = calculateShiftForScale(position, scale, dimen);
            view.setScaleX(scale);


            view.setTranslationX(baseTranslation + shiftTranslation);


        } else if (position >= 1) {
            float baseTranslation = position * dimen;
            float scale = mZeroPositionScale - mValueInterpolator.map(mScaleInterpolator.getInterpolation(position));
            scale = (scale < 0) ? 0f : scale;
            float shiftTranslation = (1.0f - position) * mOverlap;


            view.setPivotX(dimen);
            view.setScaleX(scale);
            view.setTranslationX(-baseTranslation - mBelowStackSpace - shiftTranslation);


        }
    }

    private void calculateInitialValues(int dimen) {
        float scaledDimen = mZeroPositionScale * dimen;

        float overlapBase = (dimen - scaledDimen) / (mNumberOfStacked);
        mOverlap = overlapBase * mOverlapFactor;

        float availableSpaceUnit = 0.5f * dimen * (1 - mOverlapFactor) * (1 - mZeroPositionScale);
        switch (mGravity) {

            case TOP:
                mAboveStackSpace = 0;
                mBelowStackSpace = 2 * availableSpaceUnit;
                break;
            case CENTER:
                mAboveStackSpace = availableSpaceUnit;
                mBelowStackSpace = availableSpaceUnit;
                break;
            case BOTTOM:
                mAboveStackSpace = 2 * availableSpaceUnit;
                mBelowStackSpace = 0;
                break;
        }
    }

    private float calculateShiftForScale(float position, float scale, int dimen) {
        //difference between centers
        return mAboveStackSpace + ((mNumberOfStacked + position) * mOverlap) + (dimen * 0.5f * (scale - 1));
    }

    private void validateValues(float currentPageScale, float topStackedScale, float overlapFactor) {
        if (currentPageScale <= 0 || currentPageScale > 1) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": Current page scale not correctly defined. " +
                    "Be sure to set it to value from (0, 1].");
        }

        if (topStackedScale <= 0 || topStackedScale > currentPageScale) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": Top stacked page scale not correctly defined. " +
                    "Be sure to set it to value from (0, currentPageScale].");
        }

    }

    public void setmOverlapFactor(float mOverlapFactor) {
        this.mOverlapFactor = mOverlapFactor;
    }
}
