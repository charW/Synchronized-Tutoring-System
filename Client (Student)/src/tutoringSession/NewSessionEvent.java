package tutoringSession;

import java.util.EventObject;

/*
This is used after a new connection is made to notify
the student (client) that a new session with tutor (server) will
take place based on that connection
*/
public class NewSessionEvent extends EventObject {
	
	// constructor
	public NewSessionEvent(Object source) {
		super(source);
	}
}
