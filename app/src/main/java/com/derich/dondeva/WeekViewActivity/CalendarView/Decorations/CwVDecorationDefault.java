package com.derich.dondeva.WeekViewActivity.CalendarView.Decorations;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.widget.Toast;

import com.derich.dondeva.WeekViewActivity.CalendarView.Event;
import com.derich.dondeva.WeekViewActivity.CalendarView.EventWeekView;
import com.derich.dondeva.WeekViewActivity.CalendarView.WeekView;

import java.text.SimpleDateFormat;
import java.util.Date;


public class CwVDecorationDefault implements CwVDecoration {

    protected Context mContext;

    public CwVDecorationDefault(Context context) {
        this.mContext = context;
    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");

    @Override
    public EventWeekView getEventView(Event event, Rect eventBound, int hourHeight, int separateHeight, int separateWidth) {
        EventWeekView eventWeekView = new EventWeekView(mContext);
        eventWeekView.setEvent(event);
        eventWeekView.setEventText(event.getName());
//        eventWeekView.setEventText(jobListItem.job_id);
/*        Date d = null;
        try {
            d = sdf.parse(jobListItem.schedule_dt);
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        Date d = event.getmDate();
        String goal = outFormat.format(d);
        Log.e("TAG", goal);
        int a = 0, b = 6, s=0;
        if(goal.equals("Monday")){
            a = 0; b = 6; s = 0;
        }else if(goal.equals("Tuesday")){
            a = 1; b = 5; s = 8;
        }else if(goal.equals("Wednesday")){
            a = 2; b = 4; s = 8;
        }else if(goal.equals("Thursday")){
            a = 3; b = 3; s = 8;
        }else if(goal.equals("Friday")){
            a = 4; b = 2; s = 12;
        }else if(goal.equals("Saturday")){
            a = 5; b = 1; s = 12;
        }else if(goal.equals("Sunday")){
            a = 6; b = 0; s = 14;
        }
        eventWeekView.setPosition(eventBound, -hourHeight, hourHeight - separateHeight, s, a, b);
        event.setScrollValue(eventBound.top + (-hourHeight) - 280);
        return eventWeekView;
    }
    @Override
    public WeekView getDayView(int hour) {
        WeekView weekView = new WeekView(mContext);
        weekView.setText(String.format("%1$2s:00", hour));
        return weekView;
    }

}
