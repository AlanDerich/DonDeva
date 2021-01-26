package com.derich.dondeva.WeekViewActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.derich.dondeva.MainActivity;
import com.derich.dondeva.R;
import com.derich.dondeva.RequestDetails;
import com.derich.dondeva.UserDetails;
import com.derich.dondeva.WeekViewActivity.CalendarView.CalendarDayView;
import com.derich.dondeva.WeekViewActivity.CalendarView.CalendarView;
import com.derich.dondeva.WeekViewActivity.CalendarView.CalendarWeekView;
import com.derich.dondeva.WeekViewActivity.CalendarView.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class BasicActivity extends AppCompatActivity {
    private ListView jobList;
    private CalendarView cv;
    private ArrayList<Event> events;
    private LinearLayout dayView;
    private LinearLayout weekView;
    private LinearLayout monthView;
    private ImageView dayViewImage;
    private ImageView weekViewImage;
    private ImageView monthViewImage;

    private TextView dayViewText;
    private TextView weekViewText;
    private TextView monthViewText;
    private boolean DayViewClick = false;
    private boolean WeekViewClick =  false;
    private LinearLayout dayViewContainer;
    private LinearLayout weekViewContainer;

    private CalendarDayView calenderDayView;

    private ScrollView scrollView;

    private Calendar currentDate = Calendar.getInstance();
    private ScrollView weekScrollView;
    private List<RequestDetails> mAllOrders;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private List<UserDetails> mUserr;

    private CalendarWeekView calendarWeekView;

    private HashSet<Date> eventDays = new HashSet<>();
    private SimpleDateFormat sdfDay = new SimpleDateFormat("EEEE");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);
        cv = findViewById(R.id.calendarView);
        dayView = findViewById(R.id.day_view);
        weekView = findViewById(R.id.week_view);
        monthView = findViewById(R.id.month_view);
        dayViewImage = findViewById(R.id.day_view_image);
        weekViewImage = findViewById(R.id.week_view_image);
        monthViewImage = findViewById(R.id.month_view_image);
        dayViewText = findViewById(R.id.day_view_text);
        weekViewText = findViewById(R.id.week_view_text);
        monthViewText = findViewById(R.id.month_view_text);
        calenderDayView = findViewById(R.id.calendar_day_view);
        dayViewContainer = findViewById(R.id.dayview_container);
        scrollView = findViewById(R.id.ScrollBar);
        weekViewContainer = findViewById(R.id.week_view_layout);
        weekScrollView = findViewById(R.id.weekScroll);
        calendarWeekView = findViewById(R.id.calendar_week_view);
        fillEvents();
        assignOnClickListener();

    }

    private void fillEvents() {
        eventDays = new HashSet<>();
        Calendar cal = Calendar.getInstance();
//        eventDays.add(cal.getTime());
//        for(int i =0; i<4; i++){
//            cal.add(Calendar.DAY_OF_MONTH, 1);
//            eventDays.add(cal.getTime());
//        }
        events = new ArrayList<>();
        ProgressDialog dialog = ProgressDialog.show(BasicActivity.this, "",
                "Loading requests. Please wait...", true);
        db.collectionGroup("AllRequests").orderBy("date", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mAllOrders = new ArrayList<>();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            mAllOrders.add(snapshot.toObject(RequestDetails.class));
                        }
                    } else {
                        Toast.makeText(BasicActivity.this, "No requests found. All requests will appear here", Toast.LENGTH_LONG).show();
                    }
                    initEvents();
                    dialog.dismiss();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(BasicActivity.this, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                    Log.w("HouseInfo", "error " + e);
                });
