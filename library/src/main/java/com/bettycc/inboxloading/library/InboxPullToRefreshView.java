package com.bettycc.inboxloading.library;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by ccheng on 11/4/14.
 */
public class InboxPullToRefreshView extends LinearLayout {

    private InboxLoading mRefreshView;
    private float mPreY;
    private View mViewById;

    public InboxPullToRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InboxPullToRefreshView(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        inflate(getContext(), R.layout.inbox_ptr, this);

        mRefreshView = (InboxLoading)findViewById(R.id.loading);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPreY = event.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                float y = event.getY();
                System.out.println("InboxPullToRefreshView.onTouchEvent" + y);
                mRefreshView.onScrollTo((int) y);
                break;
        }

        return super.onTouchEvent(event);
    }
}
