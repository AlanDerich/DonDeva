package com.derich.dondeva.WeekViewActivity.CalendarView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.derich.dondeva.R;
import com.derich.dondeva.WeekViewActivity.CalendarView.Decorations.CdvDecoration;
import com.derich.dondeva.WeekViewActivity.CalendarView.Decorations.CdvDecorationDefault;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class CalendarDayView extends FrameLayout {

    private int mDayHeight = 0;

    private int mEventMarginLeft = 0;

    private int mHourWidth = 120;

    private int mTimeHeight = 120;

    private int mSeparateHourHeight = 0;

    private int mStartHour = 0;

    private int mEndHour = 23;

    private LinearLayout mLayoutDayView;

    private FrameLayout mLayoutEvent;

    private CdvDecoration mDecoration;

    private List<Event> mEvents;


    public CalendarDayView(Context context) {
        super(context);
        init(null);
    }

    public CalendarDayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CalendarDayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public void init(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.view_day_calendar, this, true);

        mLayoutDayView = (LinearLayout) findViewById(R.id.dayview_container);
        mLayoutEvent = (FrameLayout) findViewById(R.id.event_container);

        mDayHeight = getResources().getDimensionPixelSize(R.dimen.dayHeight);

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CalendarView);
            try {
                mEventMarginLeft =
                        a.getDimensionPixelSize(R.styleable.CalendarView_eventMarginLeft,
                                mEventMarginLeft);
                mDayHeight =
                        a.getDimensionPixelSize(R.styleable.CalendarView_dayHeight, mDayHeight);
                mStartHour = a.getInt(R.styleable.CalendarView_startHour, mStartHour);
                mEndHour = a.getInt(R.styleable.CalendarView_endHour, mEndHour);
            } finally {
                a.recycle();
            }
        }

        mEvents = new ArrayList<>();
        mDecoration = new CdvDecorationDefault(getContext());

//        refresh(null);
    }

    public void refresh(String currentD, String messg) {
        drawDayViews();
        drawEvents(currentD);
    }

    private void drawDayViews() {
        mLayoutDayView.removeAllViews();
        DayView dayView = null;
        for (int i = mStartHour; i <= mEndHour; i++) {
            dayView = getDecoration().getDayView(i);
            mLayoutDayView.addView(dayView);
        }
        mHourWidth = (int) dayView.getHourTextWidth();
        mTimeHeight = mDayHeight;
        mSeparateHourHeight = (int) dayView.getSeparateHeight();
    }

    private void drawEvents(String currentD) {
        if (currentD!=null){
        mLayoutEvent.removeAllViews();
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
        String kkk;
        Date now=new Date();
        for (Event event : mEvents) {
            kkk=sdf.format(event.getmDate());
                if (kkk.equals(currentD)){
                    Rect rect = getTimeBound(event);
                    EventView eventView = getDecoration().getEventView(event, rect, mTimeHeight, mSeparateHourHeight);
                    if (eventView != null) {
                        mLayoutEvent.addView(eventView, eventView.getLayoutParams());
                    }
                }

        }
        }
    }

    private Rect getTimeBound(Event event) {
        Rect rect = new Rect();
        rect.top = getPositionOfTime(event.getStartTime()) + mTimeHeight + mSeparateHourHeight;
        rect.bottom = getPositionOfTime(event.getEndTime()) + mTimeHeight + mSeparateHourHeight;
        rect.left = mHourWidth + mEventMarginLeft;
        rect.right = getWidth();
        return rect;
    }

    private int getPositionOfTime(Calendar calendar) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY) - mStartHour;
        int minute = calendar.get(Calendar.MINUTE);
        return hour * mDayHeight + minute * mDayHeight / 60;
    }

    public void setEvents(List<Event> events,String currentD) {
        this.mEvents = events;
        refresh(currentD,"setEvent");
    }

    public void setLimitTime(int startHour, int endHour) {
        if (startHour >= endHour) {
            throw new IllegalArgumentException("start hour must be before end hour");
        }
        mStartHour = startHour;
        mEndHour = endHour;
        refresh(null,"setLimitTime");
    }

    /**
     * @param decorator decoration for draw event, popup, time
     */
    public void setDecorator(@NonNull CdvDecoration decorator) {
        this.mDecoration = decorator;
        refresh(null,"setDecorator");
    }

    public CdvDecoration getDecoration() {
        return mDecoration;
    }
}

