package com.derich.dondeva.WeekViewActivity.CalendarView.Decorations;

import android.graphics.Rect;

import com.derich.dondeva.WeekViewActivity.CalendarView.DayView;
import com.derich.dondeva.WeekViewActivity.CalendarView.Event;
import com.derich.dondeva.WeekViewActivity.CalendarView.EventView;


public interface CdvDecoration {

    EventView getEventView(Event event, Rect eventBound, int hourHeight, int seperateHeight);
    DayView getDayView(int hour);
}
