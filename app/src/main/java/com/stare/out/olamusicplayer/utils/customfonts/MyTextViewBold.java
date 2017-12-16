package com.stare.out.olamusicplayer.utils.customfonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class MyTextViewBold extends android.support.v7.widget.AppCompatTextView {
    public MyTextViewBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyTextViewBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextViewBold(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Fonts/OpenSans-Bold.ttf"));
        }
    }
}
