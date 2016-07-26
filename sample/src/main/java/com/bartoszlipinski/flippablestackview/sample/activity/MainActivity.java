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

package com.bartoszlipinski.flippablestackview.sample.activity;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bartoszlipinski.flippablestackview.FlippableStackView;
import com.bartoszlipinski.flippablestackview.StackPageTransformer;
import com.bartoszlipinski.flippablestackview.ViewPagerRefreshListener;
import com.bartoszlipinski.flippablestackview.sample.R;
import com.bartoszlipinski.flippablestackview.sample.fragment.ColorFragment;
import com.bartoszlipinski.flippablestackview.utilities.ValueInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bartosz Lipinski
 * 12.12.14
 */
public class MainActivity extends ActionBarActivity {

    private static final int NUMBER_OF_FRAGMENTS = 4;

    private StackedFormsLayout mFlippableStack;

    private ColorFragmentAdapter mPageAdapter;

    private List<Fragment> mViewPagerFragments;

    private TextView touchEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        touchEvent = (TextView) findViewById(R.id.touch_event);


        createViewPagerFragments();
        mPageAdapter = new ColorFragmentAdapter(getSupportFragmentManager(), mViewPagerFragments);


        mFlippableStack = (StackedFormsLayout) findViewById(R.id.flippable_stack_view);
        mFlippableStack.setFragmentManager(getSupportFragmentManager());
        mFlippableStack.addForm(mViewPagerFragments.get(0));
        mFlippableStack.layoutForms();


    }

    private void createViewPagerFragments() {
        mViewPagerFragments = new ArrayList<>();

        ColorGenerator generator = ColorGenerator.MATERIAL;

        for (int i = 0; i < NUMBER_OF_FRAGMENTS; ++i) {
            mViewPagerFragments.add(ColorFragment.newInstance(generator.getRandomColor()));
        }

    }

    private class ColorFragmentAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        public ColorFragmentAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }
    }

}
