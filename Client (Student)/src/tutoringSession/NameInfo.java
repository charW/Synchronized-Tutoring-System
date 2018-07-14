package tutoringSession;

import java.io.Serializable;

/*
This is sent across the network to let the other side know the name
of the user on this side; typically sent when the connection between
the client/server sides is first established
*/
public class NameInfo implements Serializable {
	private String name;
	
	// constructor 
	public NameInfo (String name) {
		this.name = name;
	}
	
	
	// returns the name field
	public String getName() {
		return name;
	}
}
