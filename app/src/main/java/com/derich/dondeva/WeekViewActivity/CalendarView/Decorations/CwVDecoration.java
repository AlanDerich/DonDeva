package com.derich.dondeva.WeekViewActivity.CalendarView.Decorations;

import android.graphics.Rect;

import com.derich.dondeva.WeekViewActivity.CalendarView.Event;
import com.derich.dondeva.WeekViewActivity.CalendarView.EventWeekView;
import com.derich.dondeva.WeekViewActivity.CalendarView.WeekView;


public interface CwVDecoration {

    EventWeekView getEventView(Event event, Rect eventBound, int hourHeight, int seperateHeight, int seprateWidth);
    WeekView getDayView(int hour);
}
