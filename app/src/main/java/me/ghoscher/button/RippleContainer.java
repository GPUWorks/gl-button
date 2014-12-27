package me.ghoscher.button;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by Hisham on 25/12/2014.
 */
public class RippleContainer extends FrameLayout {
    private Ripple ripple;

    public RippleContainer(Context context) {
        super(context);
        init();
    }

    public RippleContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RippleContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RippleContainer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        ripple = new Ripple(getContext());

        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ripple.setLayoutParams(params);

        addView(ripple, 0);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        ripple.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

}
