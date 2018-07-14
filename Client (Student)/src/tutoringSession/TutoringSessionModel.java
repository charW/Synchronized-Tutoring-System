package tutoringSession;

import java.io.*;
import java.net.*;
import javax.swing.event.EventListenerList;

// (client side)
public class TutoringSessionModel {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private NameInfo nameInfo;
    private String studentName, tutorName;
    private EventListenerList listenerList;
    private String serverAddress;
    private final int PORT_NUM = 6789;

    // constructor
    public TutoringSessionModel(String address, String studentName) {
        serverAddress = address;
        this.studentName = studentName;
        nameInfo = new NameInfo(studentName);
        listenerList = new EventListenerList();
    }

    // connect to tutor (server)
    void connectToTutor() {
        try {
            socket = new Socket(InetAddress.getByName(serverAddress), PORT_NUM);
            if (socket != null) {
                fireConnectedEvent(new ConnectedEvent(this));
            }
        } catch (UnknownHostException e) {
            reportMessage("That IP address didn't work...", MessageType.NOTIFICATION);
        } catch (IOException e) {
            reportMessage("Looks like this tutor is not available right now",
                    MessageType.NOTIFICATION);
        }
    }

    /*
    start the tutoring session by setting up the streams and read in any
    messages from tutor (server)
     */
    void startSession() {
        try {
            beingTutored();
        } catch (EOFException eofException) {
            reportMessage(tutorName + " has ended the connection", 
                    MessageType.NOTIFICATION);
            reportMessage("THIS SESSION HAS ENDED.", MessageType.DISCUSSION);
        } catch (ConnectException connectException) {
            reportMessage("No tutor available", MessageType.NOTIFICATION);
        } catch (UnknownHostException unknownHostException) {
            reportMessage("This address is not valid", MessageType.NOTIFICATION);
        } catch (IOException ioException) {
        } finally {
            endConnecting();
        }
    }

    // set up streams and continuously read in messages from tutor
    private void beingTutored() throws IOException {
        // setup streams
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());

        // send name of student
        send(nameInfo);

        // continuously read in message and let the controller
        // know of the incoming messages and their types
        Object message;
        try {
            while (true) {
                message = in.readObject();
                processMessage(message);
            }
        } catch (ClassNotFoundException e) {
            reportMessage("Can't figure out what the tutor sent", MessageType.NOTIFICATION);
        } catch (NullPointerException | SocketException e) {
        }
    }

    /*
    categorize message and report it and its type to the tutoring
    session controller
     */
    private void processMessage(Object message) {
        if (message instanceof String) {
            if (tutorName == null) {
                tutorName = "Tutor";
            }
            reportMessage(tutorName + ": " + (String) message, MessageType.DISCUSSION);
        } else if (message instanceof DrawingEvent) {
            reportMessage(message, MessageType.DRAWING);
        } else if (message instanceof NameInfo) {
            String name = ((NameInfo) message).getName();
            tutorName = name;
            reportMessage(name, MessageType.TUTOR_NAME);
            reportMessage("You are now connected with " + name, MessageType.NOTIFICATION);
        } else if (message instanceof MultimediaTabEvent) {
            reportMessage(message, MessageType.MULTIMEDIA_TAB);
        } else {
            reportMessage(message, MessageType.BROWSING);
        }
    }

    // end connection with tutor by closing the streams and socket
    void endConnecting() {
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException ioException) {
        }
    }

    /*
    send a message (from the dicussion panel, as part
    of the instant messaging) to tutor
     */
    void send(String message) {
        sendObject(message);
    }

    // send drawing event e to the tutor
    void send(DrawingEvent e) {
        sendObject(e);
    }

    // send student's name info to tutor
    void send(NameInfo nameInfo) {
        sendObject(nameInfo);
    }

    // send multimedia tab event e to tutor
    void send(MultimediaTabEvent e) {
        sendObject(e);
    }

    // send browsing event e to tutor
    void send(BrowsingEvent e) {
        sendObject(e);
    }

    // send object o to tutor
    private void sendObject(Object o) {
        try {
            out.writeObject(o);
            out.flush();
        } catch (IOException e1) {
        }
    }

    // report incoming message and its type to controller 
    void reportMessage(Object message, MessageType type) {
        fireMessageInEvent(new MessageInEvent(this, message, type));
    }

    // return name-info of the student
    NameInfo getNameInfo() {
        return nameInfo;
    }

    // fire the message-in event
    public void fireMessageInEvent(MessageInEvent e) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == MessageInListener.class) {
                ((MessageInListener) listeners[i + 1]).messageInEventOccured(e);
            }
        }
    }

    // add listener for message-in events
    public void addMessageInListener(MessageInListener listener) {
        listenerList.add(MessageInListener.class, listener);
    }

    // remove listener for message-in events
    public void removeMessageInListener(MessageInListener listener) {
        listenerList.remove(MessageInListener.class, listener);
    }

    // fire connected event e
    public void fireConnectedEvent(ConnectedEvent e) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == ConnectedListener.class) {
                ((ConnectedListener) listeners[i + 1]).connectedEventOccured(e);
            }
        }
    }

    // add listener for connected events
    public void addConnectedListener(ConnectedListener listener) {
        listenerList.add(ConnectedListener.class, listener);
    }

    // remove listener for connected events
    public void removeConnectedListener(ConnectedListener listener) {
        listenerList.remove(ConnectedListener.class, listener);
    }
}
