package com.derich.dondeva.WeekViewActivity.CalendarView.Decorations;

import android.graphics.Rect;

import com.derich.dondeva.WeekViewActivity.CalendarView.Event;
import com.derich.dondeva.WeekViewActivity.CalendarView.EventWorkWeekView;
import com.derich.dondeva.WeekViewActivity.CalendarView.WorkWeekView;


public interface CwwVDecoration {

    EventWorkWeekView getEventView(Event event, Rect eventBound, int hourHeight, int seperateHeight, int seprateWidth);
    WorkWeekView getDayView(int hour);
}
