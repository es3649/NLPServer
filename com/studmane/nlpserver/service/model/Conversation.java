package com.studmane.nlpserver.service.model;

import java.util.Calendar;
import java.util.List;
// import java.util.Map;
// import java.util.TreeMap;
import java.util.UUID;

/**
 * The Conversation class defines an information template which will be used to persistently store
 * data bout a conversation
 */
public class Conversation {
    public Conversation(String _name) {
        this.name = _name;
        this.ID = UUID.randomUUID().toString();
        this.state = Conversation.STATE_PROCESSING;
    }

    private String ID;
    private String name;
    private String purpose = "Interview";
    private List<Calendar> unavailable;
    // the time when the appointment will happen, 
    // if this is filled and the state = PROCESSING_STATE, then it is a proposed appt date
    private Calendar appointmentTime;
    private Calendar timeAppointmentSet;    // the time when the appointment was set by the system
    private String state;

    public static final String STATE_ARCHIVED = "Archived";
    public static final String STATE_SET = "Appt set";
    public static final String STATE_APPT_PROPOSED = "Appt proposed";
    public static final String STATE_PROCESSING = "Processing";
    public static final String STATE_RESCHEDULING = "Rescheduling";

    // public static final String DIFF_STATE = "StateChanges";
    // public static final String DIFF_UNAVAIL = "UnavailabilityChanges";
    // public static final String DIFF_APPT = "AppointmentChanged";


    /**
     * Checks that the Conversation object is "valid." There are particular fields that should not be null
     * @return the validity of this object
     */
    public boolean isValid() {
        return (ID != null
            && name != null
            && purpose != null
            && state != null);
    }

    /**
     * Determines what has changed between two conversations
     * @param that the other conversation, assumed to be newer
     * @return a ConversationDifference object describing the differences
     */
    // public Map<String,Object> diff(Conversation that) {
    //     Map<String, Object> result = new TreeMap<>();

    //     // did the state change?
    //     if (that.state != this.state) {
    //         result.put(DIFF_STATE, true);
    //     }

    //     // did the number of unavailabilities change?
    //     if (that.unavailable == null) {
    //         // the new is null (No)
    //         result.put(DIFF_UNAVAIL, 0);
    //     } else if (this.unavailable == null) {
    //         // the new is not null, but the old is (Yes)
    //         result.put(DIFF_UNAVAIL, that.unavailable.size());
    //     } else {
    //         // both not null (Maybe -> calculate)
    //         result.put(DIFF_UNAVAIL, that.unavailable.size() - this.unavailable.size());
    //     }

    //     // did the proposed appt time change?
    //     if (that.appointmentTime == null) {
    //         // new is null (No)
    //         result.put(DIFF_APPT, false);
    //     } else if (this.appointmentTime == null) {
    //         // new is not null but old is (Yes)
    //         result.put(DIFF_APPT, true);
    //     } else {
    //         // both not null (Maybe, check)
    //         result.put(DIFF_APPT, !this.appointmentTime.equals(that.appointmentTime));
    //     }

    //     // did the purpose change?
    //     // I don't think we really care

    //     return result;
    // }

    

    /**
     * Clones the object
     * @return a copy of this conversation object
     */
    // @Override
    // public Conversation clone() {
    //     return (Conversation)this.clone();
    // }

    ///////////////// Getters and Setters /////////////////
    /**
     * @return the appointmentTime
     */
    public Calendar getAppointmentTime() {
        return appointmentTime;
    }
    /**
     * @return the iD
     */
    public String getID() {
        return ID;
    }
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @return the purpose
     */
    public String getPurpose() {
        return purpose;
    }
    /**
     * @return the state
     */
    public String getState() {
        return state;
    }
    /**
     * @return the timeAppointmentSet
     */
    public Calendar getTimeAppointmentSet() {
        return timeAppointmentSet;
    }
    /**
     * @return the unavailable
     */
    public List<Calendar> getUnavailable() {
        return unavailable;
    }
    /**
     * @return is the "unavailable" list empty?
     */
    public boolean hasUnavailabilities() {
        return (unavailable != null && !unavailable.isEmpty());
    }

    /**
     * @param appointmentTime the appointmentTime to set
     */
    public void setAppointmentTime(Calendar appointmentTime) {
        this.appointmentTime = appointmentTime;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @param purpose the purpose to set
     */
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
    /**
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
    }
    /**
     * @param timeAppointmentSet the timeAppointmentSet to set
     */
    public void setTimeAppointmentSet(Calendar timeAppointmentSet) {
        this.timeAppointmentSet = timeAppointmentSet;
    }
    /**
     * @param unavailable the unavailable to set
     */
    public void setUnavailable(List<Calendar> unavailable) {
        this.unavailable = unavailable;
    }
}