//
//        {
//            Calendar timeStart = Calendar.getInstance();
//            timeStart.set(Calendar.HOUR_OF_DAY, 20);
//            timeStart.set(Calendar.MINUTE, 0);
//            Calendar timeEnd = (Calendar) timeStart.clone();
//            timeEnd.set(Calendar.HOUR_OF_DAY, 22);
//            timeEnd.set(Calendar.MINUTE, 0);
//            Event event = new Event( timeStart, timeEnd, "Another event", "Hockaido", 0);
//            events.add(event);
//        }
    }

    private void initEvents() {
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
        Date now=new Date();
        String appDay;
        Date today=null;
        for (RequestDetails requestDetails: mAllOrders){

            String startTime=requestDetails.getStartTime();
            appDay=requestDetails.getDate();
            try {
                today=sdf.parse(appDay);
                eventDays.add(today);
            } catch (ParseException e) {
                e.printStackTrace();
            }
//            if (sdf.format(now).compareTo(sdf.format(today))==0){
            String arr[] = startTime.split(":", 2);
                Calendar timeStart = Calendar.getInstance();
                timeStart.set(Calendar.HOUR_OF_DAY, Integer.parseInt(arr[0]));
                timeStart.set(Calendar.MINUTE, Integer.parseInt(arr[1]));
                Calendar timeEnd = (Calendar) timeStart.clone();
                timeEnd.set(Calendar.HOUR_OF_DAY, Integer.parseInt(arr[0])+2);
                timeEnd.set(Calendar.MINUTE, Integer.parseInt(arr[1]));
                Event event = new Event( today,requestDetails.getUsername(),requestDetails.getPhoneNum(),timeStart, timeEnd, requestDetails.getServiceName(), requestDetails.getPhoneNum(),0);
                events.add(event);
//              }
  }
        cv.updateCalendar(eventDays);
        cv.setEventDays(eventDays);
        Calendar cd=cv.getDated();
        int tDate =  cd.get(Calendar.DAY_OF_MONTH);
        int month = cd.get(Calendar.MONTH);
        int year = cd.get(Calendar.YEAR);
        String currentD= (tDate)+ "/"+String.format("%02d",month+1)+ "/"+year;
        calenderDayView.setEvents(events,currentD);
        calendarWeekView.setEvents(events,currentD);
    }

    public void dateOnClick(){
        Date d = cv.getCurrentDate();
        currentDate.setTime(d);
        cv.setCurrentDate(d);
    }

    private void setNotSelected(){
        dayView.setBackgroundResource(R.drawable.shadow_115330);
        dayViewImage.setImageDrawable(getResources().getDrawable(R.drawable.calendar_day));
        dayViewText.setTextColor(Color.parseColor("#534a4a4a"));

        weekView.setBackgroundResource(R.drawable.shadow_115330);
        weekViewImage.setImageDrawable(getResources().getDrawable(R.drawable.calendar_week));
        weekViewText.setTextColor(Color.parseColor("#534a4a4a"));

        monthView.setBackgroundResource(R.drawable.shadow_115330);
        monthViewImage.setImageDrawable(getResources().getDrawable(R.drawable.calendar_month));
        monthViewText.setTextColor(Color.parseColor("#534a4a4a"));

    }

    public  void visibilityGone(){
        cv.setDayViewClick(false);
        dayViewContainer.setVisibility(View.GONE);
        weekViewContainer.setVisibility(View.GONE);
    }

    public void setClick(){
        cv.setWeekViewClick(false);
        WeekViewClick = false;
        cv.setDayViewClick(false);
        DayViewClick = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
    }

    public void assignOnClickListener(){

        findViewById(R.id.calendar_prev_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cv.setButtonPrev();
                Date d = cv.getCurrentDate();
                currentDate.setTime(d);
                cv.setCurrentDate(d);
                if(DayViewClick){
                    cv.updateCalendar(eventDays);
                    Calendar cd=cv.getDated();
                    int tDate =  cd.get(Calendar.DAY_OF_MONTH);
                    int month = cd.get(Calendar.MONTH);
                    int year = cd.get(Calendar.YEAR);
                    calenderDayView.setEvents(events,(tDate)+ "/"+String.format("%02d",month+1)+ "/"+year);
                    /*GetContainerListDay getContainerListDay = new GetContainerListDay();
                    getContainerListDay.execute();*/
                }else if(WeekViewClick){
                    cv.updateCalendar(eventDays);
                    Calendar cd=cv.getDated();
                    int tDate =  cd.get(Calendar.DAY_OF_MONTH);
                    int month = cd.get(Calendar.MONTH);
                    int year = cd.get(Calendar.YEAR);
                    calendarWeekView.setEvents(events,(tDate)+ "/"+String.format("%02d",month+1)+ "/"+year);
                    /*GetContainerListWeek getContainerListWeek = new GetContainerListWeek();
                    getContainerListWeek.execute();*/
                }else{
                    cv.updateCalendar(eventDays);
                    /*GetContainerListMonth getContainerListMonth = new GetContainerListMonth();
                    getContainerListMonth.execute();*/
                }
                /*GetContainerList getContainerList = new GetContainerList();
                getContainerList.execute();*/
            }
        });

        findViewById(R.id.calendar_next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cv.setButtonNext();
                Date d = cv.getCurrentDate();
                currentDate.setTime(d);
                cv.setCurrentDate(d);
                if(DayViewClick){
                    cv.updateCalendar(eventDays);
                    Calendar cd=cv.getDated();
                    int tDate =  cd.get(Calendar.DAY_OF_MONTH);
                    int month = cd.get(Calendar.MONTH);
                    int year = cd.get(Calendar.YEAR);
                    calenderDayView.setEvents(events,(tDate)+ "/"+String.format("%02d",month+1)+ "/"+year);
                    /*GetContainerListDay getContainerListDay = new GetContainerListDay();
                    getContainerListDay.execute();*/
                }else if(WeekViewClick){
                    cv.updateCalendar(eventDays);
                    Calendar cd=cv.getDated();
                    int tDate =  cd.get(Calendar.DAY_OF_MONTH);
                    int month = cd.get(Calendar.MONTH);
                    int year = cd.get(Calendar.YEAR);
                    calendarWeekView.setEvents(events,(tDate)+ "/"+String.format("%02d",month+1)+ "/"+year);
                    /*GetContainerListWeek getContainerListWeek = new GetContainerListWeek();
                    getContainerListWeek.execute();*/
                }else{
                    cv.updateCalendar(eventDays);
                    /*GetContainerListMonth getContainerListMonth = new GetContainerListMonth();
                    getContainerListMonth.execute();*/
                }
                /*GetContainerList getContainerList = new GetContainerList();
                getContainerList.execute();*/
            }
        });


        findViewById(R.id.calendar_grid);
        GridView gridView = findViewById(R.id.calendar_grid);
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            cv.gridOnClick(parent, view, position);
            /*GetContainerListMonth getContainerListMonth = new GetContainerListMonth();
            getContainerListMonth.execute();*/
            cv.updateCalendar(eventDays);
            dateOnClick();
        });
        GridView gridView1 = findViewById(R.id.calendar_week_grid);
        gridView1.setOnItemClickListener((parent, view, position, id) -> {
            cv.weekCalanderGridOnClick(parent, view, position);
            dateOnClick();
        });



        dayView.setOnClickListener(v -> {
            setNotSelected();
            dayView.setBackgroundResource(R.drawable.shadow_17855);
            dayViewImage.setImageDrawable(getResources().getDrawable(R.drawable.calendar_day_selected));
            dayViewText.setTextColor(Color.parseColor("#bb1f2c"));
            visibilityGone();
            setClick();
            dayViewContainer.setVisibility(View.VISIBLE);
            DayViewClick = true;
            cv.setDayViewClick(true);
            Date d = cv.getCurrentDate();
            currentDate.setTime(d);
            cv.updateCalendar();
            cv.updateCalendar(eventDays);
            Calendar cd=cv.getDated();
            int tDate =  cd.get(Calendar.DAY_OF_MONTH);
            int month = cd.get(Calendar.MONTH);
            int year = cd.get(Calendar.YEAR);
            calenderDayView.setEvents(events,(tDate)+ "/"+String.format("%02d",month+1)+ "/"+year);
            /*GetContainerListDay getContainerListDay = new GetContainerListDay();
            getContainerListDay.execute();*/
        });

        weekView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNotSelected();
                weekView.setBackgroundResource(R.drawable.shadow_17855);
                weekViewImage.setImageDrawable(getResources().getDrawable(R.drawable.calendar_week_selected));
                weekViewText.setTextColor(Color.parseColor("#bb1f2c"));
                visibilityGone();
                setClick();
                weekViewContainer.setVisibility(View.VISIBLE);
                WeekViewClick = true;
                cv.setWeekViewClick(true);
                Date d = cv.getCurrentDate();
                currentDate.setTime(d);
                cv.updateCalendar();
                cv.updateCalendar(eventDays);
                Calendar cd=cv.getDated();
                int tDate =  cd.get(Calendar.DAY_OF_MONTH);
                int month = cd.get(Calendar.MONTH);
                int year = cd.get(Calendar.YEAR);
                calendarWeekView.setEvents(events,(tDate)+ "/"+String.format("%02d",month+1)+ "/"+year);
                /*GetContainerListWeek getContainerListWeek = new GetContainerListWeek();
                getContainerListWeek.execute();
                GetContainerList getContainerList = new GetContainerList();
                getContainerList.execute();*/

            }
        });


        monthView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNotSelected();
                monthView.setBackgroundResource(R.drawable.shadow_17855);
                monthViewImage.setImageDrawable(getResources().getDrawable(R.drawable.calendar_month_selected));
                monthViewText.setTextColor(Color.parseColor("#bb1f2c"));
                visibilityGone();
                setClick();
                cv.updateCalendar();
                cv.updateCalendar(eventDays);
                /*GetContainerList getContainerList = new GetContainerList();
                getContainerList.execute();
                GetContainerListMonth getContainerListMonth = new GetContainerListMonth();
                getContainerListMonth.execute();*/
            }
        });
    }
}
