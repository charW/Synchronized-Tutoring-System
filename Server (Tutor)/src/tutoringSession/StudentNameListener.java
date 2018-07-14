package tutoringSession;

import java.util.EventListener;

public interface StudentNameListener extends EventListener {
	public void studentNameEventOccured(StudentNameEvent e);
}
