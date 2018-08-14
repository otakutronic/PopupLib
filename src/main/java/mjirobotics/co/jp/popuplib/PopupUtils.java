package mjirobotics.co.jp.popuplib;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * PopupUtils
 * Created by Andy on 01/08/17.
 */
@SuppressWarnings({"SameParameterValue", "unused"})
public final class PopupUtils {

    /**
     * Constructor
     */
    private PopupUtils() {

    }

    /**
     * Calculates the on screen rectangle
     * @param view
     * @return
     */
    public static RectF calculeRectOnScreen(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return new RectF(location[0], location[1], location[0] + view.getMeasuredWidth(), location[1] + view.getMeasuredHeight());
    }

    /**
     * Calculates the window rectangle
     * @param view
     * @return
     */
    public static RectF calculeRectInWindow(View view) {
        int[] location = new int[2];
        view.getLocationInWindow(location);
        return new RectF(location[0], location[1], location[0] + view.getMeasuredWidth(), location[1] + view.getMeasuredHeight());
    }

    /**
     * Converts dp from px
     * @param px
     * @return
     */
    public static float dpFromPx(float px) {
        return px / Resources.getSystem().getDisplayMetrics().density;
    }

    /**
     * Converts px from dp
     * @param dp
     * @return
     */
    public static float pxFromDp(float dp) {
        return dp * Resources.getSystem().getDisplayMetrics().density;
    }

    /**
     * Sets the layout width of the view
     * @param view
     * @param width
     */
    public static void setWidth(View view, float width) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams((int) width, view.getHeight());
        } else {
            params.width = (int) width;
        }
        view.setLayoutParams(params);
        ((TextView) view).setWidth((int)width);
    }

    /**
     * Returns the arrow direction based on paramter passed
     * @param tooltipGravity
     * @return
     */
    public static int tooltipGravityToArrowDirection(int tooltipGravity) {
        switch (tooltipGravity) {
            case Gravity.START:
                return ArrowDrawable.RIGHT;
            case Gravity.END:
                return ArrowDrawable.LEFT;
            case Gravity.TOP:
                return ArrowDrawable.BOTTOM;
            case Gravity.BOTTOM:
                return ArrowDrawable.TOP;
            case Gravity.CENTER:
                return ArrowDrawable.TOP;
            default:
                throw new IllegalArgumentException("Gravity must have be CENTER, START, END, TOP or BOTTOM.");
        }
    }

    /**
     * Sets the views x pos
     * @param view
     * @param x
     */
    public static void setX(View view, int x) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            view.setX(x);
        } else {
            ViewGroup.MarginLayoutParams marginParams = getOrCreateMarginLayoutParams(view);
            marginParams.leftMargin = x - view.getLeft();
            view.setLayoutParams(marginParams);
        }
    }

    /**
     * Sets the views y pos
     * @param view
     * @param y
     */
    public static void setY(View view, int y) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            view.setY(y);
        } else {
            ViewGroup.MarginLayoutParams marginParams = getOrCreateMarginLayoutParams(view);
            marginParams.topMargin = y - view.getTop();
            view.setLayoutParams(marginParams);
        }
    }

    /**
     * Gets or creates margin layout params
     * @param view
     * @return
     */
    private static ViewGroup.MarginLayoutParams getOrCreateMarginLayoutParams(View view) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp != null) {
            if (lp instanceof ViewGroup.MarginLayoutParams) {
                return (ViewGroup.MarginLayoutParams) lp;
            } else {
                return new ViewGroup.MarginLayoutParams(lp);
            }
        } else {
            return new ViewGroup.MarginLayoutParams(view.getWidth(), view.getHeight());
        }
    }

    /**
     * Removes on global listener
     * @param view
     * @param listener
     */
    public static void removeOnGlobalLayoutListener(View view, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        } else {
            //noinspection deprecation
            view.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        }
    }

    /**
     * Sets the appearance of the text
     * @param tv
     * @param textAppearanceRes
     */
    public static void setTextAppearance(TextView tv, @StyleRes int textAppearanceRes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tv.setTextAppearance(textAppearanceRes);
        } else {
            //noinspection deprecation
            tv.setTextAppearance(tv.getContext(), textAppearanceRes);
        }
    }

    /**
     * Returns the color
     * @param context
     * @param colorRes
     * @return the color
     */
    public static int getColor(Context context, @ColorRes int colorRes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(colorRes);
        } else {
            //noinspection deprecation
            return context.getResources().getColor(colorRes);
        }
    }

    /**
     * Returns the drawable context
     * @param context
     * @param drawableRes
     * @return Drawable
     */
    public static Drawable getDrawable(Context context, @DrawableRes int drawableRes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getDrawable(drawableRes);
        } else {
            //noinspection deprecation
            return context.getResources().getDrawable(drawableRes);
        }
    }

    /**
     * Verify if the first child of the rootView is a FrameLayout.
     * Used for cases where the Popup is created inside a Dialog or DialogFragment.
     * @param anchorView
     * @return FrameLayout or anchorView.getRootView()
     */
    public static ViewGroup findFrameLayout(View anchorView) {
        ViewGroup rootView = (ViewGroup) anchorView.getRootView();
        if (rootView.getChildCount() == 1 && rootView.getChildAt(0) instanceof FrameLayout) {
            rootView = (ViewGroup) rootView.getChildAt(0);
        }
        return rootView;
    }
}
