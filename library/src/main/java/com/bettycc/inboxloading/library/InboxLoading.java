package com.bettycc.inboxloading.library;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by ccheng on 10/29/14.
 */
public class InboxLoading extends View {

    public static final int SWEEP_RANGE = 100;
    private float mCircleSize;
    private float mViewSize;
    private float mFastDegree;
    private float mSweep;
    private float mRotateBaseDegreee;
    private float mRotateDegree;

    public InboxLoading(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mCircleSize = dipToPx(80);
        mViewSize = dipToPx(100);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((int) mViewSize, (int)mViewSize);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setAntiAlias(true);
        RectF rect = new RectF((mViewSize - mCircleSize)/2,
                (mViewSize - mCircleSize)/2,
                mCircleSize,
                mCircleSize);

        canvas.drawArc(rect, mRotateDegree + mRotateBaseDegreee, mSweep, false, paint);
    }

    private float dipToPx(float dip) {
        return dip*getContext().getResources().getDisplayMetrics().density;
    }

    public void startLoading() {
        ValueAnimator rotateAnimator = ValueAnimator.ofFloat(0f, 360f);
        RotateAnimatorListener rotateAnimatorListener = new RotateAnimatorListener();
        rotateAnimator.addUpdateListener(rotateAnimatorListener);
        rotateAnimator.addListener(rotateAnimatorListener);
        rotateAnimator.setDuration(3000);
        rotateAnimator.setRepeatMode(ValueAnimator.RESTART);
        rotateAnimator.setRepeatCount(ValueAnimator.INFINITE);
        rotateAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        rotateAnimator.start();

        ValueAnimator sweepAnimator = ValueAnimator.ofFloat(0f, SWEEP_RANGE);
        SweepListener sweepListener = new SweepListener();
        sweepAnimator.addUpdateListener(sweepListener);
        sweepAnimator.setInterpolator(new DecelerateInterpolator());
        sweepAnimator.addListener(sweepListener);
        sweepAnimator.setDuration(1000);
        sweepAnimator.setRepeatMode(ValueAnimator.RESTART);
        sweepAnimator.setRepeatCount(ValueAnimator.INFINITE);
        sweepAnimator.start();
    }

    private class RotateAnimatorListener implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {

        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float v = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            mRotateDegree = v;
        }

        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {

        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {
        }
    }

    private class SweepListener implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {

        private boolean reversed = false;

        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float v = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            if (reversed) {
                v = -(SWEEP_RANGE - v);
            }
            mSweep = v;
            invalidate();
        }

        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {
            if (!reversed) {
                System.out.println("SweepListener.onAnimationRepeat");
                mRotateBaseDegreee += mSweep;
                mSweep = -SWEEP_RANGE;
            }
            reversed = reversed ? false : true;
        }
    }
}
