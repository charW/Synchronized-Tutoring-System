package tutoringSession;

import java.util.EventListener;

public interface DiscussionListener extends EventListener {
	public void discussionEventOccured(DiscussionEvent e);
}
