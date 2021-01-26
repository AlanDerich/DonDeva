package com.derich.dondeva.WeekViewActivity.CalendarView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.derich.dondeva.R;


public class DayView extends FrameLayout {

    private TextView mTextHour;

    private LinearLayout mSeparateHour;

    public DayView(Context context) {
        super(context);
        init(null);
    }

    public DayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public DayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.view_day, this, true);

        mTextHour = (TextView) findViewById(R.id.text_hour);
        mSeparateHour = (LinearLayout) findViewById(R.id.separate_hour);
    }

    public void setText(String text) {
        mTextHour.setText(text);
    }

    public float getHourTextWidth() {
        LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) mTextHour.getLayoutParams();
        float measureTextWidth = mTextHour.getPaint().measureText("12:00".toString());
        float f = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            f = Math.max(measureTextWidth, param.width) + param.getMarginEnd() + param.getMarginStart();
        }
        return f;
    }

    public float getSeparateHeight() {
        return mSeparateHour.getLayoutParams().height;
    }

    public void setHourSeparatorAsInvisible() {
        mSeparateHour.setVisibility(INVISIBLE);
    }

    public void setHourSeparatorAsVisible() {
        mSeparateHour.setVisibility(VISIBLE);
    }

    public void setHourSeparatorIsVisible(boolean b) {
        if (b) {
            setHourSeparatorAsVisible();
        } else {
            setHourSeparatorAsInvisible();
        }

    }

}
