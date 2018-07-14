package tutoringSession;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.EventListenerList;

public class MultimediaPane extends JTabbedPane {

    private JPanel addPanel;
    private int width, height;
    private EventListenerList listenerList;

    // constructor
    public MultimediaPane(int width, int height) {
        super();
        this.width = width;
        this.height = height;
        setPreferredSize(new Dimension(width, height));
        setAddPanel();
        addTab("Add Something!", addPanel);
        listenerList = new EventListenerList();
    }

    /*
    create and return a board panel associated with the index-th tab
    also make the board panel listen for drawing events to get ready to sync
    across the network
    */
    private BoardPanel createBoardPanel(int index) {
        BoardPanel boardPanel = new BoardPanel(width, height, index);
        boardPanel.addDrawingListener(new DrawingListener() {
            @Override
            public void drawingEventOccured(DrawingEvent e) {
                fireDrawingEvent(e);
            }
        });
        return boardPanel;
    }

    /*
    create and return a browser panel associated with the index-th tab;
    also make the board panel listen for browsing events to get ready to sync
    across the network
    */
    private BrowserPanel createBrowserPanel(int index) {
        BrowserPanel browserPanel = new BrowserPanel(width, height, index);
        browserPanel.addBrowsingListener(new BrowsingListener() {
            @Override
            public void browsingEventOccured(BrowsingEvent e) {
                fireBrowsingEvent(e);
            }
        });
        return browserPanel;
    }
    
    /*
     add media to the multimedia pane with media object type as specified
     and under the tab with name tabName
     */
    void addMedia(String tabName, MediaObjectType type) {
        int index = getTabCount();
        if (type.equals(MediaObjectType.BOARD)) {
            BoardPanel boardPanel = createBoardPanel(index);
            tabName = "Board: " + tabName + "  ";
            addTab(tabName, boardPanel);
            setTabComponentAt(index, makeTabPanel(tabName, boardPanel));
        } else {
            BrowserPanel browserPanel = createBrowserPanel(index);
            tabName = "Browser: " + tabName + "  ";
            addTab(tabName, browserPanel);
            setTabComponentAt(index, makeTabPanel(tabName, browserPanel));
        }
        setSelectedIndex(index);
    }

