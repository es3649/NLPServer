package service.model;

/**
 * A Message Intent declares the intent of the message
 */
public enum MessageIntent {
    NEGATIVE, AFFIRMATIVE,
    SCHEDULE, RESCHEDULE,
    CANCEL,
    QUERY_WHEN,
    OTHER
}