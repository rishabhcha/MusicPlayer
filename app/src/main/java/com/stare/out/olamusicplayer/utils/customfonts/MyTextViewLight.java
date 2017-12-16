package com.stare.out.olamusicplayer.utils.customfonts;

/**
 * Created by rishabh on 17/10/17.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class MyTextViewLight extends android.support.v7.widget.AppCompatTextView {

    public MyTextViewLight(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyTextViewLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextViewLight(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "Fonts/OpenSans-Light.ttf");
            setTypeface(tf);
        }
    }

}

