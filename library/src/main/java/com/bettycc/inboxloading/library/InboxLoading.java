package com.bettycc.inboxloading.library;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by ccheng on 10/29/14.
 */
public class InboxLoading extends View {

    public static final int SWEEP_RANGE = 270;
    public static final int SWEEP_DURATION = 600;
    public static final int ROTATE_DURATION = 2000;
    public int CIRCLE_STROKE_WIDTH;
    private float mCircleSize;
    private float mViewSize;
    private float mFastDegree;
    private float mSweepLength;
    private float mRotateDegree;
    private float mRotateDegreeOffset;
    private float mRotateDegreeOffset2;
    private ValueAnimator mSweepDispearAnimator;
    private ValueAnimator mSweepAppearAnimator;

    private int[] colorRessources = new int[]{
            R.color.blue,
            R.color.red,
            R.color.yellow,
    };
    private int mColorIndex = 0;
    private boolean mStop;
    private ValueAnimator mRotateAnimator;
    private AnimatorSet mScaleAnimatorSet;

    /**
     * How far it can be pull and the rotate animates.
     */
    private float mPullRange;

    /**
     * When the sweep range reach it, then sweep stop increasing.
     */
    private float mPullSweepRange;

    /**
     * When rotate range reach it, stop increasing start rotate degree.
     */
    private float mPullRotateDegreeRange;
    private float mAlpha;

    public InboxLoading(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mCircleSize = dipToPx(80);
        mViewSize = dipToPx(90);

        mPullRange = dipToPx(100);
        mPullSweepRange = 300;
        mPullRotateDegreeRange = dipToPx(20);

        CIRCLE_STROKE_WIDTH = (int)dipToPx(5);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((int) mViewSize, (int) mViewSize);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initAnimator();
    }

    private void cycleColorIndex() {
        mColorIndex = (mColorIndex + 1)%colorRessources.length;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        setLayerType(LAYER_TYPE_SOFTWARE, null);
        Paint paintBorder = new Paint();
        paintBorder.setAntiAlias(true);

        paintBorder.setColor(Color.WHITE);
        paintBorder.setStyle(Paint.Style.FILL);
        paintBorder.setAntiAlias(true);
        canvas.drawCircle(mViewSize/2, mViewSize/2, mViewSize/2, paintBorder);

        Paint paint = new Paint();
        paint.setColor(getResources().getColor(colorRessources[mColorIndex]));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(CIRCLE_STROKE_WIDTH);
        paint.setAntiAlias(true);
        paint.setAlpha((int) (255*mAlpha));
        RectF rect = new RectF((mViewSize - mCircleSize) / 1,
                (mViewSize - mCircleSize) / 1,
                mCircleSize,
                mCircleSize);

        int startAngle = (int) (mRotateDegree + mRotateDegreeOffset + mRotateDegreeOffset2);
        canvas.drawArc(rect, startAngle, mSweepLength, false, paint);

    }

    private float dipToPx(float dip) {
        return dip * getContext().getResources().getDisplayMetrics().density;
    }

    public void startLoading() {
        mStop = false;
        mRotateAnimator.start();
        mSweepAppearAnimator.start();
    }

    private void initAnimator() {
        mRotateAnimator = ValueAnimator.ofFloat(0f, 360f);
        RotateAnimatorListener rotateAnimatorListener = new RotateAnimatorListener();
        mRotateAnimator.addUpdateListener(rotateAnimatorListener);
        mRotateAnimator.addListener(rotateAnimatorListener);
        mRotateAnimator.setDuration(ROTATE_DURATION);
        mRotateAnimator.setRepeatMode(ValueAnimator.RESTART);
        mRotateAnimator.setRepeatCount(ValueAnimator.INFINITE);

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

        mScaleAnimatorSet = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(this, "scaleX", 1f, 0f);
        ValueAnimator.AnimatorUpdateListener scaleListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (mStop) {
                    ((View) getParent()).invalidate();
                }
            }
        };
        scaleX.addUpdateListener(scaleListener);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(this, "scaleY", 1f, 0f);
        scaleY.addUpdateListener(scaleListener);
        mScaleAnimatorSet.play(scaleX).with(scaleY);
        mScaleAnimatorSet.setInterpolator(new AccelerateInterpolator());
    }

    public void stop() {
        mStop = true;
    }

    public void start() {
        startLoading();
    }

    public void hide() {
        stop();

        mScaleAnimatorSet.start();
    }

    public void onScrollTo(int y) {
        float pullPercent = y > mPullRange ? 1 : y / mPullRange;
        if (pullPercent < 1) {
            mRotateDegreeOffset = pullPercent * mPullRotateDegreeRange;
            mSweepLength = pullPercent * mPullSweepRange;
        } else {
            float v = y - mPullRange;
            mRotateDegreeOffset2 = v;
        }

        mAlpha = pullPercent;
        invalidate();
    }

    private class RotateAnimatorListener implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {

        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            if (!mStop) {
                float v = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                mRotateDegree = v;
                invalidate();
            }
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
            if (!mStop) {
                float v = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                mSweepLength = v;
                invalidate();
            }
        }

        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {
            if (!mStop) {
                mSweepDispearAnimator.start();
                mRotateDegreeOffset += SWEEP_RANGE;
                invalidate();
            }
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
            if (!mStop) {
                float v = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                mSweepLength = v;
                invalidate();
            }
        }

        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {
            if (!mStop) {
                cycleColorIndex();
                mSweepAppearAnimator.start();
                invalidate();
            }
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    }
}
