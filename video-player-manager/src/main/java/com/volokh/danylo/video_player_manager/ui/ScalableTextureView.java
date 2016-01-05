package com.volokh.danylo.video_player_manager.ui;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.TextureView;

import com.volokh.danylo.video_player_manager.Config;
import com.volokh.danylo.video_player_manager.utils.Logger;

public abstract class ScalableTextureView extends TextureView{

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private static final String TAG = ScalableTextureView.class.getSimpleName();

    private Integer mContentWidth;
    private Integer mContentHeight;

    private float mPivotPointX = 0f;
    private float mPivotPointY = 0f;

    private float mContentScaleX = 1f;
    private float mContentScaleY = 1f;

    private float mContentRotation = 0f;

    private float mContentScaleMultiplier = 1f;

    private int mContentX = 0;
    private int mContentY = 0;

    private final Matrix mTransformMatrix = new Matrix();

    private ScaleType mScaleType;

    public enum ScaleType {
        CENTER_CROP, TOP, BOTTOM, FILL
    }

    public ScalableTextureView(Context context) {
        super(context);
    }

    public ScalableTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScalableTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ScalableTextureView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setScaleType(ScaleType scaleType) {
        mScaleType = scaleType;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (SHOW_LOGS) Logger.v(TAG, "onMeasure, mContentoWidth " + mContentWidth + ", mContentHeight " + mContentHeight);

        if (mContentWidth != null && mContentHeight != null) {
            updateTextureViewSize();
        }
    }

    public void updateTextureViewSize() {
        if (SHOW_LOGS) Logger.d(TAG, ">> updateTextureViewSize");
        if (mContentWidth == null || mContentHeight == null) {
            throw new RuntimeException("null content size");
        }

        float viewWidth = getMeasuredWidth();
        float viewHeight = getMeasuredHeight();

        float contentWidth = mContentWidth;
        float contentHeight = mContentHeight;

        if (SHOW_LOGS) {
            Logger.v(TAG, "updateTextureViewSize, mContentWidth " + mContentWidth + ", mContentHeight " + mContentHeight + ", mScaleType " + mScaleType);
            Logger.v(TAG, "updateTextureViewSize, viewWidth " + viewWidth + ", viewHeight " + viewHeight);
        }

        float scaleX = 1.0f;
        float scaleY = 1.0f;

        switch (mScaleType) {
            case FILL:
                if (viewWidth > viewHeight) {   // device in landscape
                    scaleX = (viewHeight * contentWidth) / (viewWidth * contentHeight);
                } else {
                    scaleY = (viewWidth * contentHeight) / (viewHeight * contentWidth);
                }
                break;
            case BOTTOM:
            case CENTER_CROP:
            case TOP:
                if (contentWidth > viewWidth && contentHeight > viewHeight) {
                    scaleX = contentWidth / viewWidth;
                    scaleY = contentHeight / viewHeight;
                } else if (contentWidth < viewWidth && contentHeight < viewHeight) {
                    scaleY = viewWidth / contentWidth;
                    scaleX = viewHeight / contentHeight;
                } else if (viewWidth > contentWidth) {
                    scaleY = (viewWidth / contentWidth) / (viewHeight / contentHeight);
                } else if (viewHeight > contentHeight) {
                    scaleX = (viewHeight / contentHeight) / (viewWidth / contentWidth);
                }
                break;
        }

        if (SHOW_LOGS) {
            Logger.v(TAG, "updateTextureViewSize, scaleX " + scaleX + ", scaleY " + scaleY);
        }

        // Calculate pivot points, in our case crop from center
        float pivotPointX;
        float pivotPointY;

        switch (mScaleType) {
            case TOP:
                pivotPointX = 0;
                pivotPointY = 0;
                break;
            case BOTTOM:
                pivotPointX = viewWidth;
                pivotPointY = viewHeight;
                break;
            case CENTER_CROP:
                pivotPointX = viewWidth / 2;
                pivotPointY = viewHeight / 2;
                break;
            case FILL:
                pivotPointX = mPivotPointX;
                pivotPointY = mPivotPointY;
                break;
            default:
                throw new IllegalStateException("pivotPointX, pivotPointY for ScaleType " + mScaleType + " are not defined");
        }

        if (SHOW_LOGS)
            Logger.v(TAG, "updateTextureViewSize, pivotPointX " + pivotPointX + ", pivotPointY " + pivotPointY);

        float fitCoef = 1;
        switch (mScaleType) {
            case FILL:
                break;
            case BOTTOM:
            case CENTER_CROP:
            case TOP:
                if (mContentHeight > mContentWidth) { //Portrait video
                    fitCoef = viewWidth / (viewWidth * scaleX);
                } else { //Landscape video
                    fitCoef = viewHeight / (viewHeight * scaleY);
                }
                break;
        }

        mContentScaleX = fitCoef * scaleX;
        mContentScaleY = fitCoef * scaleY;

        mPivotPointX = pivotPointX;
        mPivotPointY = pivotPointY;

        updateMatrixScaleRotate();
        if (SHOW_LOGS) Logger.d(TAG, "<< updateTextureViewSize");
    }

