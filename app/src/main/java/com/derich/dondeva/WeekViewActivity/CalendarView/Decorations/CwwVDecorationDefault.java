package com.derich.dondeva.WeekViewActivity.CalendarView.Decorations;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;

import com.derich.dondeva.WeekViewActivity.CalendarView.Event;
import com.derich.dondeva.WeekViewActivity.CalendarView.EventWorkWeekView;
import com.derich.dondeva.WeekViewActivity.CalendarView.WorkWeekView;

import java.text.SimpleDateFormat;
import java.util.Date;


public class CwwVDecorationDefault implements CwwVDecoration {

    protected Context mContext;

    public CwwVDecorationDefault(Context context) {
        this.mContext = context;
    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");

    @Override
    public EventWorkWeekView getEventView(Event event, Rect eventBound, int hourHeight, int separateHeight, int separateWidth) {
        EventWorkWeekView eventWeekView = new EventWorkWeekView(mContext);
        eventWeekView.setEvent(event);
        eventWeekView.setEventText(event.getName());
//        event.setName(jobListItem.job_id);
//        eventWeekView.setEventText(jobListItem.job_id);
//        Date d = null;
//        try {
//            d = sdf.parse(jobListItem.schedule_dt);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        Date d = event.getStartTime().getTime();
        String goal = outFormat.format(d);
        Log.e("TAG", goal);
        int a = 0, b = 4, s = 0;
        if(goal.equals("Monday")){
            a = 0; b = 4;
        }else if(goal.equals("Tuesday")){
            a = 1; b = 3;
        }else if(goal.equals("Wednesday")){
            a = 2; b = 2;
        }else if(goal.equals("Thursday")){
            a = 3; b = 1;
        }else if(goal.equals("Friday")){
            a = 4; b = 0;
        }
        eventWeekView.setPosition(eventBound, -hourHeight, hourHeight - separateHeight, s, a, b);
        event.setScrollValue(eventBound.top + (-hourHeight) - 280);
        return eventWeekView;
    }
    @Override
    public WorkWeekView getDayView(int hour) {
        WorkWeekView weekView = new WorkWeekView(mContext);
        weekView.setText(String.format("%1$2s:00", hour));
        return weekView;
    }

}
