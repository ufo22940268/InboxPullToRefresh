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
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by ccheng on 10/29/14.
 */
public class InboxLoading extends View {

    public static final int SWEEP_RANGE = 270;
    public static final int SWEEP_DURATION = 600;
    public static final int ROTATE_DURATION = 2000;
    private float mCircleSize;
    private float mViewSize;
    private float mFastDegree;
    private float mSweep;
    private float mRotateDegree;
    private float mRotateDegreeOffset;
    private ValueAnimator mSweepDispearAnimator;
    private ValueAnimator mSweepAppearAnimator;

    private int[] colorRessources = new int[]{

    };

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

        int startAngle = (int) (mRotateDegree + mRotateDegreeOffset);
        System.out.println("startAngle = " + startAngle + "\tsweep = " + mSweep);
        canvas.drawArc(rect, startAngle, mSweep, false, paint);
    }

    private float dipToPx(float dip) {
        return dip*getContext().getResources().getDisplayMetrics().density;
    }

    public void startLoading() {
        ValueAnimator rotateAnimator = ValueAnimator.ofFloat(0f, 360f);
        RotateAnimatorListener rotateAnimatorListener = new RotateAnimatorListener();
        rotateAnimator.addUpdateListener(rotateAnimatorListener);
        rotateAnimator.addListener(rotateAnimatorListener);
        rotateAnimator.setDuration(ROTATE_DURATION);
        rotateAnimator.setRepeatMode(ValueAnimator.RESTART);
        rotateAnimator.setRepeatCount(ValueAnimator.INFINITE);
        rotateAnimator.start();

        SweepAppearListener sweepAppearListener = new SweepAppearListener();
        mSweepAppearAnimator = ValueAnimator.ofFloat(0f, SWEEP_RANGE);
        mSweepAppearAnimator.addUpdateListener(sweepAppearListener);
        mSweepAppearAnimator.addListener(sweepAppearListener);
        mSweepAppearAnimator.setInterpolator(new DecelerateInterpolator());
        mSweepAppearAnimator.setDuration(SWEEP_DURATION);

        SweepDisppearListener sweepDisppearListener = new SweepDisppearListener();
        mSweepDispearAnimator = ValueAnimator.ofFloat(-SWEEP_RANGE, 0f);
        mSweepDispearAnimator.addUpdateListener(sweepDisppearListener);
        mSweepDispearAnimator.addListener(sweepDisppearListener);
        mSweepDispearAnimator.setInterpolator(new DecelerateInterpolator());
        mSweepDispearAnimator.setDuration(SWEEP_DURATION);

        mSweepAppearAnimator.start();
    }

    private class RotateAnimatorListener implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {

        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float v = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            mRotateDegree = v;
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
        }
    }

    private class SweepAppearListener implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {

        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float v = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            mSweep = v;
            invalidate();
        }

        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {
            mSweepDispearAnimator.start();
            mRotateDegreeOffset += SWEEP_RANGE;
            invalidate();
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {
        }
    }

    private class SweepDisppearListener implements Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float v = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            mSweep = v;
            invalidate();
        }

        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {
            mSweepAppearAnimator.start();
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    }
}
