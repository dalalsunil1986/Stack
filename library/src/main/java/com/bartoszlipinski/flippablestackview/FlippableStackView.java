/**
 * Copyright 2015 Bartosz Lipinski
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bartoszlipinski.flippablestackview;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;


public class FlippableStackView extends ViewPager {
    private static final float DEFAULT_CURRENT_PAGE_SCALE = 0.9f;
    private static final float DEFAULT_TOP_STACKED_SCALE = 0.8f;
    private static final float DEFAULT_OVERLAP_FACTOR = 1.0f;

    private float initialTouch = 0;
    private StackPageTransformer transformer;


    ViewPagerRefreshListener viewPagerRefreshListener;

    public FlippableStackView(Context context) {
        super(context);
    }

    public FlippableStackView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Used to create a simple <code>FlippableStackView</code> (only the number of stacked views
     * is being set by the user, other parameters are set to default values).
     *
     * @param numberOfStacked Number of pages stacked under the current page.
     */
    public void initStack(int numberOfStacked) {
        initStack(numberOfStacked, DEFAULT_CURRENT_PAGE_SCALE, DEFAULT_TOP_STACKED_SCALE, DEFAULT_OVERLAP_FACTOR, StackPageTransformer.Gravity.CENTER);
    }


    /**
     * Used to create <code>FlippableStackView</code> with all customizable parameters defined.
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
    public void initStack(int numberOfStacked, float currentPageScale, float topStackedScale, float overlapFactor, StackPageTransformer.Gravity gravity) {
        setPageTransformer(false,transformer);
        setOffscreenPageLimit(numberOfStacked + 1);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (initialTouch == 0) {
                initialTouch = ev.getX();

            }

        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            initialTouch = 0;
        }


        float touchOffset = initialTouch + 200;
        if (ev.getX() > touchOffset) {
            MotionEvent event = MotionEvent.obtain(ev.getDownTime(), ev.getEventTime(), ev.getAction(), touchOffset, ev.getY(), ev.getMetaState());

            return super.onTouchEvent(event);
        }

        if(transformer!=null)
            transformer.setmOverlapFactor(12);

        if(viewPagerRefreshListener!=null)
            viewPagerRefreshListener.refresh();


        return super.onTouchEvent(ev);

    }

    public void setViewPagerRefreshListener(ViewPagerRefreshListener viewPagerRefreshListener) {
        this.viewPagerRefreshListener = viewPagerRefreshListener;
    }



    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);

        if(adapter!=null)
        setCurrentItem(adapter.getCount() - 1);
    }


}
