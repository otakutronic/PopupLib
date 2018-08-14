package mjirobotics.co.jp.popuplib;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DimenRes;
import android.support.annotation.Dimension;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import popup.R;

/**
 * A popup that can be used to display text on screen.
 * @author Created by Andy on 01/08/17.
 * @see android.widget.PopupWindow
 */
@SuppressWarnings("SameParameterValue")
public class Popup implements PopupWindow.OnDismissListener {

    private static final String TAG = Popup.class.getSimpleName();

    // Default Resources
    private static final int DEFAULT_POPUP_WINDOW_STYLE_RES = android.R.attr.popupWindowStyle;
    private static final int DEFAULT_TEXT_APPEARANCE_RES = R.style.simpletooltip_default;
    private static final int DEFAULT_BACKGROUND_COLOR_RES = R.color.simpletooltip_background;
    private static final int DEFAULT_TEXT_COLOR_RES = R.color.simpletooltip_text;
    private static final int DEFAULT_ARROW_COLOR_RES = R.color.simpletooltip_arrow;
    private static final int DEFAULT_MARGIN_RES = R.dimen.simpletooltip_margin;
    private static final int DEFAULT_PADDING_RES = R.dimen.simpletooltip_padding;
    private static final int DEFAULT_ANIMATION_PADDING_RES = R.dimen.simpletooltip_animation_padding;
    private static final int DEFAULT_ANIMATION_DURATION_RES = R.integer.simpletooltip_animation_duration;
    private static final int DEFAULT_ARROW_WIDTH_RES = R.dimen.simpletooltip_arrow_width;
    private static final int DEFAULT_ARROW_HEIGHT_RES = R.dimen.simpletooltip_arrow_height;
    private static final int DEFAULT_OVERLAY_OFFSET_RES = R.dimen.simpletooltip_overlay_offset;

    private final Context mContext;
    private final MyActivityLifecycleCallbacks mCallbacks = new MyActivityLifecycleCallbacks();

    private OnDismissListener mOnDismissListener;
    private OnShowListener mOnShowListener;
    private PopupWindow mPopupWindow;
    private final int mGravity;
    private final int mArrowDirection;
    private final boolean mDismissOnInsideTouch;
    private final boolean mDismissOnOutsideTouch;
    private final boolean mModal;
    private final View mContentView;
    private View mContentLayout;
    @IdRes
    private final int mTextViewId;
    private final CharSequence mText;
    private final int mTime;
    private final boolean mRollingText;
    private final RollingText.Type mRollingTextType;
    private final int mMaxLines;
    private final int mWidth;
    private final boolean mRemoveOnTimer;
    private final View mAnchorView;
    private final boolean mTransparentOverlay;
    private final float mOverlayOffset;
    private final boolean mOverlayMatchParent;
    private final float mMaxWidth;
    private View mOverlay;
    private ViewGroup mRootView;
    private final boolean mShowArrow;
    private ImageView mArrowView;
    private final Drawable mArrowDrawable;
    private final boolean mAnimated;
    private AnimatorSet mAnimator;
    private final float mMargin;
    private final float mPaddingVertical;
    private final float mPaddingHorizonatal;
    private final float mAnimationPadding;
    private final long mAnimationDuration;
    private final float mArrowWidth;
    private final float mArrowHeight;
    private final boolean mFocusable;
    private boolean dismissed = false;
    private int mHighlightShape = OverlayView.HIGHLIGHT_SHAPE_OVAL;

    /**
     *
     * @param builder
     */
    private Popup(Builder builder) {
        mContext = builder.context;
        mGravity = builder.gravity;
        mArrowDirection = builder.arrowDirection;
        mDismissOnInsideTouch = builder.dismissOnInsideTouch;
        mDismissOnOutsideTouch = builder.dismissOnOutsideTouch;
        mModal = builder.modal;
        mContentView = builder.contentView;
        mTextViewId = builder.textViewId;
        mText = builder.text;
        mTime = builder.time;
        mRollingText = builder.rollingText;
        mRollingTextType = builder.rollingTextType;
        mMaxLines = builder.maxLines;
        mWidth = builder.width;
        mRemoveOnTimer = builder.removeOnTimer;
        mAnchorView = builder.anchorView;
        mTransparentOverlay = builder.transparentOverlay;
        mOverlayOffset = builder.overlayOffset;
        mOverlayMatchParent = builder.overlayMatchParent;
        mMaxWidth = builder.maxWidth;
        mShowArrow = builder.showArrow;
        mArrowWidth = builder.arrowWidth;
        mArrowHeight = builder.arrowHeight;
        mArrowDrawable = builder.arrowDrawable;
        mAnimated = builder.animated;
        mMargin = builder.margin;
        mPaddingVertical = builder.paddingVertical;
        mPaddingHorizonatal = builder.paddingHorizontal;
        mAnimationPadding = builder.animationPadding;
        mAnimationDuration = builder.animationDuration;
        mOnDismissListener = builder.onDismissListener;
        mOnShowListener = builder.onShowListener;
        mFocusable = builder.focusable;
        mRootView = PopupUtils.findFrameLayout(mAnchorView);
        mHighlightShape = builder.highlightShape;

        init();
    }

