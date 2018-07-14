package tutoringSession;

import java.awt.*;
import java.net.Socket;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.EventListenerList;

// (server side)
public class TutoringSessionController extends JPanel implements Runnable {

    private DiscussionPanel discussionPanel;
    private MultimediaPane multimediaPane;
    private TutoringSessionModel sessionModel;
    private EventListenerList listenerList;

    // constructor
    public TutoringSessionController(int width, int height, Socket socket, String name) {
        setLookAndFeel();
        setLayout(new BorderLayout());

        listenerList = new EventListenerList();
        setTutoringSessionModel(socket, name);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setMultimediaPane(width, height);
                setDiscussionPanel(width, height);
                add(discussionPanel, BorderLayout.WEST);
                add(multimediaPane, BorderLayout.CENTER);
            }
        });
    }

    /*
    initialize the discussion panel with dimension based on width and height,
    and make it listen to and process discussion events
     */
    private void setDiscussionPanel(int width, int height) {
        discussionPanel = new DiscussionPanel((int) (width * 0.3), height);
        discussionPanel.addDiscussionListener(new DiscussionListener() {
            @Override
            public void discussionEventOccured(DiscussionEvent e) {
                processDiscussion(e.getMessage());
            }
        });
    }

    /*
    initialize the multimedia panel with dimension based on width and height
    and make it listen to and process multimedia tab events, browsing events,
    and drawing events
     */
    private void setMultimediaPane(int width, int height) {
        multimediaPane = new MultimediaPane((int) (width * 0.7), height);
        multimediaPane.addMultimediaTabListener(new MultimediaTabListener() {
            @Override
            public void newMultimediaTabEventOccured(MultimediaTabEvent e) {
                sessionModel.send(e);
            }
        });
        multimediaPane.addBrowsingListener(new BrowsingListener() {
            @Override
            public void browsingEventOccured(BrowsingEvent e) {
                sessionModel.send(e);
            }
        });
        multimediaPane.addDrawingListener(new DrawingListener() {
            @Override
            public void drawingEventOccured(DrawingEvent e) {
                sessionModel.send(e);
            }
        });
    }

    /*
    initialize the tutoring session model with socket and name
    of the tutor, and make it listen to and process incoming messages
     */
    private void setTutoringSessionModel(Socket socket, String name) {
        sessionModel = new TutoringSessionModel(socket, name);
        sessionModel.addMessageInListener(new MessageInListener() {

            @Override
            public void messageInEventOccured(MessageInEvent e) {
                processMessage(e.getMessageType(), e.getMessage());
            }
        });
    }

    /*
    do nothing if the message is null, empty, or comprised of only spaces;
    otherwise ask the model to send the message to the tutor and update 
    the chat history in the discussion panel
     */
    private void processDiscussion(String message) {
        if (message == null) {
            return;
        }
        message = message.trim();
        if (!message.equals("")) {
            discussionPanel.updateChatHistory("Me: " + message + "\n\n");
            sessionModel.send(message);
        }
    }

    /*
    process accroding to its type the message sent from the model to the 
    controller and update the GUI accordingly
     */
    private void processMessage(final MessageType type, final Object message) {
        JPanel currentPanel = this;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                switch (type) {
                    case DISCUSSION:
                        discussionPanel.updateChatHistory((String) message + "\n\n");
                        break;
                    case DRAWING:
                        multimediaPane.updateBoard((DrawingEvent) message);
                        break;
                    case STUDENT_NAME:
                        fireStudentNameEvent(new StudentNameEvent(this, (String) message));
                        break;
                    case NOTIFICATION:
                        JOptionPane.showMessageDialog(currentPanel, (String) message);
                        break;
                    case MULTIMEDIA_TAB:
                        processMultimediaTabMessage((MultimediaTabEvent) message);
                        break;
                    case BROWSING:
                        processBrowsingMessage((BrowsingEvent) message);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /*
    update according to the incoming browsing event e the content
    in a browser under a specific tab in the multiemdia pane
     */
    private void processBrowsingMessage(BrowsingEvent e) {
        int index = e.getIndex();
        Component component = multimediaPane.getComponentAt(index);
        multimediaPane.setSelectedIndex(index);
        BrowserPanel browser = ((BrowserPanel) component);
        if (e.browsedBack()) {
            browser.browseBackWithoutSync();
        } else if (e.browsedForward()) {
            browser.browseForwardWithoutSync();
        } else {
            browser.loadURLWithoutSync(e.getURL());
        }
    }

    /*
    decide whether to add to or to remove a tab with a board/browser from the
    multimedia pane according to e
     */
    private void processMultimediaTabMessage(MultimediaTabEvent e) {
        if (e.isForAdding()) {
            multimediaPane.addMedia(e.getTabName(), e.getMediaObjectType());
            return;
        }
        Component component = multimediaPane.getComponentAt(e.getIndexToRemove());
        if (component instanceof BrowserPanel) {
            ((BrowserPanel) component).endCurrentWebContent();
        }
        multimediaPane.remove(component);
    }

    /* 
    terminate all web contents in any of the browsers in the
    multimedia pane before ending connections with the student (client)
     */
    public void end() {
        multimediaPane.endAllWebContent();
        sessionModel.endTutoring();
    }

    // set a Nimbus look and feel for the GUI
    private void setLookAndFeel() {
        for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                try {
                    UIManager.setLookAndFeel(info.getClassName());
                } catch (Exception e) {
                }
                break;
            }
        }
    }

    // fire student name event e
    void fireStudentNameEvent(StudentNameEvent e) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == StudentNameListener.class) {
                ((StudentNameListener) listeners[i + 1]).studentNameEventOccured(e);
            }
        }
    }

    // add listener for student name events
    public void addStudentNameListener(StudentNameListener listener) {
        listenerList.add(StudentNameListener.class, listener);
    }

    // remove listener for student name events
    public void removeStudentNameListener(StudentNameListener listener) {
        listenerList.remove(StudentNameListener.class, listener);
    }

    @Override
    public void run() {
        sessionModel.beginTutoring();
    }
}
