package mjirobotics.co.jp.popuplib;

import android.os.CountDownTimer;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * TextView which displays a time based progressing text.
 * Created by Andy on 01/08/17.
 */
public class RollingText {

    private String mText;
    private TextView mTextView;
    private CountDownTimer mTextTimer;
    private int mMaxLines, mNextIndex, mPercent, mDuration;
    private Type mType;
    private MyObjectListener mListener;
    private ArrayList<Integer> mSeperatorIndexes;

    public enum Type {
        WORDS, CHARS
    }

    /**
     * Constructor
     * @param textView text to display
     * @param maxLines maximum lines
     * @param type roll type
     */
    public RollingText(TextView textView, int maxLines, Type type) {
        mTextView = textView;
        mTextView.setGravity(Gravity.TOP);
        setType(type);
        setMaxLines(maxLines);
        mText = "";
        textView.setText(mText);
        mListener = null;
        mSeperatorIndexes = new ArrayList<Integer>();
    }

    public TextView getView() {
        return mTextView;
    }

    public void setView(TextView view) {
        mTextView = view;
    }

    public Type getType() {
        return mType;
    }

    public void setType(Type type) {
        mType = type;
    }

    public int getMaxLines() {
        return mMaxLines;
    }

    public void setMaxLines(int maxLines) {
        mMaxLines = (maxLines > 0) ? maxLines : Integer.MAX_VALUE;
        mTextView.setMaxLines(mMaxLines);
    }

    /**
     * Displays rolling string text based on timer
     * @param text to display
     * @param duration time to roll text
     */
    public void setText(String text, int duration) {
        mText = text;
        mDuration = duration;
        mTextView.setVisibility(View.VISIBLE);
        setUp();
    }

    /**
     * Calculates and sets the height of the textview area
     * does set up things
     */
    private void setUp() {
        mTextView.setText(mText);
        mTextView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener(){

                    @Override
                    public void onGlobalLayout() {
                        // gets called after layout has been done but before display
                        // so we can get the height then hide the view
                        final int height = mTextView.getHeight() - 2;
                        mTextView.setHeight(height);
                        mTextView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        mTextView.setText("");
                        if(getType() == Type.CHARS) {
                            setRollingChars();
                        }
                        else {
                            setRollingWords();
                        }
                    }
                });
    }

    /**
     * Sets an update timer for the text to roll
     * one word at a time

     private void setRollingWords() {
     mNextIndex = 0 ;
     mTextTimer = new CountDownTimer(mDuration, 1) {

    @Override
    public void onTick(long millisUntilFinished) {
    final float fraction = millisUntilFinished / (float) mDuration;
    mPercent = (100 - (int) (fraction * 100));
    final double stringIndex = (mText.length() * 0.01) * mPercent;

    if((int) stringIndex > mNextIndex && mNextIndex != -1) {
    mNextIndex = mText.indexOf(mSeparator, mNextIndex + 1);
    final int substringIndex = (mNextIndex != -1) ? mNextIndex : mText.length();
    final String textSubstring = mText.substring(0, substringIndex);
    checkForScroll();
    mTextView.setText(textSubstring);
    }
    }

    @Override
    public void onFinish() {
    stop();
    mTextView.setText(mText);
    if (mListener != null)
    mListener.onObjectFinished("finished");
    }
    };
     mTextTimer.start();
     }*/

    /**
     * Sets an update timer for the text to roll
     * one word at a time
     */
    private void setRollingWords() {
        mNextIndex = 0 ;
        mSeperatorIndexes = getEndOfWordIndexes(mText);
        mTextTimer = new CountDownTimer(mDuration, 1) {

            @Override
            public void onTick(long millisUntilFinished) {
                final float fraction = millisUntilFinished / (float) mDuration;
                mPercent = (100 - (int) (fraction * 100));
                final double stringIndex = (mText.length() * 0.01) * mPercent;

                if((int) stringIndex >= mNextIndex) {
                    mNextIndex = (mSeperatorIndexes.size() > 0) ?
                            mSeperatorIndexes.get(0).intValue() : mText.length();
                    final String textSubstring = mText.substring(0, mNextIndex);
                    checkForScroll();
                    mTextView.setText(textSubstring);
                    if(mSeperatorIndexes.size() > 0) mSeperatorIndexes.remove(0);
                }
            }

            @Override
            public void onFinish() {
                stop();
                mTextView.setText(mText);
                if (mListener != null)
                    mListener.onObjectFinished("finished");
            }
        };
        mTextTimer.start();
    }

    /**
     * Sets an update timer for the text to roll
     * char by char
     */
    private void setRollingChars() {
        mTextTimer = new CountDownTimer(mDuration, 1) {

            @Override
            public void onTick(long millisUntilFinished) {
                final float fraction = millisUntilFinished / (float) mDuration;
                mPercent = (100 - (int) (fraction * 100));
                final double stringIndex = (mText.length() * 0.01) * mPercent;
                final String textSubstring = mText.substring(0, (int)stringIndex);
                checkForScroll();
                mTextView.setText(textSubstring);
            }

            @Override
            public void onFinish() {
                stop();
                mTextView.setText(mText);
                if (mListener != null)
                    mListener.onObjectFinished("finished");
            }
        };
        mTextTimer.start();
    }

    /**
     * Scrolls textview if number of lines exceeds max lines
     */
    private void checkForScroll() {
        mTextView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener(){

                    @Override
                    public void onGlobalLayout() {
                        mTextView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        final int lineCount = mTextView.getLineCount();

                        if(lineCount > mMaxLines && mMaxLines > 0) {
                            mTextView.setGravity(Gravity.BOTTOM);
                            mTextView.setMovementMethod(new ScrollingMovementMethod());
                        }
                    }
                });
    }

    /**
     * Returns an array list of end of word indexes
     * @param string
     * @return ArrayList<Integer> of Indexes
     */
    private ArrayList<Integer> getEndOfWordIndexes(final String string) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        char previousChar = (string.length() > 0) ? string.charAt(0) : Character.MIN_VALUE;

        for (int i = 0; i < string.length(); i++){
            char nextChar = string.charAt(i);
            LanguageDetector.CharType nextCharType = LanguageDetector.getCharType(nextChar);
            LanguageDetector.CharType previousCharType = LanguageDetector.getCharType(previousChar);

            if(LanguageDetector.isEndMark(nextChar) == false &&
                    (previousCharType != nextCharType ||
                            previousCharType == LanguageDetector.CharType.TYPE_CHINESE ||
                            previousChar == ' ')) {
                list.add(i);
            }
            previousChar = nextChar;
        }
        return list;
    }

    /**
     * Stop the rolling text
     */
    public void stop() {
        mTextTimer.cancel();
    }

    /**
     * Assign the listener implementing events interface that will receive the events
     * @param listener
     */
    public void setObjectListener(MyObjectListener listener) {
        this.mListener = listener;
    }

    /**
     * Event contract with methods that define events and arguments which are relevant event data.
     */
    public interface MyObjectListener {
        public void onObjectFinished(String message);
    }
}