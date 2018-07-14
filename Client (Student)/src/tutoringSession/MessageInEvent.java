package tutoringSession;

import java.util.EventObject;

/*
This event is used to notify the server or client side when a message
of one of the types specified in MessageType is received from the other side
or from the tutoring session model on this side (to report the
status of an ongoing session)
*/
public class MessageInEvent extends EventObject {

    private Object messageIn;
    private MessageType type;

    // constructor
    public MessageInEvent(Object source, Object messageIn, MessageType type) {
        super(source);
        this.messageIn = messageIn;
        this.type = type;
    }

    // return the incoming message
    public Object getMessage() {
        return messageIn;
    }

    // return the type of the incoming message
    public MessageType getMessageType() {
        return type;
    }
}
