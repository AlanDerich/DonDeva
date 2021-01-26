package com.derich.dondeva.WeekViewActivity.CalendarView;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.derich.dondeva.R;


public class EventWorkWeekView extends FrameLayout {

    protected Event mEvent;

    protected TextView mEventName;

    public EventWorkWeekView(Context context) {
        super(context);
        init(null);
    }

    public EventWorkWeekView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public EventWorkWeekView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    protected void init(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.event_view_week, this, true);

        mEventName = (TextView) findViewById(R.id.item_event_name);
    }

    public void setEventText(String text){
        mEventName.setText(text);
    }

    public void setEvent(Event event) {
        this.mEvent = event;
    }

    public void setPosition(Rect rect, int topMargin, int bottomMargin, int s, int a, int b){
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = rect.top + topMargin - getResources().getDimensionPixelSize(R.dimen.weekHeight) ;
        params.height = rect.height() + bottomMargin;
        params.leftMargin = rect.left + ((rect.right * a) - 4  - s);
        params.rightMargin = (rect.right * b) + 4 + s;
        setLayoutParams(params);
    }

}
