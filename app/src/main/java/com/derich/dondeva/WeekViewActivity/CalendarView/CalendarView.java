package com.derich.dondeva.WeekViewActivity.CalendarView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.derich.dondeva.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

/**
 * Created by NeHa on 15/10/2019.
 */
public class CalendarView extends LinearLayout {
    private static final String LOGTAG = "Calendar View";

    private static final int DAYS_COUNT = 42;

    private static final String DATE_FORMAT = "MMM yyyy";

    private String dateFormat;

    private Context context;

    private Calendar currentDate = Calendar.getInstance();

    private LinearLayout header;
    private ImageView btnPrev;
    private ImageView btnNext;
    //private TextView txtDate;
    private GridView grid;
    private GridView weekCalendarGrid;

    private LinearLayout linearDayView;

    private TextView displayMonth;

    private TextView displayYear;

    private int monthNo , yearNo;

    Date currentSelectedDate = null;
    Date prevSelectedDate = null;
    View currentDateView;
    View prevDateView;

    private TextView mondayView;
    private TextView tuesdayView;
    private TextView wednesdayView;
    private TextView thursdayView;
    private TextView fridayView;
    private TextView saturdayView;
    private TextView sundayView;

    private TextView mondayViewH;
    private TextView tuesdayViewH;
    private TextView wednesdayViewH;
    private TextView thursdayViewH;
    private TextView fridayViewH;

    boolean DayViewClick = true;
    boolean WeekViewClick =  false;

    private TextView dayViewDate;
    private TextView headerDay;

    ArrayList<Event> events;

    HashSet<Date> eventDays  = new HashSet<Date>();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public CalendarView(Context context) {
        super(context);
    }

    public CalendarView(Context context, AttributeSet attrs){
        super(context, attrs);
        initControl(context, attrs);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        initControl(context, attrs);
    }

    String[] MonthName = new String[] {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October","November", "December"};

    private void initControl(Context context, AttributeSet attrs){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_calendar, this);

        loadDateFormat(attrs);
        assignUiElements();
        assignClickHandlers();
        updateCalendar();
    }

    private void loadDateFormat(AttributeSet attrs){
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CalendarView);

