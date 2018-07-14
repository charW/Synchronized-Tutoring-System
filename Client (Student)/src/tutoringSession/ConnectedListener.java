package tutoringSession;

import java.util.EventListener;

public interface ConnectedListener extends EventListener {
	public void connectedEventOccured(ConnectedEvent e);
}
