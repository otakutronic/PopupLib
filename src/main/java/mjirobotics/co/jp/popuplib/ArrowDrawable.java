package mjirobotics.co.jp.popuplib;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.ColorInt;

/**
 * ArrowDrawable
 * Created by Andy on 01/08/17.
 */
public class ArrowDrawable extends ColorDrawable {

    public static final int LEFT = 0, TOP = 1, RIGHT = 2, BOTTOM = 3, AUTO = 4;

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final int mBackgroundColor;
    private Path mPath;
    private final int mDirection;

    /**
     * Sets forefround color
     * @param foregroundColor
     * @param direction
     */
    ArrowDrawable(@ColorInt int foregroundColor, int direction) {
        this.mBackgroundColor = Color.TRANSPARENT;
        this.mPaint.setColor(foregroundColor);
        this.mDirection = direction;
    }

    /**
     * Updates path on bounds change
     * @param bounds
     */
    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        updatePath(bounds);
    }

    /**
     * Sets the arrow path
     * @param bounds
     */
    private synchronized void updatePath(Rect bounds) {
        mPath = new Path();

        switch (mDirection) {
            case LEFT:
                mPath.moveTo(bounds.width(), bounds.height());
                mPath.lineTo(0, bounds.height() / 2);
                mPath.lineTo(bounds.width(), 0);
                mPath.lineTo(bounds.width(), bounds.height());
                break;
            case TOP:
                mPath.moveTo(0, bounds.height());
                mPath.lineTo(bounds.width() / 2, 0);
                mPath.lineTo(bounds.width(), bounds.height());
                mPath.lineTo(0, bounds.height());
                break;
            case RIGHT:
                mPath.moveTo(0, 0);
                mPath.lineTo(bounds.width(), bounds.height() / 2);
                mPath.lineTo(0, bounds.height());
                mPath.lineTo(0, 0);
                break;
            case BOTTOM:
                mPath.moveTo(0, 0);
                mPath.lineTo(bounds.width() / 2, bounds.height());
                mPath.lineTo(bounds.width(), 0);
                mPath.lineTo(0, 0);
                break;
        }
        mPath.close();
    }

    /**
     * Draws the arrow
     * @param canvas
     */
    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(mBackgroundColor);
        if (mPath == null)
            updatePath(getBounds());
        canvas.drawPath(mPath, mPaint);
    }

    /**
     * Sets alpha
     * @param alpha
     */
    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    /**
     * Sets color
     * @param color
     */
    public void setColor(@ColorInt int color) {
        mPaint.setColor(color);
    }

    /**
     * Sets color filter
     * @param colorFilter
     */
    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    /**
     * Gets opacity
     * @return int
     */
    @Override
    public int getOpacity() {
        if (mPaint.getColorFilter() != null) {
            return PixelFormat.TRANSLUCENT;
        }

        switch (mPaint.getColor() >>> 24) {
            case 255:
                return PixelFormat.OPAQUE;
            case 0:
                return PixelFormat.TRANSPARENT;
        }
        return PixelFormat.TRANSLUCENT;
    }
}
