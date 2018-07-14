package tutoringManager;

import java.util.EventListener;


public interface NewStudentListener extends EventListener {
	public void newStudentEventOccured(NewStudentEvent e);
}
