package tutoringSession;

import java.util.EventListener;


public interface BrowsingListener extends EventListener {
    public void browsingEventOccured(BrowsingEvent e);
}
