package tutoringSession;

import java.util.EventObject;

/*
This event is used to inform the tutor the name of the student
that he/she is connected to
*/
public class StudentNameEvent extends EventObject {
	String studentName;
	
        // constructor
	public StudentNameEvent(Object source, String name) {
		super(source);
		studentName = name;
	}
	
        // return the student name
	public String getStudentName() {
		return studentName;
	}
}