    /**
     * Initialization
     */
    private void init() {
        configPopupWindow();
        configContentView();

        Application app = ((Activity) mContext).getApplication();
        app.registerActivityLifecycleCallbacks(mCallbacks);
    }

    /**
     * sets up pop up window
     */
    private void configPopupWindow() {
        mPopupWindow = new PopupWindow(mContext, null, DEFAULT_POPUP_WINDOW_STYLE_RES);
        mPopupWindow.setOnDismissListener(this);
        mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.setClippingEnabled(false);
        mPopupWindow.setFocusable(mFocusable);
    }

    /**
     * shows the popup
     */
    public void show() {
        verifyDismissed();

        mContentLayout.getViewTreeObserver().addOnGlobalLayoutListener(mLocationLayoutListener);
        mContentLayout.getViewTreeObserver().addOnGlobalLayoutListener(mAutoDismissLayoutListener);

        mRootView.post(new Runnable() {
            @Override
            public void run() {
                if (mRootView.isShown())
                    mPopupWindow.showAtLocation(mRootView, Gravity.NO_GRAVITY, mRootView.getWidth(), mRootView.getHeight());
                else
                    Log.e(TAG, "Popup cannot be shown, root view is invalid or has been closed.");
            }
        });
    }

    /**
     * verifies dismissed
     */
    private void verifyDismissed() {
        if (dismissed) {
            throw new IllegalArgumentException("Popup has ben dismissed.");
        }
    }

    /**
     * Creates and adds an overlay
     */
    private void createOverlay() {
        mOverlay = mTransparentOverlay ? new View(mContext) : new OverlayView(mContext, mAnchorView, mHighlightShape, mOverlayOffset);
        if (mOverlayMatchParent)
            mOverlay.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        else
            mOverlay.setLayoutParams(new ViewGroup.LayoutParams(mRootView.getWidth(), mRootView.getHeight()));
        mOverlay.setOnTouchListener(mOverlayTouchListener);
        mRootView.addView(mOverlay);
    }

    /**
     * Returns the popups location
     * @return
     */
    private PointF calculePopupLocation() {
        PointF location = new PointF();

        final RectF anchorRect = PopupUtils.calculeRectInWindow(mAnchorView);
        final PointF anchorCenter = new PointF(anchorRect.centerX(), anchorRect.centerY());

        switch (mGravity) {
            case Gravity.START:
                location.x = anchorRect.left - mPopupWindow.getContentView().getWidth() - mMargin;
                location.y = anchorCenter.y - mPopupWindow.getContentView().getHeight() / 2f;
                break;
            case Gravity.END:
                location.x = anchorRect.right + mMargin;
                location.y = anchorCenter.y - mPopupWindow.getContentView().getHeight() / 2f;
                break;
            case Gravity.TOP:
                location.x = anchorCenter.x - mPopupWindow.getContentView().getWidth() / 2f;
                location.y = anchorRect.top - mPopupWindow.getContentView().getHeight() - mMargin;
                break;
            case Gravity.BOTTOM:
                location.x = anchorCenter.x - mPopupWindow.getContentView().getWidth() / 2f;
                location.y = anchorRect.bottom + mMargin;
                break;
            case Gravity.CENTER:
                location.x = anchorCenter.x - mPopupWindow.getContentView().getWidth() / 2f;
                location.y = anchorCenter.y - mPopupWindow.getContentView().getHeight() / 2f;
                break;
            default:
                throw new IllegalArgumentException("Gravity must have be CENTER, START, END, TOP or BOTTOM.");
        }

        return location;
    }

