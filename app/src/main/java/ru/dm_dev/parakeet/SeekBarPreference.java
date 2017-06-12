package ru.dm_dev.parakeet;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class SeekBarPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener {

    private static final String ANDROIDNS = "http://schemas.android.com/apk/res/android";
    private static final String PREFERENCENS = "http://schemas.android.com/apk/res/ru.dm_dev.parakeet";

    private static final int DEFAULT_MIN_VALUE = 1;
    private static final int DEFAULT_MAX_VALUE = 100;

    private static final String TAG = "SeekBarPreference";

    private int mMinValue, mMaxValue, mDefaultValue = 0;
    private int mValue = DEFAULT_MIN_VALUE;
    private String mSuffix;
    private String mDialogMessage;
    private Context mContext;

    private SeekBar mSeekBar;
    private TextView mSplashText, mValueText;

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        mDialogMessage = attrs.getAttributeValue(ANDROIDNS, "dialogMessage");
        mSuffix = attrs.getAttributeValue(ANDROIDNS, "text");
        mMaxValue = attrs.getAttributeIntValue(PREFERENCENS, "maxValue", DEFAULT_MAX_VALUE);
        mMinValue = attrs.getAttributeIntValue(PREFERENCENS, "minValue", DEFAULT_MIN_VALUE);
        mDefaultValue = attrs.getAttributeIntValue(ANDROIDNS, "defaultValue", mMinValue);

        Log.d(TAG,String.format("SeekBarPreference: minValue = %d, maxValue = %d, defaultValue = %d", mMinValue, mMaxValue, mDefaultValue));
    }

    @Override
    protected View onCreateDialogView() {
        LinearLayout.LayoutParams params;
        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(6, 6, 6, 6);

        if (mDialogMessage != null) {
            mSplashText = new TextView(mContext);
            mSplashText.setText(mDialogMessage);
            layout.addView(mSplashText);
        }
        mValueText = new TextView(mContext);
        mValueText.setGravity(Gravity.CENTER_HORIZONTAL);
        mValueText.setTextSize(32);
        params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        layout.addView(mValueText, params);

        mSeekBar = new SeekBar(mContext);
        mSeekBar.setOnSeekBarChangeListener(this);
        layout.addView(mSeekBar, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        if (shouldPersist()) {
            mValue = getPersistedInt(mDefaultValue);
        }

        Log.d(TAG,String.format("onCreateDialogView: mValue = %d", mValue));

        mSeekBar.setMax(mMaxValue - mMinValue);
        mSeekBar.setProgress(mValue - mMinValue);
        return layout;
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        mSeekBar.setMax(mMaxValue - mMinValue);
        mSeekBar.setProgress(mValue - mMinValue);

        Log.d(TAG,String.format("onBindDialogView: minValue = %d, maxValue = %d, defaultValue = %d, mValue = %d", mMinValue, mMaxValue, mDefaultValue, mValue));
    }

    @Override
    protected void onSetInitialValue(boolean restore, Object defaultValue) {
        super.onSetInitialValue(restore, defaultValue);
        if (restore) {
            mValue = shouldPersist() ? getPersistedInt(mDefaultValue) : DEFAULT_MIN_VALUE;
        } else {
            mValue = (Integer) defaultValue;
        }
        Log.d(TAG,String.format("onSetInitialValue: restore = %b, mValue = %d", restore, mValue));
    }

    public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
        mValue = value + mMinValue;
        String t = String.valueOf(mValue);
        mValueText.setText(mSuffix == null ? t : t.concat(mSuffix));
        if (shouldPersist()) {
            persistInt(mValue);
        }
        callChangeListener(new Integer(mValue));
    }

    public void onStartTrackingTouch(SeekBar seek) {
    }

    public void onStopTrackingTouch(SeekBar seek) {
    }

    public void setMax(int max) {
        mMaxValue = max;
    }

    public int getMax() {
        return mMaxValue;
    }

    public void setMin(int min) {
        mMinValue = min;
    }

    public int getMin() {
        return mMinValue;
    }

    public void setProgress(int progress) {
        mValue = progress;
        if (mSeekBar != null) {
            mSeekBar.setProgress(progress);
        }
        Log.d(TAG,String.format("setProgress: mValue = %d", mValue));
    }

    public int getProgress() {
        return mValue;
    }

//    @Override
//    protected void onDialogClosed(boolean positiveResult) {
//        super.onDialogClosed(positiveResult);
//        if (!positiveResult) {
//            return;
//        }
//        if (shouldPersist()) {
//            persistInt(mValue);
//        }
//        notifyChanged();
//    }

    @Override
    public CharSequence getSummary() {
        if (super.getSummary() != null) {
            return String.format(super.getSummary().toString(), getPersistedInt(mDefaultValue));
        } else {
            return String.valueOf(getPersistedInt(mDefaultValue));
        }
    }
}
