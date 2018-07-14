package tutoringSession;

import java.util.EventListener;

public interface DrawingListener extends EventListener {
	public void drawingEventOccured(DrawingEvent e);
}
