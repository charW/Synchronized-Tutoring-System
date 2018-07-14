package tutoringSession;

import java.util.EventObject;

/*
This event is used to notify the user that a connection is 
successfully made with a server (i.e. a tutor)
*/
public class ConnectedEvent extends EventObject {
	Object source;
	
	// constructor
	public ConnectedEvent(Object source) {
		super(source);
	}
}
