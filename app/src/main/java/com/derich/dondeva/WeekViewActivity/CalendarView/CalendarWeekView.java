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
import com.derich.dondeva.WeekViewActivity.CalendarView.Decorations.CwVDecoration;
import com.derich.dondeva.WeekViewActivity.CalendarView.Decorations.CwVDecorationDefault;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class CalendarWeekView extends FrameLayout {

    private int mDayHeight = 0;

    private int mEventMarginLeft = 0;

    private int mHourWidth = 120;

    private int mTimeHeight = 120;

    private int mSeparateHourHeight = 0;

    private int mSeparateHourWidth = 0;

    private int mStartHour = 0;

    private int mEndHour = 23;

    private LinearLayout mLayoutDayView;

    private FrameLayout mLayoutEvent;

    private CwVDecoration mDecoration;

    private List<Event> mEvents;


    public CalendarWeekView(Context context) {
        super(context);
        init(null);
    }

    public CalendarWeekView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CalendarWeekView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.view_day_calendar, this, true);

        mLayoutDayView = (LinearLayout) findViewById(R.id.dayview_container);
        mLayoutEvent = (FrameLayout) findViewById(R.id.event_container);

        mDayHeight = getResources().getDimensionPixelSize(R.dimen.weekHeight);

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
        mDecoration = new CwVDecorationDefault(getContext());

//        refresh(null);
    }

    public void refresh(String d) {
        drawDayViews();
        drawEvents(d);
    }

    private void drawDayViews() {
        mLayoutDayView.removeAllViews();
        WeekView weekView = null;
        for (int i = mStartHour; i <= mEndHour; i++) {
            weekView = getDecoration().getDayView(i);
            mLayoutDayView.addView(weekView);
        }
        mHourWidth = (int) weekView.getHourTextWidth();
        mTimeHeight = mDayHeight;
        mSeparateHourHeight = (int) weekView.getSeparateHeight();
        mSeparateHourWidth = (int) weekView.getSeparateWidth() - (mHourWidth + mEventMarginLeft);
    }
//    private void drawEvents() {
//        mLayoutEvent.removeAllViews();
//
//        for (Event event : mEvents) {
//            Rect rect = getTimeBound(event);
//            EventWeekView eventWeekView = getDecoration().getEventView(event, rect, mTimeHeight, mSeparateHourHeight, mSeparateHourWidth);
//            if (eventWeekView != null) {
//                mLayoutEvent.addView(eventWeekView, eventWeekView.getLayoutParams());
//            }
//        }
//    }
    private void drawEvents(String currentD) {
        if (currentD!=null){
            mLayoutEvent.removeAllViews();
            SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
            String kkk;
            int i,k,initiali;
            try {
                Date dt=sdf.parse(currentD);
                Calendar calendar= Calendar.getInstance();
                calendar.setTime(dt);
                int weekOfYear= calendar.get(Calendar.WEEK_OF_YEAR);
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.WEEK_OF_YEAR, weekOfYear);
                cal.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
                SimpleDateFormat sdfDay=new SimpleDateFormat("dd");
                i=Integer.parseInt(sdfDay.format(cal.getTime()));
                k=i+6;
                initiali=i;
                for (i=k-6;i<k;i++){
                    Calendar cale = Calendar.getInstance();
                    cale.set(Calendar.WEEK_OF_YEAR, weekOfYear);
                    cale.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY + (i-initiali));
                    int day = cale.get(Calendar.DAY_OF_MONTH);
                    int month = cale.get(Calendar.MONTH);
                    int year = cale.get(Calendar.YEAR);
                   String nowDate= day+"/"+String.format("%02d",month+1)+"/"+year;
                    for (Event event : mEvents) {
                        kkk=sdf.format(event.getmDate());
                        if (kkk.equals(nowDate)){
                            Rect rect = getTimeBound(event);
                            EventWeekView eventWeekView = getDecoration().getEventView(event, rect, mTimeHeight, mSeparateHourHeight, mSeparateHourWidth);
                            if (eventWeekView != null) {
                                mLayoutEvent.addView(eventWeekView, eventWeekView.getLayoutParams());
                            }
                        }

                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }
    private Rect getTimeBound(Event event) {
        Rect rect = new Rect();
        rect.top = getPositionOfTime(event.getStartTime()) + mTimeHeight + mSeparateHourHeight;
        rect.bottom = getPositionOfTime(event.getEndTime()) + mTimeHeight + mSeparateHourHeight;
        rect.left = mHourWidth + mEventMarginLeft;
        rect.right = mSeparateHourWidth / 7;
        return rect;
    }

    private int getPositionOfTime(Calendar calendar) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY) - mStartHour;
        int minute = calendar.get(Calendar.MINUTE);
        return hour * mDayHeight + minute * mDayHeight / 60;
    }

    public void setEvents(List<Event> events,String currentD) {
        this.mEvents = events;
        refresh(currentD);
    }

    public void setLimitTime(int startHour, int endHour) {
        if (startHour >= endHour) {
            throw new IllegalArgumentException("start hour must be before end hour");
        }
        mStartHour = startHour;
        mEndHour = endHour;
        refresh(null);
    }

    /**
     * @param decorator decoration for draw event, popup, time
     */
    public void setDecorator(@NonNull CwVDecoration decorator) {
        this.mDecoration = decorator;
//        refresh();
    }

    public CwVDecoration getDecoration() {
        return mDecoration;
    }
}

