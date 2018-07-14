package tutoringSession;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.EventListenerList;

// (server side)
public class Board extends JComponent {
    
    private Image backBuffer;
    private Graphics2D backBufferG;
    private Coordinates currentPos, previousPos;
    private boolean isErasing, isDrawingDot, isClearing;
    static private float writingWidth, erasingWidth;
    static private Color currentColor;
    static private final Color BACKGROUND_COLOR = Color.black;
    private EventListenerList listenerList;
    private int index;

    /* constructor: construct the board with index-th position on the
       main tabbed pane of the tutoring session */
    public Board(int index) {
        this.index = index;
        isErasing = false;
        currentColor = Color.white;
        writingWidth = 2.5f;
        erasingWidth = 8f;
        listenerList = new EventListenerList();

        addListeners();
    }

    // add mouse and mouse motion listeners
    private void addListeners() {
        // add mouse interactivity through anonymous classes
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                previousPos = new Coordinates(e.getX(), e.getY());
                configureGraphics(isErasing);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (backBuffer != null) {
                    updateAfterClick(e.getX(), e.getY());
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (backBuffer != null) {
                    updateWhileDrag(e.getX(), e.getY());
                }
            }
        });
    }

    // initialize and configure the back buffer and its graphics
    private void initAndConfigureBackBuffer() {
        backBuffer = createImage(getWidth(), getHeight());
        backBufferG = (Graphics2D) backBuffer.getGraphics();
        backBufferG.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        selfClear();
    }

    /* set the appropriate stroke and color depending on whether the
       user is erasing */
    private void configureGraphics(boolean erasing) {
        if (backBuffer == null || backBufferG == null) {
            initAndConfigureBackBuffer();
        }

        if (erasing) {
            backBufferG.setStroke(new BasicStroke(erasingWidth,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));
            backBufferG.setPaint(BACKGROUND_COLOR);
        } else {
            backBufferG.setStroke(new BasicStroke(writingWidth,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));
            backBufferG.setPaint(currentColor);
        }
    }

    /* draw an circle with radius 3 at (x, y) where the mouse has
       clicked */
    private void updateAfterClick(int x, int y) {
        isDrawingDot = true;
        backBufferG.fillOval(x, y, 3, 3);
        repaint();
        currentPos = new Coordinates(x, y);
        sendDrawingEvent();
        isDrawingDot = false;
    }

    /* draw line from its previous coordinates to (x, y) where the mouse is 
       currently dragging pass, and updates the coordinates */
    private void updateWhileDrag(int x, int y) {
        backBufferG.drawLine(previousPos.x, previousPos.y, x, y);
        repaint();
        currentPos = new Coordinates(x, y);
        sendDrawingEvent();
        previousPos = currentPos;
    }

    /* transfer contents from back buffer to primary surface
       (also initialize and configure back buffer and its graphics 
       in the beginning) */
    @Override
    public void paintComponent(Graphics g) {
        if (backBuffer == null) {
            initAndConfigureBackBuffer();
        }
        g.drawImage(backBuffer, 0, 0, null);
    }

    // clear the board without sending the drawing event to sync
    private void selfClear() {
        backBufferG.setPaint(BACKGROUND_COLOR);
        backBufferG.fillRect(0, 0, getWidth(), getHeight());
        backBufferG.setPaint(currentColor);
        repaint();
    }

    /* clear the board and send the drawing event to sync the board
       on the other side of the network as well */
    void clear() {
        isClearing = true;
        backBufferG.setPaint(BACKGROUND_COLOR);
        backBufferG.fillRect(0, 0, getWidth(), getHeight());
        backBufferG.setPaint(currentColor);
        repaint();
        sendDrawingEvent();
        isClearing = false;
    }

    // set the board to eraseMode
    void setErase(boolean isErasing) {
        this.isErasing = isErasing;
    }

    // set the writing color to red
    void setRed() {
        currentColor = Color.red;
    }

    // set the writing color to yellow
    void setYellow() {
        currentColor = Color.yellow;
    }

    // set the writing color to blue
    void setBlue() {
        currentColor = Color.blue;
    }

    // set the writing color to green
    void setGreen() {
        currentColor = Color.green;
    }

    // set the writing color to white
    void setWhite() {
        currentColor = Color.white;
    }

    // update the board to display graphics specified by the drawing event e
    void updateGraphics(DrawingEvent e) {
        if (e.isClearing()) {
            selfClear();
            return;
        }
        boolean erasing = e.inErasingMode();
        configureGraphics(erasing);
        Color colorSent = BACKGROUND_COLOR;
        if (!erasing) {
            colorSent = e.getColor();
            backBufferG.setPaint(colorSent);
        }

        int previousX = e.getPreviousPos().x;
        int previousY = e.getPreviousPos().y;
        int currentX = e.getCurrentPos().x;
        int currentY = e.getCurrentPos().y;

        if (e.isDrawingDot()) {
            backBufferG.fillOval(currentX, currentY, 3, 3);
        } else {
            backBufferG.drawLine(previousX, previousY, currentX, currentY);
        }
        repaint();
        backBufferG.setPaint(currentColor);
    }

    /* send drawing event with the current state of the board, as reflected
       in the fields */
    void sendDrawingEvent() {
        fireDrawingEvent(new DrawingEvent(this,
                previousPos,
                currentPos,
                currentColor,
                isErasing,
                isDrawingDot,
                isClearing,
                index));
    }

    // fire drawing event e
    void fireDrawingEvent(DrawingEvent e) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == DrawingListener.class) {
                ((DrawingListener) listeners[i + 1]).drawingEventOccured(e);
            }
        }
    }

    // add drawing event listener
    void addDrawingListener(DrawingListener listener) {
        listenerList.add(DrawingListener.class, listener);
    }

    // remove drawing event listener
    void removeDrawingListener(DrawingListener listener) {
        listenerList.remove(DrawingListener.class, listener);
    }
}
