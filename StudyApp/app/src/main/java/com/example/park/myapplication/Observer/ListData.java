package com.example.park.myapplication.Observer;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

import java.text.Collator;
import java.util.Comparator;

/**
 * Created by prena on 15. 10. 10..
 */
public class ListData {
    public Drawable mIcon;

    // 제목
    public String mTitle;

    // 날짜
    public String mDate;
    public String mPackage;

    public int mColor;

    /**
     * 알파벳 이름으로 정렬
     */
    public static final Comparator<ListData> ALPHA_COMPARATOR = new Comparator<ListData>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(ListData mListDate_1, ListData mListDate_2) {
            return sCollator.compare(mListDate_1.mTitle, mListDate_2.mTitle);
        }
    };
}