    private void updateMatrixScaleRotate() {
        if (SHOW_LOGS) Logger.d(TAG, ">> updateMatrixScaleRotate, mContentRotation " + mContentRotation + ", mContentScaleMultiplier " + mContentScaleMultiplier + ", mPivotPointX " + mPivotPointX + ", mPivotPointY " + mPivotPointY);

        mTransformMatrix.reset();
        mTransformMatrix.setScale(mContentScaleX * mContentScaleMultiplier, mContentScaleY * mContentScaleMultiplier, mPivotPointX, mPivotPointY);
        mTransformMatrix.postRotate(mContentRotation, mPivotPointX, mPivotPointY);
        setTransform(mTransformMatrix);
        if (SHOW_LOGS) Logger.d(TAG, "<< updateMatrixScaleRotate, mContentRotation " + mContentRotation + ", mContentScaleMultiplier " + mContentScaleMultiplier + ", mPivotPointX " + mPivotPointX + ", mPivotPointY " + mPivotPointY);
    }

    private void updateMatrixTranslate() {
        if (SHOW_LOGS) {
            Logger.d(TAG, "updateMatrixTranslate, mContentX " + mContentX + ", mContentY " + mContentY);
        }

        float scaleX = mContentScaleX * mContentScaleMultiplier;
        float scaleY = mContentScaleY * mContentScaleMultiplier;

        mTransformMatrix.reset();
        mTransformMatrix.setScale(scaleX, scaleY, mPivotPointX, mPivotPointY);
        mTransformMatrix.postTranslate(mContentX, mContentY);
        setTransform(mTransformMatrix);
    }

    @Override
    public void setRotation(float degrees) {
        if (SHOW_LOGS)
            Logger.d(TAG, "setRotation, degrees " + degrees + ", mPivotPointX " + mPivotPointX + ", mPivotPointY " + mPivotPointY);

        mContentRotation = degrees;

        updateMatrixScaleRotate();
    }

    @Override
    public float getRotation() {
        return mContentRotation;
    }

    @Override
    public void setPivotX(float pivotX) {
        if (SHOW_LOGS) Logger.d(TAG, "setPivotX, pivotX " + pivotX);

        mPivotPointX = pivotX;
    }

    @Override
    public void setPivotY(float pivotY) {
        if (SHOW_LOGS) Logger.d(TAG, "setPivotY, pivotY " + pivotY);

        mPivotPointY = pivotY;
    }

    @Override
    public float getPivotX() {
        return mPivotPointX;
    }

    @Override
    public float getPivotY() {
        return mPivotPointY;
    }

    public float getContentAspectRatio() {
        return mContentWidth != null && mContentHeight != null
                ? (float) mContentWidth / (float) mContentHeight
                : 0;
    }

    /**
     * Use it to animate TextureView content x position
     * @param x
     */
    public final void setContentX(float x) {
        mContentX = (int) x - (getMeasuredWidth() - getScaledContentWidth()) / 2;
        updateMatrixTranslate();
    }

    /**
     * Use it to animate TextureView content x position
     * @param y
     */
    public final void setContentY(float y) {
        mContentY = (int) y - (getMeasuredHeight() - getScaledContentHeight()) / 2;
        updateMatrixTranslate();
    }

    protected final float getContentX() {
        return mContentX;
    }

    protected final float getContentY() {
        return mContentY;
    }

    /**
     * Use it to set content of a TextureView in the center of TextureView
     */
    public void centralizeContent() {
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int scaledContentWidth = getScaledContentWidth();
        int scaledContentHeight = getScaledContentHeight();

        if (SHOW_LOGS) Logger.d(TAG, "centralizeContent, measuredWidth " + measuredWidth + ", measuredHeight " + measuredHeight + ", scaledContentWidth " + scaledContentWidth + ", scaledContentHeight " + scaledContentHeight);

        mContentX = 0;
        mContentY = 0;

        if (SHOW_LOGS) Logger.d(TAG, "centerVideo, mContentX " + mContentX + ", mContentY " + mContentY);

        updateMatrixScaleRotate();
    }

    public Integer getScaledContentWidth() {
        return (int) (mContentScaleX * mContentScaleMultiplier * getMeasuredWidth());
    }

    public Integer getScaledContentHeight() {
        return (int) (mContentScaleY * mContentScaleMultiplier * getMeasuredHeight());
    }

    public float getContentScale() {
        return mContentScaleMultiplier;
    }

    public void setContentScale(float contentScale) {
        if (SHOW_LOGS) Logger.d(TAG, "setContentScale, contentScale " + contentScale);

        mContentScaleMultiplier = contentScale;
        updateMatrixScaleRotate();
    }

    protected final void setContentHeight(int height) {
        mContentHeight = height;
    }

    protected final Integer getContentHeight() {
        return mContentHeight;
    }

    protected final void setContentWidth(int width) {
        mContentWidth = width;
    }

    protected final Integer getContentWidth() {
        return mContentWidth;
    }

}
