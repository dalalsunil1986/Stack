package com.bartoszlipinski.flippablestackview.sample.activity;

import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.util.Log;
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
 * Created by Dell on 7/26/2016.
 */
public class StackedFormsLayout extends FrameLayout {

    private static final float DEFAULT_OVERLAP_FACTOR = 40.0f;


    private List<Fragment> fragments;
    private List<View> formViews;
    private int formsCount;
    private FragmentManager fragmentManager;
    private List<Integer> formLayoutIds;
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    private Transformer transformer;
    float x1,x2;
    final int MIN_DISTANCE = 100;
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
            addCurrentForm(fragments.indexOf(form));
        }

        formsCount = fragments.size();

    }


    public void layoutForms() {
        for (int position = 0; position <= formsCount; position++) {
            if (position == 0 && formsCount == 1) {
                layoutInitialForm(position);
                return;
            }

            addCurrentForm(position);

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float swipeFactor;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                if(initialTouch==0)
                    initialTouch=x1;

                return true;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();

                Log.v("Swipe", "left to right detected "+x2);

                float deltaX = x2 - x1;
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    // Left to Right swipe action
                    if (x2 > x1) {
                        Log.v("Swipe", "left to right detected");

                        float swipeRightFactor = (event.getX()-initialTouch)/100;
                        swipeFactor = swipeRightFactor;
                    }

                    // Right to left swipe action
                    else {
                        Log.v("Swipe", "right to left detected");
                        float swipeLeftFactor = -(event.getX()-initialTouch)/100;
                        swipeFactor = swipeLeftFactor/3;
                    }

                    if (transformer != null && swipeFactor != 0) {
                        if (swipeFactor != Float.NEGATIVE_INFINITY && swipeFactor != Float.POSITIVE_INFINITY && swipeFactor != Float.POSITIVE_INFINITY) {
                            // integral type
                            Log.v("Swipe", "factor " + swipeFactor);
                            transformer.setOverlapFactor((float) (transformer.getDefaultOverlapFactor() * Math.ceil(swipeFactor)));
                            updateFormPositions();
                        }
                    }

                } else {
                    // consider as something else - a screen tap for example
                }

                initialTouch=0;
                break;
        }

        return super.onTouchEvent(event);
    }

    private void layoutInitialForm(int position) {
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;

        LinearLayout layout = new LinearLayout(getContext());
        layout.setLayoutParams(lp);
        layout.setId(formLayoutIds.get(position));
        this.addView(layout);

        fragmentManager.beginTransaction().add(layout.getId(), fragments.get(position)).commit();
    }

    private void updateFormPositions() {
        for (View layout : formViews)
            transformer.transformPage(layout, formViews.indexOf(layout));

    }

    private void addCurrentForm(final int position) {
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

    public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

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
}
