package service;

import java.util.Calendar;
import java.util.List;

import service.model.Conversation;

public class ScheduleManager {
    public ScheduleManager() {}

    /**
     * Invalidates a block of time in the conversation object
     * @param date the time to block out
     * @param conv the conversation from which time will be blocked out
     */
    public void invalidateBlock(Calendar date, Conversation conv) {
        assert false;
    }

    /**
     * Reserves an appointment
     * @param date the date and time of the appointment
     * @param name the name fo the person the appt is for
     * @param purpose the reason for the appointment
     */
    public void scheduleAppt(Calendar date, String name, String purpose) {
        assert false;
    }

    /**
     * unschedules an appointment for a person
     * @param date the date and time of the appointment to remove
     */
    public void removeAppt(Calendar date) {
        assert false;
    }

    /**
     * Query the appointment table for the given person's appointment
     * @param name the name of the person to get the appt for
     * @return the date and time of the person's appointment
     */
    public Calendar getApptFor(String name) {
        assert false;
        return null;
    }

    /**
     * Get apointment availibilities on the given date
     * Calls the getAvailabilities with exceptions=null
     * 
     * @param date the date for which to query availabilities
     * @return a list of all available slots
     */
    public List<Calendar> getAvailibilities(Calendar date) {
        return getAvailabilities(date, null);
    }

    /**
     * Get apointment availibilities on the given date.
     * Gets the list of all appts, then if exceptions is not null,
     * it filters the raw list so as not to include any values from the exceptions
     * 
     * @param date the date for which to query availabilities
     * @param exceptions a list of time which definitely won't work.
     * @return a list of all acceptable and available slots
     */
    public List<Calendar> getAvailabilities(Calendar date, List<Calendar> exceptions) {
        assert false;
        return null;
    }

    
}