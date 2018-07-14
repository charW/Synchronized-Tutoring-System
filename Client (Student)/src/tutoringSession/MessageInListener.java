package tutoringSession;

import java.util.EventListener;

public interface MessageInListener extends EventListener {
	public void messageInEventOccured(MessageInEvent e);
}
