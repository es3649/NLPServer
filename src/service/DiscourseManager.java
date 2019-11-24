package service;

import service.model.Conversation;
import service.model.MessageIntent;

/**
 * DiscourseManager uses coreNLP annotation information and previously extracted information to generate a response.
 * 
 * Each Conversation is essentially a state machine. (see https://drive.google.com/file/d/1lFISFnhQ_wPcQpdqLnQWy5_dTsdnq6Dy/view?usp=sharing)
 * The discourse manager handles executing state transitions and executing outputs. These outputs take the form of
 * English conversation. The responses are randomly selected from a set of predefined options with formatting.
 */
public class DiscourseManager {
    public DiscourseManager() {}

    /**
     * Using an existing conversation template, generate a textual response
     * to the individual to further the conversation
     * @param intent a generalized summary of the message contents
     * @param cpnv an updated convesation model with data about the conversation so far
     * @return a string as a proposed response to the conversation
     */
    public String reply(MessageIntent intent, Conversation conv) {
        switch (conv.getState()) {    
        case Conversation.STATE_PROCESSING:
            return processing(conv);
        case Conversation.STATE_APPT_PROPOSED:
            return proposed(intent, conv);
        case Conversation.STATE_ARCHIVED:
            // TODO
            assert false;
        case Conversation.STATE_RESCHEDULING:
            return rescheduling(intent, conv);
        case Conversation.STATE_SET:
            return set(intent, conv);
        default:
            assert false;
        }
        
        assert false;
        return null;
    }

    private String processing(Conversation conv) {
        // propose a time block
        // generate the message
        // transition to a the message proposed state
        assert false;
        return null;
    }

    private String proposed(MessageIntent intent, Conversation conv) {
        switch (intent) {
        case AFFIRMATIVE:
            // save time
            // generate message
        case NEGATIVE:
            // block out time
            // repropose
            processing(conv);
        case CANCEL:
        default:
        }

        assert false;
        return null;
    }

    private String rescheduling(MessageIntent intent, Conversation conv) {
        switch (intent) {
        case AFFIRMATIVE:
            // schedule an appointment from scratch
            return processing(conv);
        case CANCEL:
            // proceed to NEGATIVE
        case NEGATIVE:
            // archive, probably stil do a message
        default:
        }

        assert false;
        return null;
    }

    private String set(MessageIntent intent, Conversation conv) {
        switch (intent) {
        case QUERY_WHEN:
            // go figure out when
        case CANCEL:
            // cancel
        case RESCHEDULE:
            // start rescheduling
        default:
        }

        assert false;
        return null;
    }
}