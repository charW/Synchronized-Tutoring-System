package tutoringSession;

import java.io.Serializable;
import java.util.EventObject;

/*
This event is used to notify the server or client side when the other
side has added a media (of one of the types in MediaObjectType) or has
removed a media under a specific tab from the multimedia pane
*/
public class MultimediaTabEvent extends EventObject implements Serializable {
    
    private boolean toAdd;
    private int indexToRemove;
    private MediaObjectType type;
    private String videoURL;
    private String tabName;
    
    
    // constructor
    public MultimediaTabEvent(Object source) {
        super(source);
    }
    
    /*
    set the type of the media added under the multimedia tab, and set
    the name of that tab
    
    requires: type != null
              tabName != null
    */
    public void setToAdd(MediaObjectType type, String tabName) {
        toAdd = true;
        this.type = type;
        this.tabName = tabName;
    }
    
    /*
    set the index of the tab to be removed
    
    requires: 0 < indexToRemove < total number of tabs in multimedia pane
    */
    public void setToRemove(int indexToRemove) {
        toAdd = false;
        this.indexToRemove = indexToRemove;
    }
    
    /*
    various getter methods to retrieve properties of the this event -----------
    */
    public boolean isForAdding() {
        return toAdd;
    }
    
    public boolean isForRemoving() {
        return !toAdd;
    }
    
    public int getIndexToRemove() {
        return indexToRemove;
    }
    
    public MediaObjectType getMediaObjectType() {
        return type;
    }
    
    public String getVideoURL() {
        return videoURL;
    }
    
    public String getTabName() {
        return tabName;
    }
}