        try
        {
            dateFormat = ta.getString(R.styleable.CalendarView_dateFormat);
            if (dateFormat == null){
                dateFormat = DATE_FORMAT;
            }
        }
        finally
        {
            ta.recycle();
        }
    }

    private void assignUiElements(){
        header = (LinearLayout)findViewById(R.id.calendar_header_month);
        headerDay = (TextView) findViewById(R.id.day_view_cd);
        btnPrev = (ImageView)findViewById(R.id.calendar_prev_button);
        btnNext = (ImageView)findViewById(R.id.calendar_next_button);
        grid = (GridView)findViewById(R.id.calendar_grid);
        weekCalendarGrid =(GridView) findViewById(R.id.calendar_week_grid);

        linearDayView = (LinearLayout) findViewById(R.id.linear_day_view);

        dayViewDate = (TextView) findViewById(R.id.date_view_cd);

        displayMonth =(TextView) findViewById(R.id.calendar_month_display);
        displayYear = (TextView) findViewById(R.id.calendar_year_display);
        mondayView = (TextView) findViewById(R.id.mondayText);
        tuesdayView = (TextView) findViewById(R.id.tuesdayText);
        wednesdayView = (TextView) findViewById(R.id.wednesdayText);
        thursdayView = (TextView) findViewById(R.id.thursdayText);
        fridayView = (TextView) findViewById(R.id.fridayText);
        saturdayView = (TextView) findViewById(R.id.saturdayText);
        sundayView = (TextView) findViewById(R.id.sundayText);

        mondayViewH =(TextView) findViewById(R.id.mondayTextWork);
        tuesdayViewH =(TextView) findViewById(R.id.tuesdayTextWork);
        wednesdayViewH =(TextView) findViewById(R.id.wednesdayTextWork);
        thursdayViewH =(TextView) findViewById(R.id.thursdayTextWork);
        fridayViewH =(TextView) findViewById(R.id.fridayTextWork);

        events = new ArrayList<>();

    }

    public HashSet<Date> getEventDays() {
        return eventDays;
    }

    public void setEventDays(HashSet<Date> eventDays) {
        this.eventDays = eventDays;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void assignClickHandlers(){
        btnNext.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                setButtonNext();
            }
        });

        btnPrev.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                setButtonPrev();
            }
        });

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gridOnClick(parent,view,position);
            }
        });

        weekCalendarGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                weekCalanderGridOnClick(parent,view,position);
            }
        });

        weekCalendarGrid.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return event.getAction() == MotionEvent.ACTION_MOVE;
            }

        });

    }

    public void gridOnClick(AdapterView<?> parent, View view, int position){
        Date d = (Date) parent.getItemAtPosition(position);
        currentDate.setTime(d);
        setDaysC(d, view);
        eventDays = getEventDays();
        updateCalendar(eventDays);
    }

    public void weekCalanderGridOnClick(AdapterView<?> parent, View view, int position){
        Date d = (Date) parent.getItemAtPosition(position);
        currentDate.setTime(d);
        setDaysC(d, view);
        updateCalendar();
    }

    private void visibilityGone(){
        header.setVisibility(GONE);
        grid.setVisibility(GONE);
        linearDayView.setVisibility(GONE);
        headerDay.setVisibility(GONE);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,  55);
        params.setMargins(0, 0, 0 ,0);
        header.setLayoutParams(params);
        weekCalendarGrid.setVisibility(GONE);
    }

    public void setDaysC(Date d, View view){
        eventDays = getEventDays();
        Calendar dSelected = Calendar.getInstance();
        dSelected.setTime(d);
        Date today = new Date();
        Calendar todaySelected = Calendar.getInstance();
        todaySelected.setTime(today);
        int todayDay = todaySelected.get(Calendar.DAY_OF_MONTH);
        int todayMonth = todaySelected.get(Calendar.MONTH);
        int todayYear = todaySelected.get(Calendar.YEAR);

        if (monthNo == dSelected.get(Calendar.MONTH) && yearNo == dSelected.get(Calendar.YEAR)) {
            if (monthNo == todayMonth && dSelected.get(Calendar.DAY_OF_MONTH) == todayDay && yearNo == todayYear) {

            }else {
                if (currentSelectedDate == null){
                    currentSelectedDate = d;
                    currentDateView = view;
                    String dString = sdf.format(currentDate.getTime());
                    Date dD = null;
                    try {
                        dD = sdf.parse(dString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(eventDays.contains(dD)){
                        view.setBackgroundResource(R.drawable.event_date_bg);
                    }else{
                        view.setBackgroundResource(R.drawable.date_selection);
                    }
                }else{
                    prevDateView = currentDateView;
                    prevDateView.setBackgroundResource(0);
                    prevSelectedDate = currentSelectedDate;
                    currentSelectedDate = d;
                    currentDateView = view;
                    String dString = sdf.format(currentDate.getTime());
                    Date dd = null;
                    try {
                        dd = sdf.parse(dString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(eventDays.contains(dd)){
                        view.setBackgroundResource(R.drawable.event_date_bg);
                    }else{
                        view.setBackgroundResource(R.drawable.date_selection);
                    }
                }
            }

        }
    }

    public Date getCurrentDate(){
        return currentDate.getTime();
    }

    public void setCurrentDate(Date d){
        currentDate.setTime(d);
    }
    public void setWeekViewClick(boolean check){
        WeekViewClick = check;
        setDayView();
        updateCalendar();
    }

    public void setDayViewClick(boolean check) {
        DayViewClick = check;
        setDayView();
        updateCalendar();
    }

    public void setDayView(){
        visibilityGone();
        if(DayViewClick){
            linearDayView.setVisibility(VISIBLE);
            headerDay.setVisibility(VISIBLE);
        }else if (WeekViewClick) {
            weekCalendarGrid.setVisibility(VISIBLE);
            weekCalendarGrid.setNumColumns(7);
            header.setVisibility(VISIBLE);
            Resources r = getContext().getResources();
            int px = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                    32,
                        r.getDisplayMetrics()
            );
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,  55);
            params.setMargins(px, 0, 0 ,0);
            header.setLayoutParams(params);
        }else{
            grid.setVisibility(VISIBLE);
            header.setVisibility(VISIBLE);
        }
    }

    public void updateCalendar() {
        HashSet<Date> events = new HashSet<Date>();
        updateCalendar(events);
    }

    /**
     * Display dates correctly in grid
     */
    public void updateCalendar(HashSet<Date> events){
        ArrayList<Date> cells = new ArrayList<>();
        Calendar calendar = (Calendar)currentDate.clone();
        int day = currentDate.get(Calendar.DAY_OF_MONTH);
        // determine the cell for current month's beginning

        if(WeekViewClick ){
            calendar.set(Calendar.DAY_OF_WEEK, 1);
        }else{
            calendar.set(Calendar.DAY_OF_MONTH, 1);
        }
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        int monthBeginningCell = (calendar.get(Calendar.DAY_OF_WEEK) - calendar.getFirstDayOfWeek()) < 0 ? 7 +
                (calendar.get(Calendar.DAY_OF_WEEK) - calendar.getFirstDayOfWeek()): (calendar.get(Calendar.DAY_OF_WEEK) - calendar.getFirstDayOfWeek());

        Log.e("DAY Of WEEK ", ""+ monthBeginningCell);

        // move calendar backwards to the beginning of the week
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);
        int Days;
        if(WeekViewClick ){
            Days = 7;
        }else{
            Days = DAYS_COUNT;
        }
        // fill cells
        while (cells.size() < Days)
        {
            cells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // update grid
        grid.setAdapter(new CalendarAdapter(getContext(), cells, events));
        weekCalendarGrid.setAdapter(new CalendarAdapter(getContext(), cells, events));


        int month = currentDate.get(Calendar.MONTH);
        int year = currentDate.get(Calendar.YEAR);

        int weekDate =  currentDate.get(Calendar.WEEK_OF_YEAR);

        monthNo = month;
        yearNo = year;
        //int season = monthSeason[month];
        //int color = season;
        String monthName= MonthName[month];
        displayMonth.setText(monthName);
        displayYear.setText(String.valueOf(year));
        Date today = new Date();
        SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
        String goal = outFormat.format(today);
        Log.e("DAY : ",goal);
        setDayColorDull(goal);

        int tDate =  currentDate.get(Calendar.DAY_OF_MONTH);
        Calendar todaySelected = Calendar.getInstance();
        int todayMonth = todaySelected.get(Calendar.MONTH);
        int todayYear = todaySelected.get(Calendar.YEAR);
        int todayDay = todaySelected.get(Calendar.DAY_OF_MONTH);
        int todayWeekDate = todaySelected.get(Calendar.WEEK_OF_YEAR);
        //header.setBackgroundColor(getResources().getColor(color));
        Log.e("TAG", ""+todayWeekDate);
        Log.e("TAG", ""+weekDate);

        if(WeekViewClick){
            if (weekDate == todayWeekDate ){setDayColor(goal);}
        }else{
            setDayColorDull(goal);
            if (monthNo == todayMonth && yearNo == todayYear){setDayColor(goal);}
        }

        if (DayViewClick){
            CalendarDayView cdv=new CalendarDayView(getContext());
            cdv.refresh((tDate)+ "/"+String.format("%02d",month+1)+ "/"+year,"Calendarview");

        }
        else if(WeekViewClick){
            CalendarWeekView cwv=new CalendarWeekView(getContext());
            cwv.refresh((tDate)+ "/"+String.format("%02d",month+1)+ "/"+year);
        }

//		currentDate.Now.StartOfWeek(DayOfWeek.Monday);



        SimpleDateFormat outFormat1 = new SimpleDateFormat("EEEE");
        String st = outFormat1.format(currentDate.getTime());
        headerDay.setText(st);
        headerDay.setTextColor(Color.parseColor("#D97904"));
        headerDay.setBackgroundColor(Color.parseColor("#ffeeef"));
        dayViewDate.setText(String.valueOf(currentDate.get(Calendar.DAY_OF_MONTH)));

        if (monthNo == todayMonth && day == todayDay && yearNo == todayYear){
            dayViewDate.setBackgroundResource(R.drawable.today_date);
            headerDay.setTextColor(Color.parseColor("#ffffff"));
            headerDay.setBackgroundColor(Color.parseColor("#D97904"));
        }else{
            dayViewDate.setBackgroundResource(R.drawable.day_bg);
        }

    }

    public void setButtonNext(){
        btnNext.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_right_white_24dp));
        btnPrev.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_left_grey_24dp));

		if(DayViewClick){
			currentDate.add(Calendar.DAY_OF_MONTH, 1);
			updateCalendar();
		}else if(WeekViewClick){
			currentDate.add(Calendar.WEEK_OF_YEAR, 1 );
			updateCalendar();
		}else{
			currentDate.add(Calendar.MONTH, 1);
			updateCalendar();
		}

    }public Calendar getDated(){
        return currentDate;
    }

    public void setButtonPrev(){
        btnNext.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_right_grey_24dp));
        btnPrev.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_left_white_24dp));

		if(DayViewClick){
			currentDate.add(Calendar.DAY_OF_MONTH, -1);
			updateCalendar();
		}else if(WeekViewClick){
			currentDate.add(Calendar.WEEK_OF_YEAR, -1 );
			updateCalendar();
		}else{
			currentDate.add(Calendar.MONTH, -1);
			updateCalendar();
		}
    }


    public class CalendarAdapter extends ArrayAdapter<Date> {
        // days with events
        private HashSet<Date> eventDays;

        // for view inflation
        private LayoutInflater inflater;


        CalendarAdapter(Context context, ArrayList<Date> days, HashSet<Date> eventDays)
        {
            super(context, R.layout.control_calendar_day, days);
            this.eventDays = eventDays;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent)
        {
            Date date = getItem(position);
            Calendar calSelected = Calendar.getInstance();
            calSelected.setTime(date);


            int day = calSelected.get(Calendar.DAY_OF_MONTH);
            int month = calSelected.get(Calendar.MONTH);
            int year = calSelected.get(Calendar.YEAR);

            // today
            Date today = new Date();
            Calendar todaySelected = Calendar.getInstance();
            todaySelected.setTime(today);
            int todayDay = todaySelected.get(Calendar.DAY_OF_MONTH);
            int todayMonth = todaySelected.get(Calendar.MONTH);
            int todayYear = todaySelected.get(Calendar.YEAR);


            // inflate item if it does not exist yet
            if (view == null){
                view = inflater.inflate(R.layout.control_calendar_day, parent, false);
            }

            view.setBackgroundResource(0);
            // clear styling
            ((TextView)view).setTypeface(null, Typeface.NORMAL);
            ((TextView)view).setTextColor(Color.BLACK);

            Calendar c = (Calendar) currentDate.clone();



            if (monthNo != month || yearNo != year)
            {
                if(WeekViewClick ){
                    if (month == todayMonth && day == todayDay && year == todayYear){
                        view.setBackgroundResource(R.drawable.today_date);
                    }
                }else{

                    ((TextView)view).setTextColor(getResources().getColor(R.color.grey_text));

                    if (month == todayMonth && day == todayDay && year == todayYear){
                        view.setBackgroundResource(R.drawable.dull_today_date);
                    }

                }

            }else if (month == todayMonth && day == todayDay && year == todayYear){
                view.setBackgroundResource(R.drawable.today_date);
            }
            Log.e("Days ddf", "fsjfksdfjsk" + eventDays.contains(currentDate.getTime()));

            if(day == c.get(Calendar.DAY_OF_MONTH) && month == c.get(Calendar.MONTH) && year == c.get(Calendar.YEAR)){
                if (month == todayMonth && day == todayDay && year == todayYear){
                    view.setBackgroundResource(R.drawable.today_date);
                }else{
                    if(eventDays.contains(currentDate.getTime())){
                        view.setBackgroundResource(R.drawable.event_select_date_bg);
                    }else{
                        view.setBackgroundResource(R.drawable.date_selection);
                    }
                }
            }

            if(!WeekViewClick ){
                if (eventDays != null)
                {
                    for (Date eventDate : eventDays)
                    {
                        Calendar eventSelected = Calendar.getInstance();
                        eventSelected.setTime(eventDate);
                        if (eventSelected.get(Calendar.DAY_OF_MONTH) == day &&
                                eventSelected.get(Calendar.MONTH) == month &&
                                eventSelected.get(Calendar.YEAR) == year){
                            // mark this day for event
                            view.setBackgroundResource(R.drawable.event_date_bg);
                            if (c.get(Calendar.DAY_OF_MONTH) == day &&
                                    c.get(Calendar.MONTH) == month &&
                                    c.get(Calendar.YEAR) == year){
                                view.setBackgroundResource(R.drawable.event_select_date_bg);
                            }
                            if (month == todayMonth && day == todayDay && year == todayYear){
                                view.setBackgroundResource(R.drawable.today_date);
                            }
                        }
                    }
                }
            }
            ((TextView)view).setText(String.valueOf(day));
            return view;
        }
    }

    public void setDayColorDull(String day){
        if(day.equals("Monday")){
            mondayView.setTextColor(Color.parseColor("#D97904"));
            mondayView.setBackgroundColor(0);
            mondayViewH.setTextColor(Color.parseColor("#D97904"));
            mondayViewH.setBackgroundColor(0);
        }else if(day.equals("Tuesday")){
            tuesdayView.setTextColor(Color.parseColor("#D97904"));
            tuesdayView.setBackgroundColor(0);
            tuesdayViewH.setTextColor(Color.parseColor("#D97904"));
            tuesdayViewH.setBackgroundColor(0);
        }else if(day.equals("Wednesday")){
            wednesdayView.setTextColor(Color.parseColor("#D97904"));
            wednesdayView.setBackgroundColor(0);
            wednesdayViewH.setTextColor(Color.parseColor("#D97904"));
            wednesdayViewH.setBackgroundColor(0);
        }else if(day.equals("Thursday")){
            thursdayView.setTextColor(Color.parseColor("#D97904"));
            thursdayView.setBackgroundColor(0);
            thursdayViewH.setTextColor(Color.parseColor("#D97904"));
            thursdayViewH.setBackgroundColor(0);
        }else if(day.equals("Friday")){
            fridayView.setTextColor(Color.parseColor("#D97904"));
            fridayView.setBackgroundColor(0);
            fridayViewH.setTextColor(Color.parseColor("#D97904"));
            fridayViewH.setBackgroundColor(0);
        }else if(day.equals("Saturday")){
            saturdayView.setTextColor(Color.parseColor("#D97904"));
            saturdayView.setBackgroundColor(0);
        }else if(day.equals("Sunday")){
            sundayView.setTextColor(Color.parseColor("#D97904"));
            sundayView.setBackgroundColor(0);
        }
    }

    public void setDayColor(String day){
        if(day.equals("Monday")){
            mondayView.setTextColor(Color.parseColor("#ffffff"));
            mondayView.setBackgroundColor(Color.parseColor("#D97904"));
            mondayViewH.setTextColor(Color.parseColor("#ffffff"));
            mondayViewH.setBackgroundColor(Color.parseColor("#D97904"));
        }else if(day.equals("Tuesday")){
            tuesdayView.setTextColor(Color.parseColor("#ffffff"));
            tuesdayView.setBackgroundColor(Color.parseColor("#D97904"));
            tuesdayViewH.setTextColor(Color.parseColor("#ffffff"));
            tuesdayViewH.setBackgroundColor(Color.parseColor("#D97904"));
        }else if(day.equals("Wednesday")){
            wednesdayView.setTextColor(Color.parseColor("#ffffff"));
            wednesdayView.setBackgroundColor(Color.parseColor("#D97904"));
            wednesdayViewH.setTextColor(Color.parseColor("#ffffff"));
            wednesdayViewH.setBackgroundColor(Color.parseColor("#D97904"));
        }else if(day.equals("Thursday")){
            thursdayView.setTextColor(Color.parseColor("#ffffff"));
            thursdayView.setBackgroundColor(Color.parseColor("#D97904"));
            thursdayViewH.setTextColor(Color.parseColor("#ffffff"));
            thursdayViewH.setBackgroundColor(Color.parseColor("#D97904"));
        }else if(day.equals("Friday")){
            fridayView.setTextColor(Color.parseColor("#ffffff"));
            fridayView.setBackgroundColor(Color.parseColor("#D97904"));
            fridayViewH.setTextColor(Color.parseColor("#ffffff"));
            fridayViewH.setBackgroundColor(Color.parseColor("#D97904"));
        }else if(day.equals("Saturday")){
            saturdayView.setTextColor(Color.parseColor("#ffffff"));
            saturdayView.setBackgroundColor(Color.parseColor("#D97904"));
        }else if(day.equals("Sunday")){
            sundayView.setTextColor(Color.parseColor("#ffffff"));
            sundayView.setBackgroundColor(Color.parseColor("#D97904"));
        }
    }





}
