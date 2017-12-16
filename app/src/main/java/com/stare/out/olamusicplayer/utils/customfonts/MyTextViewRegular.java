package com.stare.out.olamusicplayer.utils.customfonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class MyTextViewRegular extends android.support.v7.widget.AppCompatTextView {

    public MyTextViewRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyTextViewRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextViewRegular(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "Fonts/OpenSans-Regular.ttf");
            setTypeface(tf);
        }
    }

}
