package com.stare.out.olamusicplayer.utils.customfonts;

/**
 * Created by rishabh on 17/10/17.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class MyTextViewSemibold extends android.support.v7.widget.AppCompatTextView {

    public MyTextViewSemibold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyTextViewSemibold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextViewSemibold(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "Fonts/OpenSans-Semibold.ttf");
            setTypeface(tf);
        }
    }

}

