package tutoringSession;

import java.util.EventListener;

public interface NewSessionListener extends EventListener {
	public void newSessionEventOccured(NewSessionEvent e);
}
