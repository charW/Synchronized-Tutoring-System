package tutoringSession;

import java.util.EventListener;

public interface TutorNameListener extends EventListener {
	public void tutorNameEventOccured(TutorNameEvent e);
}