    /**
     * Configures the content view
     */
    private void configContentView() {

        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(mArrowDirection == ArrowDrawable.LEFT || mArrowDirection == ArrowDrawable.RIGHT ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);
        int layoutPadding = (int) (mAnimated ? mAnimationPadding : 0);
        linearLayout.setPadding(layoutPadding, layoutPadding, layoutPadding, layoutPadding);

        if (mShowArrow) {
            mArrowView = new ImageView(mContext);
            mArrowView.setImageDrawable(mArrowDrawable);
            LinearLayout.LayoutParams arrowLayoutParams;

            if (mArrowDirection == ArrowDrawable.TOP || mArrowDirection == ArrowDrawable.BOTTOM) {
                arrowLayoutParams = new LinearLayout.LayoutParams((int) mArrowWidth, (int) mArrowHeight, 0);
            } else {
                arrowLayoutParams = new LinearLayout.LayoutParams((int) mArrowHeight, (int) mArrowWidth, 0);
            }

            arrowLayoutParams.gravity = Gravity.CENTER;
            mArrowView.setLayoutParams(arrowLayoutParams);

            if (mArrowDirection == ArrowDrawable.BOTTOM || mArrowDirection == ArrowDrawable.RIGHT) {
                linearLayout.addView(mContentView);
                linearLayout.addView(mArrowView);
            } else {
                linearLayout.addView(mArrowView);
                linearLayout.addView(mContentView);
            }
        } else {
            linearLayout.addView(mContentView);
        }

        LinearLayout.LayoutParams contentViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0);
        contentViewParams.gravity = Gravity.CENTER;
        mContentView.setLayoutParams(contentViewParams);

        if (mDismissOnInsideTouch || mDismissOnOutsideTouch)
            mContentView.setOnTouchListener(mPopupWindowTouchListener);

        mContentLayout = linearLayout;
        mContentLayout.setVisibility(View.INVISIBLE);
        mPopupWindow.setContentView(mContentLayout);

        if (mContentView instanceof TextView) {
            TextView tv = (TextView) mContentView;
            setText(tv);
        } else {
            TextView tv = (TextView) mContentView.findViewById(mTextViewId);
            if (tv != null) {
                setText(tv);
            }
        }

