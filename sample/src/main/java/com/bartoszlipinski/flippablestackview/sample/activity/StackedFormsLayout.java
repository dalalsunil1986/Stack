package com.bartoszlipinski.flippablestackview.sample.activity;

import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A layout that provides a base for stacked forms.
 */
public class StackedFormsLayout extends FrameLayout {

    private static final float DEFAULT_OVERLAP_FACTOR = 40.0f;
    private static final float SWIPE_RIGHT_FACTOR = 1.20f;
    private static final float SWIPE_LEFT_FACTOR = 0.16f;
    private static final float MAXIMUM_SWIPE_FACTOR = 8.0f;

    private List<Fragment> fragments;
    private List<View> formViews;
    private int formsCount;
    private FragmentManager fragmentManager;
    private List<Integer> formLayoutIds;
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    private Transformer transformer;
    private float x1, x2;
    private final int MIN_DISTANCE = 100;
    private float initialTouch;

    public StackedFormsLayout(Context context) {
        super(context);
        init();
    }

    public StackedFormsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StackedFormsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        fragments = new ArrayList<>();
        formLayoutIds = new ArrayList<>();
        formViews = new ArrayList<>();
        transformer = new Transformer(4, DEFAULT_OVERLAP_FACTOR);
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }


    public void addForm(Fragment form) {
        formsCount = fragments.size();

        fragments.add(form);
        formLayoutIds.add(generateViewId());

    }

    public void addAllForms(List<Fragment> forms) {
        for (Fragment form : forms) {
            fragments.add(form);
            formLayoutIds.add(generateViewId());
            addFormToLayout(fragments.indexOf(form));
        }

        formsCount = fragments.size();

    }


    public void layoutForms() {
        for (int position = 0; position <= formsCount; position++) {
            if (position == 0 && formsCount == 1) {
                layoutInitialForm();
                return;
            }

            addFormToLayout(position);

        }
    }

    /***
     * Touch event that's intercepted to provide form previews based on left and right swipes.
     *
     * @param event
     * @return
     */

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        /***
         * Basically the number that will multiply the default overlap factor to layout each form
         */
        float swipeFactor;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                x1 = event.getX();

                /***
                 * Tracking the initial touch on the screen so that it can be used to calculate the change that
                 * took place on ACTION_DOWN and ACTION_UP
                 */
                if (initialTouch == 0)
                    initialTouch = x1;

                return true;
            case MotionEvent.ACTION_UP:

                x2 = event.getX();
                float deltaX = x2 - x1;
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    /***
                     * Left to Right swipe action
                     */
                    if (x2 > x1) {


                        float offset = (event.getX() - initialTouch) / 100;
                        swipeFactor = offset * SWIPE_RIGHT_FACTOR;
                    }

                    /**
                     * Right to left swipe action
                     */
                    else {
                        float offset = -(event.getX() - initialTouch) / 100;
                        swipeFactor = offset * SWIPE_LEFT_FACTOR;
                    }

                    swipeFactor = filterSwipeFactor(swipeFactor);

                    if (transformer != null && swipeFactor != 0) {

                        transformer.setOverlapFactor((transformer.getDefaultOverlapFactor() * swipeFactor));
                        updateFormPositions();

                    }
                }
                initialTouch = 0;
                break;
        }

        return super.onTouchEvent(event);
    }


    /***
     * Add the initial form to the layout that will be centered.
     * It's position will change when sub forms are opened and it assumes the
     * position that's supplied by the Stack Transformer.
     */
    private void layoutInitialForm() {
        int position = 0;
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;

        LinearLayout layout = new LinearLayout(getContext());
        layout.setLayoutParams(lp);
        layout.setId(formLayoutIds.get(position));
        this.addView(layout);

        fragmentManager.beginTransaction().add(layout.getId(), fragments.get(position)).commit();
    }

    /**
     * Called to update all forms based on changes in either the current position of a page
     * or the overly factor that affects the shift that each will have on the x axis.
     */
    private void updateFormPositions() {
        for (View layout : formViews)
            transformer.transformPage(layout, formViews.indexOf(layout));
    }

    /**
     * Adds a form to the current layout using the position that it's current located in
     * the list that stores all fragments.
     *
     * @param position
     */
    private void addFormToLayout(final int position) {
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);

        final LinearLayout layout = new LinearLayout(getContext());
        layout.setLayoutParams(lp);
        layout.setId(formLayoutIds.get(position));
        this.addView(layout);
        fragmentManager.beginTransaction().add(layout.getId(), fragments.get(position)).commit();

        formViews.add(layout);
        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                removeOnGlobalLayoutListener(layout, this);
                transformer.transformPage(layout, position);
            }
        });
    }

    /**
     * Global Layout Listener that targets newer version and also has backward compat.
     *
     * @param v
     * @param listener
     */
    public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

    /***
     * Generates a unique view id that can be used for views.
     *
     * @return
     */
    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    /**
     * Ensures that the swipe factor value is a finite number.
     * And also checks if its below the maximum limit whereafter it's set to it.
     *
     * @param swipeFactor
     * @return
     */
    private float filterSwipeFactor(float swipeFactor) {
        if (swipeFactor != Float.NEGATIVE_INFINITY && swipeFactor != Float.POSITIVE_INFINITY && swipeFactor != Float.POSITIVE_INFINITY) {

            if (swipeFactor > MAXIMUM_SWIPE_FACTOR)
                swipeFactor = MAXIMUM_SWIPE_FACTOR;

            return (float) Math.ceil(swipeFactor);
        } else return 0;
    }
}