    /*
    create and return a button that enables the user to add an embedded 
    web browser to the communication platform between the client and the server
     */
    private JButton createAddBrowserBtn() {
        JButton addBrowserBtn = new JButton();
        try {
            Image browseImg = ImageIO.read(getClass().getResource("internet.png"));
            addBrowserBtn.setIcon(new ImageIcon(browseImg));
        } catch (IOException ex) {
        }
        addBrowserBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tabName = (String) JOptionPane.showInputDialog(
                        "Please enter the browser name.");
                while (tabName != null && tabName.trim().equals("")) {
                    tabName = (String) JOptionPane.showInputDialog(
                            "Invalid input. Please enter browser name again.");
                }
                if (tabName != null) {
                    tabName = tabName.trim();
                    addMedia(tabName, MediaObjectType.BROWSER);
                    sendBrowserAddedEvent(tabName);
                }
            }
        });
        return addBrowserBtn;
    }

    /*
    create and return a button that enables the user to add a blackboard 
    to the communication platform between the client and the server
     */
    private JButton createAddBoardBtn() {
        JButton addBoardBtn = new JButton();
        try {
            Image boardImg = ImageIO.read(getClass().getResource("board.png"));
            addBoardBtn.setIcon(new ImageIcon(boardImg));
        } catch (IOException ex) {
        }
        addBoardBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tabName = JOptionPane.showInputDialog(
                        "Please enter the board name.");
                while (tabName != null && tabName.trim().equals("")) {
                    tabName = (String) JOptionPane.showInputDialog(
                            "Invalid input. Please enter board name again.");
                }
                if (tabName != null) {
                    tabName = tabName.trim();
                    addMedia(tabName, MediaObjectType.BOARD);
                    sendBoardAddedEvent(tabName);
                }
            }
        });
        return addBoardBtn;
    }

    /*
    init and add components to the add panel, which is the panel under 
    the tab named "Add something!", and it's there so the user can choose 
    to add one more blackboard or browser to the communication platform
    between the client and the server
     */
    private void setAddPanel() {
        addPanel = new JPanel();

        JButton addBrowserBtn = createAddBrowserBtn();
        JButton addBoardBtn = createAddBoardBtn();

        addPanel.add(addBoardBtn);
        addPanel.add(addBrowserBtn);
    }

    /*
     initialize and configure a panel for tabs that in particular adds
     a button to allow users to close each tab 
     */
    JPanel makeTabPanel(String tabName, JPanel panel) {
        JPanel tabPanel = new JPanel(new GridBagLayout());
        tabPanel.setOpaque(false);
        JLabel tutorName = new JLabel(tabName);
        JButton close = new JButton("x");

        close.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (panel != null) {
                    int index = indexOfComponent(panel);
                    if (panel instanceof BrowserPanel) {
                        ((BrowserPanel) panel).endCurrentWebContent();
                    }
                    sendMediaRemovedEvent(index);
                    remove(panel);
                }
            }
        });

        // set layout constraints
        GridBagConstraints gbc = new GridBagConstraints();

        // left -------------------------
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        tabPanel.add(tutorName, gbc);

        // right-------------------------
        gbc.gridx = 4;
        gbc.weightx = 1;
        tabPanel.add(close, gbc);

        return tabPanel;
    }

    /*
    end all current web content in any embedded browser in the
    multimedia pane; this's should be called when the tutoring session that
    this multimedia pane is in is being ended
    */
    void endAllWebContent() {
        int tabCount = getTabCount();
        for (int i = 1; i < tabCount; ++i) {
            Object component = getComponentAt(i);
            if (component instanceof BrowserPanel) {
                ((BrowserPanel)component).endCurrentWebContent();
            }
        }
    }
    
    // update the graphics specified by e to the board
    void updateBoard(DrawingEvent e) {
        int index = e.getIndex();
        ((BoardPanel) getComponentAt(index)).updateBoard(e);
    }

    /*
    send event about how a browser has been added under a new tab
    with tabName
     */
    private void sendBrowserAddedEvent(String tabName) {
        MultimediaTabEvent videoAddedEvent = new MultimediaTabEvent(this);
        videoAddedEvent.setToAdd(MediaObjectType.BROWSER, tabName);
        fireMultimediaTabEvent(videoAddedEvent);
    }

    /*
    send event about how a board has been added under a new tab
    with tabName
     */
    private void sendBoardAddedEvent(String tabName) {
        MultimediaTabEvent boardAddedEvent = new MultimediaTabEvent(this);
        boardAddedEvent.setToAdd(MediaObjectType.BOARD, tabName);
        fireMultimediaTabEvent(boardAddedEvent);
    }

    
    /*
    send event about how a media (a board or a browser) under the
    indexToRemove-th tab has been removed
     */
    private void sendMediaRemovedEvent(int indexToRemove) {
        MultimediaTabEvent mediaRemovedEvent = new MultimediaTabEvent(this);
        mediaRemovedEvent.setToRemove(indexToRemove);
        fireMultimediaTabEvent(mediaRemovedEvent);
    }

    // fire multimedia tab event e
    void fireMultimediaTabEvent(MultimediaTabEvent e) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == MultimediaTabListener.class) {
                ((MultimediaTabListener) listeners[i + 1]).
                        newMultimediaTabEventOccured(e);
            }
        }
    }

    // add listener for multimedia tab event
    void addMultimediaTabListener(MultimediaTabListener listener) {
        listenerList.add(MultimediaTabListener.class, listener);
    }

    // add listener for multimedia tab event
    void removeMultimediaTabListener(MultimediaTabListener listener) {
        listenerList.remove(MultimediaTabListener.class, listener);
    }

    // fire browsing event e (for the embedded browser)
    void fireBrowsingEvent(BrowsingEvent e) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == BrowsingListener.class) {
                ((BrowsingListener) listeners[i + 1]).browsingEventOccured(e);
            }
        }
    }

    // add listener for browsing events (for the embedded browser)
    void addBrowsingListener(BrowsingListener listener) {
        listenerList.add(BrowsingListener.class, listener);
    }

    // remove listener for browsing events (for the embedded browser)
    void removeBrowsingListener(BrowsingListener listener) {
        listenerList.remove(BrowsingListener.class, listener);
    }


    // fire drawing event e (for the board)
    void fireDrawingEvent(DrawingEvent e) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == DrawingListener.class) {
                ((DrawingListener) listeners[i + 1]).drawingEventOccured(e);
            }
        }
    }

    // add listener for drawing events (for the board)
    void addDrawingListener(DrawingListener listener) {
        listenerList.add(DrawingListener.class, listener);
    }

    // remove listener for drawing events (for the board)
    void removeDrawingListener(DrawingListener listener) {
        listenerList.remove(DrawingListener.class, listener);
    }
}
