package tutoringSession;

import java.awt.Color;
import java.io.Serializable;
import java.util.EventObject;


/*
This event is used to notify the server or client side when the other
side has drawn something new on the blackboard
*/
public class DrawingEvent extends EventObject implements Serializable {

    private Coordinates currentPos, previousPos, textPos;
    private Boolean eraseMode, drawDot, clearMode;
    private Color color;
    private int boardIndex;

    // constructor
    public DrawingEvent(Object source,
            Coordinates previousPos,
            Coordinates currentPos,
            Color color,
            boolean eraseMode,
            boolean drawDot,
            boolean clearMode,
            int boardIndex) {
        super(source);
        this.currentPos = currentPos;
        this.previousPos = previousPos;
        this.color = color;
        this.eraseMode = eraseMode;
        this.drawDot = drawDot;
        this.clearMode = clearMode;
        this.boardIndex = boardIndex;
    }

    /* various getter methods to retrieve different properties
       of the object drawn */
    
    public Coordinates getCurrentPos() {
        return currentPos;
    }

    public Coordinates getPreviousPos() {
        return previousPos;
    }

    public Color getColor() {
        return color;
    }

    public boolean inErasingMode() {
        return eraseMode;
    }

    public boolean isDrawingDot() {
        return drawDot;
    }

    public boolean isClearing() {
        return clearMode;
    }

    public int getIndex() {
        return boardIndex;
    }
}
