package tutoringSession;
import java.util.EventObject;

/*
This event is used to notify the student the name of the tutor
that he/she is connected to
*/
public class TutorNameEvent extends EventObject {
	private String name;

	// constructor 
	public TutorNameEvent(Object source, String name) {
		super(source);
		this.name = name;
	}
	
	// return tutor's name
	public String getTutorName() {
		return name;
	}
}
