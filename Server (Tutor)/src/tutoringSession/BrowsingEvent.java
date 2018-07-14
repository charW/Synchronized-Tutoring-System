package tutoringSession;

import java.util.EventObject;

/*
This event is used to notify either server or client side when the other
side has navigated to a new url in the embedded web browser
*/
public class BrowsingEvent extends EventObject {

    private String url;
    private int index;
    private boolean isBrowsingForward, isBrowsingBack;

    /* constructor
       requires: isBrowsingBack and isBrowsingForward are not both true */
    public BrowsingEvent(Object source, int index, String url,
            Boolean isBrowsingForward, Boolean isBrowsingBack) {
        super(source);
        this.index = index;
        this.url = url;
        this.isBrowsingForward = isBrowsingForward;
        this.isBrowsingBack = isBrowsingBack;
    }

    /* various getter methods for retrieving different properties of
       the browsing event */
    
    public String getURL() {
        return url;
    }

    public int getIndex() {
        return index;
    }

    public boolean browsedForward() {
        return isBrowsingForward;
    }

    public boolean browsedBack() {
        return isBrowsingBack;
    }
}
