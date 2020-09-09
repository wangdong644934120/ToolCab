package com.stit.toolcab.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import cn.cloudwalk.component.liveness.jni.CWLivenessDetector;

public class CWGLRect extends View {
    private Paint mPaint;
    ReentrantLock mLock = new ReentrantLock();
    final int MAX_RECT_SIZE = 20;//上限个数
    //ArrayList<Rect> mRects = new ArrayList<>(MAX_RECT_SIZE);
    private static Map<Integer, Integer> mColorMap = new HashMap<Integer, Integer>() {
        {
            put(CWLivenessDetector.FACE_LIVENESS_DEFAULT, Color.YELLOW);
            put(CWLivenessDetector.FACE_LIVENESS_IS_LIVE, Color.GREEN);
            put(CWLivenessDetector.FACE_LIVENESS_IS_UNLIVE_ERR, Color.RED);
            put(CWLivenessDetector.FACE_LIVENESS_SKIN_FAILED_ERR, Color.YELLOW);
            put(CWLivenessDetector.FACE_LIVENESS_NO_PAIR_FACE_ERR, Color.YELLOW);
            put(CWLivenessDetector.FACE_LIVENESS_NIS_NO_FACE_ERR, Color.YELLOW);
            put(CWLivenessDetector.FACE_LIVENESS_VIS_NO_FACE_ERR, Color.YELLOW);
        }
    };

    public static Map<Integer, Integer> getColorMap() {
        return mColorMap;
    }

    public static void setColorMap(Map<Integer, Integer> colorMap) {
        CWGLRect.mColorMap = colorMap;
    }

    public static void setColorMapElement(Integer errorCode, RECT_COLOR color) {
        Integer colorCode = 0;
        switch (color) {
            case RED:
                colorCode = Color.RED;
                break;
            case GREEN:
                colorCode = Color.GREEN;
                break;
            case YELLOW:
                colorCode = Color.YELLOW;
                break;
            default:
                return;
        }
        mColorMap.put(errorCode, colorCode);
    }

    private int mWidth;
    private int mHeight;
    private int rectLeft;
    private int rectRight;
    private int rectTop;
    private int rectBottom;

    public enum RECT_COLOR {
        RED,
        GREEN,
        YELLOW
    }

    public CWGLRect(Context context) {
        super(context);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(3f);
        mPaint.setAlpha(180);
    }

    public CWGLRect(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(3f);
        mPaint.setAlpha(180);
    }

    public void setRect(Rect rect) {
        try {
            mLock.lock();
            if (rect == null) {
                this.rectLeft = 0;
                this.rectTop = 0;
                this.rectRight = 0;
                this.rectBottom = 0;
            } else {
                this.rectLeft = rect.left;
                this.rectTop = rect.top;
                this.rectRight = rect.right;
                this.rectBottom = rect.bottom;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mLock.unlock();
        }
        postInvalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        this.mWidth = w;
        this.mHeight = h;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (mWidth <= 0 || mHeight <= 0) {
            return;
        }
        try {
            mLock.lock();
            canvas.drawRect(new Rect(rectLeft, rectTop, rectRight, rectBottom), mPaint);
            invalidate();
        } finally {
            mLock.unlock();
        }
    }
}
