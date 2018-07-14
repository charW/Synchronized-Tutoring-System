package tutoringSession;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;


public class BoardPanel extends JPanel {
    
    private Board board;
    private JPanel toolBar;

    /* constructor: construct a board panel with width and
       height, and has index-th order on the main tabbed pane 
       of the tutoring session */
    public BoardPanel(int width, int height, int index) {
        setPreferredSize(new Dimension(width, height));
        setLayout(new BorderLayout());
        board = new Board(index);
        toolBar = makeToolBar();
        add(board, BorderLayout.CENTER);
        add(toolBar, BorderLayout.SOUTH);
    }

    /* make and return a tool bar with all the necessary buttons
       (including selections, eraser, and clear) to control drawing
       activities on the board) */
    private JPanel makeToolBar() {
        JPanel toolBar = new JPanel();

        /* a group of buttons for color selections and eraser, 
           only one out of which can be selected at a time */
        JRadioButton red = new JRadioButton("Red");
        JRadioButton yellow = new JRadioButton("Yellow");
        JRadioButton blue = new JRadioButton("Blue");
        JRadioButton white = new JRadioButton("White");
        JRadioButton green = new JRadioButton("Green");
        JRadioButton eraser = new JRadioButton("Eraser");

        ButtonGroup buttons = new ButtonGroup();
        buttons.add(red);
        buttons.add(yellow);
        buttons.add(blue);
        buttons.add(white);
        buttons.add(green);
        buttons.add(eraser);

        // select white to be the default writing color
        white.setSelected(true);

        // button to clear the board
        JButton clear = new JButton("Clear");

        ActionListener action = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.setErase(false);
                if (e.getSource() == red) {
                    board.setRed();
                } else if (e.getSource() == yellow) {
                    board.setYellow();
                } else if (e.getSource() == blue) {
                    board.setBlue();
                } else if (e.getSource() == white) {
                    board.setWhite();
                } else if (e.getSource() == green) {
                    board.setGreen();
                } else if (e.getSource() == eraser) {
                    board.setErase(true);
                } else if (e.getSource() == clear) {
                    board.clear();
                }
            }
        };

        red.addActionListener(action);
        yellow.addActionListener(action);
        blue.addActionListener(action);
        white.addActionListener(action);
        green.addActionListener(action);
        eraser.addActionListener(action);
        clear.addActionListener(action);

        toolBar.add(red);
        toolBar.add(yellow);
        toolBar.add(blue);
        toolBar.add(white);
        toolBar.add(green);
        toolBar.add(eraser);
        toolBar.add(clear);

        return toolBar;
    }

    // update to the board the graphics specified by the drawing event e
    void updateBoard(DrawingEvent e) {
        board.updateGraphics(e);
    }

    // add listener for drawing events
    void addDrawingListener(DrawingListener listener) {
        board.addDrawingListener(listener);
    }

    // remove listener for drawing events
    void removeDrawingListener(DrawingListener listener) {
        board.removeDrawingListener(listener);
    }
}