        mContentView.setPadding((int) mPaddingHorizonatal, (int) mPaddingVertical, (int) mPaddingHorizonatal, (int) mPaddingVertical);
        if(mWidth != 0) PopupUtils.setWidth(mContentView, mWidth);
    }

    /**
     * Sets the text
     * @param text view
     */
    public void setText(TextView tv) {
        if(mRollingText) {
            RollingText rollingText = new RollingText(tv, mMaxLines, mRollingTextType);
            rollingText.setText(mText.toString(), mTime);
            if(mRemoveOnTimer) {
                rollingText.setObjectListener(new RollingText.MyObjectListener() {
                    @Override
                    public void onObjectFinished(String message) {
                        dismiss();
                    }
                });
            }
        } else {
            tv.setText(mText);
        }
    }

    /**
     * clean up and remove
     */
    public void dismiss() {
        if (dismissed)
            return;

        dismissed = true;
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
            Application app = ((Activity) mContext).getApplication();
            app.unregisterActivityLifecycleCallbacks(mCallbacks);
        }
    }

    /**
     * Indicates whether this popup is showing on screen.
     * @return if the popup is showing
     */
    public boolean isShowing() {
        return mPopupWindow != null && mPopupWindow.isShowing();
    }

    /**
     * findViewById
     * @param id
     * @param <T>
     * @return
     */
    public <T extends View> T findViewById(int id) {
        //noinspection unchecked
        return (T) mContentLayout.findViewById(id);
    }

    /**
     *
     */
    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public class MyActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityPaused(Activity activity) {
            dismiss();
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

        @Override
        public void onActivityStarted(Activity activity) {}

        @Override
        public void onActivityResumed(Activity activity) {}

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

        @Override
        public void onActivityStopped(Activity activity) {}

        @Override
        public void onActivityDestroyed(Activity activity) {}
    }

    /**
     * onDismiss
     */
    @Override
    public void onDismiss() {
        dismissed = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (mAnimator != null) {
                mAnimator.removeAllListeners();
                mAnimator.end();
                mAnimator.cancel();
                mAnimator = null;
            }
        }

        if (mRootView != null && mOverlay != null) {
            mRootView.removeView(mOverlay);
        }
        mRootView = null;
        mOverlay = null;

        if (mOnDismissListener != null)
            mOnDismissListener.onDismiss(this);
        mOnDismissListener = null;

        PopupUtils.removeOnGlobalLayoutListener(mPopupWindow.getContentView(), mLocationLayoutListener);
        PopupUtils.removeOnGlobalLayoutListener(mPopupWindow.getContentView(), mArrowLayoutListener);
        PopupUtils.removeOnGlobalLayoutListener(mPopupWindow.getContentView(), mShowLayoutListener);
        PopupUtils.removeOnGlobalLayoutListener(mPopupWindow.getContentView(), mAnimationLayoutListener);
        PopupUtils.removeOnGlobalLayoutListener(mPopupWindow.getContentView(), mAutoDismissLayoutListener);

        mPopupWindow = null;
    }

    /**
     * onTouch
     */
    private final View.OnTouchListener mPopupWindowTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getX() > 0 && event.getX() < v.getWidth() &&
                    event.getY() > 0 && event.getY() < v.getHeight()) {
                if (mDismissOnInsideTouch) {
                    dismiss();
                    return mModal;
                }
                return false;
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                v.performClick();
            }
            return mModal;
        }
    };

    /**
     * onTouch
     */
    private final View.OnTouchListener mOverlayTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mDismissOnOutsideTouch) {
                dismiss();
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                v.performClick();
            }
            return mModal;
        }
    };

    /**
     * onGlobalLayout
     */
    private final ViewTreeObserver.OnGlobalLayoutListener mLocationLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            final PopupWindow popup = mPopupWindow;
            if (popup == null || dismissed) return;

            if (mMaxWidth > 0 && mContentView.getWidth() > mMaxWidth) {
                PopupUtils.setWidth(mContentView, mMaxWidth);
                popup.update(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                return;
            }

            PopupUtils.removeOnGlobalLayoutListener(popup.getContentView(), this);
            popup.getContentView().getViewTreeObserver().addOnGlobalLayoutListener(mArrowLayoutListener);
            PointF location = calculePopupLocation();
            popup.setClippingEnabled(true);
            popup.update((int) location.x, (int) location.y, popup.getWidth(), popup.getHeight());
            popup.getContentView().requestLayout();
            createOverlay();
        }
    };

    /**
     * onGlobalLayout
     */
    private final ViewTreeObserver.OnGlobalLayoutListener mArrowLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            final PopupWindow popup = mPopupWindow;
            if (popup == null || dismissed) return;

            PopupUtils.removeOnGlobalLayoutListener(popup.getContentView(), this);

            popup.getContentView().getViewTreeObserver().addOnGlobalLayoutListener(mAnimationLayoutListener);
            popup.getContentView().getViewTreeObserver().addOnGlobalLayoutListener(mShowLayoutListener);
            if (mShowArrow) {
                RectF achorRect = PopupUtils.calculeRectOnScreen(mAnchorView);
                RectF contentViewRect = PopupUtils.calculeRectOnScreen(mContentLayout);
                float x, y;
                if (mArrowDirection == ArrowDrawable.TOP || mArrowDirection == ArrowDrawable.BOTTOM) {
                    x = mContentLayout.getPaddingLeft() + PopupUtils.pxFromDp(2);
                    float centerX = (contentViewRect.width() / 2f) - (mArrowView.getWidth() / 2f);
                    float newX = centerX - (contentViewRect.centerX() - achorRect.centerX());
                    if (newX > x) {
                        if (newX + mArrowView.getWidth() + x > contentViewRect.width()) {
                            x = contentViewRect.width() - mArrowView.getWidth() - x;
                        } else {
                            x = newX;
                        }
                    }
                    y = mArrowView.getTop();
                    y = y + (mArrowDirection == ArrowDrawable.BOTTOM ? -1 : +1);
                } else {
                    y = mContentLayout.getPaddingTop() + PopupUtils.pxFromDp(2);
                    float centerY = (contentViewRect.height() / 2f) - (mArrowView.getHeight() / 2f);
                    float newY = centerY - (contentViewRect.centerY() - achorRect.centerY());
                    if (newY > y) {
                        if (newY + mArrowView.getHeight() + y > contentViewRect.height()) {
                            y = contentViewRect.height() - mArrowView.getHeight() - y;
                        } else {
                            y = newY;
                        }
                    }
                    x = mArrowView.getLeft();
                    x = x + (mArrowDirection == ArrowDrawable.RIGHT ? -1 : +1);
                }
                PopupUtils.setX(mArrowView, (int) x);
                PopupUtils.setY(mArrowView, (int) y);
            }
            popup.getContentView().requestLayout();
        }
    };

    /**
     *
     */
    private final ViewTreeObserver.OnGlobalLayoutListener mShowLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            final PopupWindow popup = mPopupWindow;
            if (popup == null || dismissed) return;

            PopupUtils.removeOnGlobalLayoutListener(popup.getContentView(), this);

            if (mOnShowListener != null)
                mOnShowListener.onShow(Popup.this);
            mOnShowListener = null;

            mContentLayout.setVisibility(View.VISIBLE);
        }
    };

    /**
     * onGlobalLayout
     */
    private final ViewTreeObserver.OnGlobalLayoutListener mAnimationLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            final PopupWindow popup = mPopupWindow;
            if (popup == null || dismissed) return;

            PopupUtils.removeOnGlobalLayoutListener(popup.getContentView(), this);

            if (mAnimated) startAnimation();

            popup.getContentView().requestLayout();
        }
    };

    /**
     * startAnimation
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void startAnimation() {
        final String property = mGravity == Gravity.TOP || mGravity == Gravity.BOTTOM ? "translationY" : "translationX";

        final ObjectAnimator anim1 = ObjectAnimator.ofFloat(mContentLayout, property, -mAnimationPadding, mAnimationPadding);
        anim1.setDuration(mAnimationDuration);
        anim1.setInterpolator(new AccelerateDecelerateInterpolator());

        final ObjectAnimator anim2 = ObjectAnimator.ofFloat(mContentLayout, property, mAnimationPadding, -mAnimationPadding);
        anim2.setDuration(mAnimationDuration);
        anim2.setInterpolator(new AccelerateDecelerateInterpolator());

        mAnimator = new AnimatorSet();
        mAnimator.playSequentially(anim1, anim2);
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!dismissed && isShowing()) {
                    animation.start();
                }
            }
        });
        mAnimator.start();
    }

    /**
     * Listener used to call when root is terminated without the popup is being closed.
     * It can occur when the popup is used within Dialogs.
     */
    private final ViewTreeObserver.OnGlobalLayoutListener mAutoDismissLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            final PopupWindow popup = mPopupWindow;
            if (popup == null || dismissed) return;

            if (!mRootView.isShown()) dismiss();
        }
    };

    /**
     * OnDismissListener
     */
    public interface OnDismissListener {
        void onDismiss(Popup tooltip);
    }

    /**
     * OnShowListener
     */
    public interface OnShowListener {
        void onShow(Popup tooltip);
    }

    /**
     * Class responsible for making it easier to build the object.
     * @author Created by Andy on 01/08/17.
     */
    @SuppressWarnings({"SameParameterValue", "unused"})
    public static class Builder {

        private final Context context;
        private boolean dismissOnInsideTouch = true;
        private boolean dismissOnOutsideTouch = false;
        private boolean modal = false;
        private View contentView;
        @IdRes
        private int textViewId = android.R.id.text1;
        private CharSequence text = "";
        private int time = 0;
        private RollingText.Type rollingTextType = RollingText.Type.WORDS;
        private boolean rollingText = false;
        private int maxLines = 0;
        private int width = 0;
        private boolean removeOnTimer = true;
        private View anchorView;
        private int arrowDirection = ArrowDrawable.AUTO;
        private int gravity = Gravity.BOTTOM;
        private boolean transparentOverlay = true;
        private float overlayOffset = -1;
        private boolean overlayMatchParent = true;
        private float maxWidth;
        private boolean showArrow = true;
        private Drawable arrowDrawable;
        private boolean animated = false;
        private float margin = -1;
        private float paddingVertical = -1;
        private float paddingHorizontal = -1;
        private float animationPadding = -1;
        private OnDismissListener onDismissListener;
        private OnShowListener onShowListener;
        private long animationDuration;
        private int backgroundColor;
        private int textColor;
        private int arrowColor;
        private float arrowHeight;
        private float arrowWidth;
        private boolean focusable;
        private int highlightShape = OverlayView.HIGHLIGHT_SHAPE_OVAL;

        public Builder(Context context) {
            this.context = context;
        }

        public Popup build() throws IllegalArgumentException {
            validateArguments();
            if (backgroundColor == 0) {
                backgroundColor = PopupUtils.getColor(context, DEFAULT_BACKGROUND_COLOR_RES);
            }
            if (textColor == 0) {
                textColor = PopupUtils.getColor(context, DEFAULT_TEXT_COLOR_RES);
            }
            if (contentView == null) {
                TextView tv = new TextView(context);
                PopupUtils.setTextAppearance(tv, DEFAULT_TEXT_APPEARANCE_RES);
                tv.setBackgroundColor(backgroundColor);
                tv.setTextColor(textColor);
                contentView = tv;
            }
            if (arrowColor == 0) {
                arrowColor = PopupUtils.getColor(context, DEFAULT_ARROW_COLOR_RES);
            }
            if (margin < 0) {
                margin = context.getResources().getDimension(DEFAULT_MARGIN_RES);
            }
            if (paddingVertical < 0) {
                paddingVertical = context.getResources().getDimension(DEFAULT_PADDING_RES);
            }
            if (paddingHorizontal < 0) {
                paddingHorizontal = context.getResources().getDimension(DEFAULT_PADDING_RES);
            }
            if (animationPadding < 0) {
                animationPadding = context.getResources().getDimension(DEFAULT_ANIMATION_PADDING_RES);
            }
            if (animationDuration == 0) {
                animationDuration = context.getResources().getInteger(DEFAULT_ANIMATION_DURATION_RES);
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                animated = false;
            }
            if (showArrow) {
                if (arrowDirection == ArrowDrawable.AUTO)
                    arrowDirection = PopupUtils.tooltipGravityToArrowDirection(gravity);
                if (arrowDrawable == null)
                    arrowDrawable = new ArrowDrawable(arrowColor, arrowDirection);
                if (arrowWidth == 0)
                    arrowWidth = context.getResources().getDimension(DEFAULT_ARROW_WIDTH_RES);
                if (arrowHeight == 0)
                    arrowHeight = context.getResources().getDimension(DEFAULT_ARROW_HEIGHT_RES);
            }
            if (highlightShape < 0 || highlightShape > OverlayView.HIGHLIGHT_SHAPE_RECTANGULAR) {
                highlightShape = OverlayView.HIGHLIGHT_SHAPE_OVAL;
            }
            if (overlayOffset < 0) {
                overlayOffset = context.getResources().getDimension(DEFAULT_OVERLAY_OFFSET_RES);
            }
            return new Popup(this);
        }

        /**
         * validates arguments
         * @throws IllegalArgumentException
         */
        private void validateArguments() throws IllegalArgumentException {
            if (context == null) {
                throw new IllegalArgumentException("Context not specified.");
            }
            if (anchorView == null) {
                throw new IllegalArgumentException("Anchor view not specified.");
            }
        }

        /**
         * Defines new custom content for the popup
         * @param textView New content for the popup
         * @return Builder
         */
        public Builder contentView(TextView textView) {
            this.contentView = textView;
            this.textViewId = 0;
            return this;
        }

        /**
         * Defines new custom content for the tooltip.
         * @param contentView New content for the popup, can be one or any custom component
         * @param textViewId  ResID for the existing within the. Default is
         * @return Builder
         */
        public Builder contentView(View contentView, @IdRes int textViewId) {
            this.contentView = contentView;
            this.textViewId = textViewId;
            return this;
        }

        /**
         * Defines new custom content for the popup.
         * @param contentViewId LayoutId that will be inflated as the new content for the popup.
         * @param textViewId    ResID for the existing within the. Default is
         * @return Builder
         */
        public Builder contentView(@LayoutRes int contentViewId, @IdRes int textViewId) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.contentView = inflater.inflate(contentViewId, null, false);
            this.textViewId = textViewId;
            return this;
        }

        /**
         * Defines new custom content for the popup.
         * @param contentViewId LayoutId that will be inflated as the new content for the popup.
         * @return Builder
         */
        public Builder contentView(@LayoutRes int contentViewId) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.contentView = inflater.inflate(contentViewId, null, false);
            this.textViewId = 0;
            return this;
        }

        /**
         * Sets whether the popup will close when it receives a click within its area.
         * @param dismissOnInsideTouch To close when you receive the click inside.
         * @return Builder
         */
        public Builder dismissOnInsideTouch(boolean dismissOnInsideTouch) {
            this.dismissOnInsideTouch = dismissOnInsideTouch;
            return this;
        }

        /**
         * Sets whether the popup will close when you receive a click outside your area.
         * @param dismissOnOutsideTouch To close when you get the click off.
         * @return Builder
         */
        public Builder dismissOnOutsideTouch(boolean dismissOnOutsideTouch) {
            this.dismissOnOutsideTouch = dismissOnOutsideTouch;
            return this;
        }

        /**
         * Sets whether the screen will lock while the popup is open.
         * @param modal To lock the screen
         * @return Builder
         */
        public Builder modal(boolean modal) {
            this.modal = modal;
            return this;
        }

        /**
         * Sets the text to be displayed in the popup.
         *
         * @param text Text that will be displayed.
         * @return this
         */
        public Builder text(CharSequence text) {
            this.text = text;
            return this;
        }

        /**
         * Sets whether the text will "roll"
         * @param rollingText true for rolling text
         * @return Builder
         */
        public Builder rollingText(boolean rollingText) {
            this.rollingText = rollingText;
            return this;
        }

        /**
         * Sets whether the text will be removed after time is up
         * @param removeOnTimer true for remove
         * @return this
         */
        public Builder removeOnTimer(boolean removeOnTimer) {
            this.removeOnTimer = removeOnTimer;
            return this;
        }

        /**
         * Sets how the text will "roll"
         * @param type true for rolling text
         * @return Builder
         */
        public Builder rollingTextType(RollingText.Type type) {
            this.rollingTextType = type;
            return this;
        }

        /**
         * Sets the time for the text to "roll"
         * @param time the duration for text to roll
         * @return Builder
         */
        public Builder time(int time) {
            this.time = time;
            return this;
        }

        /**
         * Sets the maxLines the textview can display
         * @param maxLines max number of Lines for the textview
         * @return Builder
         */
        public Builder maxLines(int maxLines) {
            this.maxLines = maxLines;
            return this;
        }

        /**
         * Sets the text that will be displayed in the popup.
         * @param textRes ID do resource da String.
         * @return this
         */
        public Builder text(@StringRes int textRes) {
            this.text = context.getString(textRes);
            return this;
        }

        /**
         * Sets the width of the textview popup.
         * @param width width of popup
         * @return Builder
         */
        public Builder width(int width) {
            this.width = width;
            return this;
        }

        /**
         * Set the target that the popup will point. Make sure that the anchor shold be showing in the screen.
         * @param anchorView that the popup will point.
         * @return this
         */
        public Builder anchorView(View anchorView) {
            this.anchorView = anchorView;
            return this;
        }

        /**
         * Defines where the tooltip will be positioned relative to the
         * @param gravity side to which the tooltip will be positioned.
         * @return Builder
         */
        public Builder gravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        /**
         * Sets the direction in which the arrow will be created.
         * @param arrowDirection Direction in which the arrow will be created.
         * @return Builder
         */
        public Builder arrowDirection(int arrowDirection) {
            this.arrowDirection = arrowDirection;
            return this;
        }

        /**
         * Sets whether the screen background will be darkened or transparent while the popup is open.
         * @param transparentOverlay For transparent background,
         * @return Builder
         */
        public Builder transparentOverlay(boolean transparentOverlay) {
            this.transparentOverlay = transparentOverlay;
            return this;
        }

        /**
         * Sets the maximum width of the popup.
         * @param maxWidthRes the maximum width.
         * @return this
         */
        public Builder maxWidth(@DimenRes int maxWidthRes) {
            this.maxWidth = context.getResources().getDimension(maxWidthRes);
            return this;
        }

        /**
         * Sets the maximum width of the popup.
         * @param maxWidth the maximum width.
         * @return Builder
         */
        public Builder maxWidth(float maxWidth) {
            this.maxWidth = maxWidth;
            return this;
        }

        /**
         * Sets whether the popup will be animated while it is open.
         * @param animated For animated popup
         * @return Builder
         */
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        public Builder animated(boolean animated) {
            this.animated = animated;
            return this;
        }

        /**
         * Sets the size of the offset during the animation.
         * @param animationPadding Size of the displacement in pixels.
         * @return Builder
         */
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        public Builder animationPadding(float animationPadding) {
            this.animationPadding = animationPadding;
            return this;
        }

        /**
         * Sets the size of the offset during the animation.
         * @param animationPaddingRes ResID of the displacement size.
         * @return Builder
         */
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        public Builder animationPadding(@DimenRes int animationPaddingRes) {
            this.animationPadding = context.getResources().getDimension(animationPaddingRes);
            return this;
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        public Builder animationDuration(long animationDuration) {
            this.animationDuration = animationDuration;
            return this;
        }

        /**
         * Sets the padding between the top of the popup and its contents.
         * @param padding Size of the padding in pixels.
         * @return Builder
         */
        public Builder paddingVertical(float padding) {
            this.paddingVertical = padding;
            return this;
        }

        /**
         * Sets the padding between the top of the popup and its contents.
         *
         * @param padding Size of the padding in pixels.
         * @return this
         * @see Builder
         */
        public Builder paddingVertical(@DimenRes int paddingRes) {
            this.paddingVertical = context.getResources().getDimension(paddingRes);
            return this;
        }

        /**
         * Sets the padding between the edge of the popup and its contents.
         * @param padding Size of the padding in pixels.
         * @return Builder
         */
        public Builder paddingHorizontal(float padding) {
            this.paddingHorizontal = padding;
            return this;
        }

        /**
         * Sets the margin between Tooltip and Pattern
         * @param margin Margin size in pixels.
         * @return Builder
         */
        public Builder margin(float margin) {
            this.margin = margin;
            return this;
        }

        /**
         * Sets the margin between the popup.
         * @param marginRes ResID of the size of the margin.
         * @return Builder
         */
        public Builder margin(@DimenRes int marginRes) {
            this.margin = context.getResources().getDimension(marginRes);
            return this;
        }

        /**
         * Sets text color
         * @param textColor
         * @return Builder
         */
        public Builder textColor(int textColor) {
            this.textColor = textColor;
            return this;
        }

        /**
         * Sets background color
         * @param backgroundColor
         * @return Builder
         */
        public Builder backgroundColor(@ColorInt int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        /**
         * Indicates whether to be generated indicative arrow.
         * @param showArrow to show arrow
         * @return this
         */
        public Builder showArrow(boolean showArrow) {
            this.showArrow = showArrow;
            return this;
        }

        /**
         * Sets the arrow drawable
         * @param arrowDrawable
         * @return Builder
         */
        public Builder arrowDrawable(Drawable arrowDrawable) {
            this.arrowDrawable = arrowDrawable;
            return this;
        }

        /**
         * Sets the arrow drawable
         * @param drawableRes
         * @return Builder
         */
        public Builder arrowDrawable(@DrawableRes int drawableRes) {
            this.arrowDrawable = PopupUtils.getDrawable(context, drawableRes);
            return this;
        }

        /**
         * Sets the arrow color
         * @param arrowColor
         * @return Builder
         */
        public Builder arrowColor(@ColorInt int arrowColor) {
            this.arrowColor = arrowColor;
            return this;
        }

        /**
         * Height of the arrow. This value is automatically set in the Width or Height
         * @param arrowHeight Height in pixels.
         * @return this
         * @see Builder#arrowWidth(float)
         */
        public Builder arrowHeight(float arrowHeight) {
            this.arrowHeight = arrowHeight;
            return this;
        }

        /**
         * Width of the arrow. This value is automatically set in the Width or Height
         * @param arrowWidth Width in pixels.
         * @return Builder
         */
        public Builder arrowWidth(float arrowWidth) {
            this.arrowWidth = arrowWidth;
            return this;
        }

        /**
         * Listener for on dismiss
         * @param onDismissListener
         * @return Builder
         */
        public Builder onDismissListener(OnDismissListener onDismissListener) {
            this.onDismissListener = onDismissListener;
            return this;
        }

        /**
         * Listener for on show
         * @param onShowListener
         * @return Builder
         */
        public Builder onShowListener(OnShowListener onShowListener) {
            this.onShowListener = onShowListener;
            return this;
        }

        /**
         * Enables focus on the contents of the popup.
         * @param focusable Can receive focus.
         * @return this
         */
        public Builder focusable(boolean focusable) {
            this.focusable = focusable;
            return this;
        }

        /**
         * Configure the the Shape type.
         * @param highlightShape Shape type.
         * @return Builder
         */
        public Builder highlightShape(int highlightShape) {
            this.highlightShape = highlightShape;
            return this;
        }

        /**
         * Margin between view and highlight Shape border.
         * @param overlayOffset Size in pixels.
         * @return this
         */
        public Builder overlayOffset(@Dimension float overlayOffset) {
            this.overlayOffset = overlayOffset;
            return this;
        }

        /**
         * Sets the behavior of the overlay view. Used for cases where the Overlay view can not be MATCH_PARENT.
         * Like in a Dialog or DialogFragment
         * @param overlayMatchParent True if the overlay should be MATCH_PARENT. False if it should get the same size as the parent.
         * @return Builder
         */
        public Builder overlayMatchParent(boolean overlayMatchParent) {
            this.overlayMatchParent = overlayMatchParent;
            return this;
        }
    }
}
