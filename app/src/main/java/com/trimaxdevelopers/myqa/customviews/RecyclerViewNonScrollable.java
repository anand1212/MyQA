package com.trimaxdevelopers.myqa.customviews;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class RecyclerViewNonScrollable extends RecyclerView {

    public RecyclerViewNonScrollable(Context context) {
        super(context);
    }

    public RecyclerViewNonScrollable(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewNonScrollable(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        //Ignore scroll events.
        if (ev.getAction() == MotionEvent.ACTION_MOVE)
            return true;

        //Dispatch event for non-scroll actions, namely clicks!
        return super.dispatchTouchEvent(ev);
    }
}