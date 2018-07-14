package tutoringManager;

import java.net.Socket;
import java.util.EventObject;

/*
This is to notify the user (i.e. tutor) that a connection has
been accepted from a new student
*/
public class NewStudentEvent extends EventObject{
	private Socket socket;
	
	// constructor
	public NewStudentEvent(Object source, Socket socket) {
		super(source);
		this.socket = socket;
	}
	
	
	// return socket
	public Socket getSocket() {
		return socket;
	}
}
