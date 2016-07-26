package com.bartoszlipinski.flippablestackview.sample.activity;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.bartoszlipinski.flippablestackview.StackPageTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Dell on 7/26/2016.
 */
public class StackedFormsLayout extends FrameLayout {

    private static final float DEFAULT_CURRENT_PAGE_SCALE = 0.9f;
    private static final float DEFAULT_TOP_STACKED_SCALE = 0.8f;
    private static final float DEFAULT_OVERLAP_FACTOR = 1.0f;


    private List<Fragment> fragments;
    private List<View> formViews;
    private int formsCount;
    private FragmentManager fragmentManager;
    private List<Integer> formLayoutIds;
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    private Transformer transformer;

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

    private void init()
    {
        fragments=new ArrayList<>();
        formLayoutIds=new ArrayList<>();
        formLayoutIds.add(generateViewId());
        formViews=new ArrayList<>();
        transformer = new Transformer(4,DEFAULT_CURRENT_PAGE_SCALE, DEFAULT_TOP_STACKED_SCALE, DEFAULT_OVERLAP_FACTOR, Transformer.Gravity.CENTER);
    }

    public void setFragmentManager(FragmentManager fragmentManager)
    {
        this.fragmentManager=fragmentManager;
    }


   public void addForm(Fragment form)
   {
       fragments.add(form);
   }


    public void layoutForms()
    {
        removeAllViews();
        for(int position=0;position<=formsCount;position++)
        {
            if(position==0)
            {
                layoutInitialForm(position);
                return;
            }


        }

    }

    private void layoutInitialForm(int position)
    {
        LayoutParams lp = new LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT );
        lp.gravity = Gravity.CENTER;

        LinearLayout layout = new LinearLayout(getContext());
        layout.setLayoutParams(lp);
        layout.setId(formLayoutIds.get(0));
        this.addView(layout);

        fragmentManager.beginTransaction().add(layout.getId(),fragments.get(position)).commit();

    }

    public static int generateViewId() {
        for (;;) {
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
