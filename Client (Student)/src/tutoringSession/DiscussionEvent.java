package tutoringSession;

import java.util.EventObject;

/*
This event is used to notify either server or client side when the other
side has typed in a new comment via the instant messaging system
*/
public class DiscussionEvent extends EventObject {
    
    // the message typed in the instant messaging system
    private String message;

    // constructor
    public DiscussionEvent(Object source, String text) {
        super(source);
        message = text;
    }

    // return message
    public String getMessage() {
        return message;
    }
}
