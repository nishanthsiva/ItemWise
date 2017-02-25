package com.wolfsoft.teammeetingschedule;

import java.util.Date;

/**
 * Created by nishanth on 2/25/17.
 */

public class CalendarEvent {

    private String eventName;
    private Date startDate;
    private Date endDate;
    private String eventLocation;
    private String eventStatus;

    public CalendarEvent(String eventName, Date startDate, Date endDate, String eventLocation, String eventStatus){
        this.eventName = eventName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.eventLocation = eventLocation;
        this.eventStatus = eventStatus;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(String eventStatus) {
        this.eventStatus = eventStatus;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
