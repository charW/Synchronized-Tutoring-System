package tutoringSession;

import java.io.Serializable;

public class Coordinates implements Serializable {

    public int x, y;

    // constructor: constructs the coordinate (x, y)
    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
