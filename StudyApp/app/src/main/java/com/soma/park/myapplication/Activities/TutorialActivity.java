package com.soma.park.myapplication.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.soma.park.myapplication.R;

public class TutorialActivity extends Activity {

    ViewPager viewPager;
    MyPagerAdapter myPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiy_tutorial);
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        myPagerAdapter = new MyPagerAdapter();
        viewPager.setAdapter(myPagerAdapter);
    }

    private class MyPagerAdapter extends PagerAdapter{

        int NumberOfPages = 6;

        int[] res = {
                R.drawable.tutorial_0,
                R.drawable.tutorial_1,
                R.drawable.tutorial_2,
                R.drawable.tutorial_3,
                R.drawable.tutorial_4,
                R.drawable.tutorial_5 };

        @Override
        public int getCount() {
            return NumberOfPages;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            ImageView imageView = new ImageView(TutorialActivity.this);
            imageView.setImageResource(res[position]);
            LayoutParams imageParams = new LayoutParams(
                    LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(imageParams);
            LinearLayout layout = new LinearLayout(TutorialActivity.this);
            layout.addView(imageView);

            if (position == 5) {
                Handler hd = new Handler();
                hd.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(TutorialActivity.this, LockScreenActivity.class);
                        startActivity(intent);
                    }
                }, 3000);
            }

            container.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout)object);
        }
    }
}