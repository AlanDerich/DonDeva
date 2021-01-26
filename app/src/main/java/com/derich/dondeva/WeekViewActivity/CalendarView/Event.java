package com.derich.dondeva.WeekViewActivity.CalendarView;

import java.util.Calendar;
import java.util.Date;


public class Event{
    private Date mDate;
    private Calendar mStartTime;
    private Calendar mEndTime;
    private String ID,userName,phoneNum;
    private String mLocation;
    private int scrollValue = 0;

    public Event(Date mDate,String userName,String phoneNum,Calendar mStartTime, Calendar mEndTime, String ID, String mLocation, int scrollValue){
        this.mDate=mDate;
        this.userName=userName;
        this.phoneNum=phoneNum;
        this.mStartTime = mStartTime;
        this.mEndTime = mEndTime;
        this.ID = ID;
        this.mLocation = mLocation;
        this.scrollValue = scrollValue;
    }

    public Date getmDate() {
        return mDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public void setmDate(Date mDate) {
        this.mDate = mDate;
    }

    public int getScrollValue() {
        return scrollValue;
    }

    public void setScrollValue(int scrollValue) {
        this.scrollValue = scrollValue;
    }

    public Calendar getStartTime() {
        return mStartTime;
    }

    public void setStartTime(Calendar startTime) {
        this.mStartTime = startTime;
    }

    public Calendar getEndTime() {
        return mEndTime;
    }

    public void setEndTime(Calendar endTime) {
        this.mEndTime = endTime;
    }

    public String getName() {
        return ID;
    }

    public void setName(String name) {
        this.ID = name;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        this.mLocation = location;
    }

}
