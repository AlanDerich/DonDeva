package com.derich.dondeva.WeekViewActivity.CalendarView;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.derich.dondeva.R;


public class EventView extends FrameLayout {

    protected Event mEvent;

    protected TextView mEventName;

    public EventView(Context context) {
        super(context);
        init(null);
    }

    public EventView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public EventView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    protected void init(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.view_event, this, true);

        mEventName = (TextView) findViewById(R.id.item_event_name);

    }

    public void setEventText(String text){
        mEventName.setText(text);
    }

    public void setEvent(Event event) {
        this.mEvent = event;
    }

    public void setPosition(Rect rect, int topMargin, int bottomMargin){
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = rect.top + topMargin - getResources().getDimensionPixelSize(R.dimen.dayHeight);
        params.height = rect.height() + bottomMargin;
        params.leftMargin = rect.left;
        setLayoutParams(params);
    }

}
