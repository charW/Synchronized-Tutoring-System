package tutoringSession;

import java.io.*;
import java.net.*;
import javax.swing.event.EventListenerList;

// (server side)
public class TutoringSessionModel {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String tutorName, studentName;
    private NameInfo nameInfo;
    private EventListenerList listenerList;

    // constructor
    public TutoringSessionModel(Socket socket, String tutorName) {
        this.socket = socket;
        this.tutorName = tutorName;
        nameInfo = new NameInfo(tutorName);
        listenerList = new EventListenerList();
    }

    /*
    begins tutoring by setting up streams and reading in messages from the
    student (client); catch any exceptions and report them to the
    tutoring session controller
     */
    void beginTutoring() {
        try {
            conductTutoring();
        } catch (EOFException eofException) {
            reportMessage(studentName + " ended connection", 
                    MessageType.NOTIFICATION);
            reportMessage("THIS SESSION HAS ENDED.", MessageType.DISCUSSION);
        } catch (IOException ioException) {
        } finally {
            endTutoring();
        }
    }

    /*
    start the tutoring session by setting up the streams and keep reading in
    incoming messages from the student (client)
    */
    private void conductTutoring() throws IOException {
        // set up streams
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());

        // send the name of the tutor (i.e. the user)
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
            reportMessage("Can't figure out what the student sent", 
                    MessageType.NOTIFICATION);
        } catch (NullPointerException | SocketException e) {
        }
    }

    /*
    categorize the message and report it and its type to the tutoring
    session controller
     */
    private void processMessage(Object message) {
        if (message instanceof String) {
            if (studentName == null) {
                studentName = "Student";
            }
            reportMessage(studentName + ": " + (String) message, 
                    MessageType.DISCUSSION);
        } else if (message instanceof DrawingEvent) {
            reportMessage(message, MessageType.DRAWING);
        } else if (message instanceof NameInfo) {
            String name = ((NameInfo) message).getName();
            studentName = name;
            reportMessage(name, MessageType.STUDENT_NAME);
            reportMessage("You are now connected with " + name, 
                    MessageType.NOTIFICATION);
        } else if (message instanceof MultimediaTabEvent) {
            reportMessage(message, MessageType.MULTIMEDIA_TAB);
        } else {
            reportMessage(message, MessageType.BROWSING);
        }
    }

    // close streams and sockets
    void endTutoring() {
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException ioException) {
        }
    }

    /*
    send a message (from the dicussion panel, as part
    of the instant messaging) to student
     */
    void send(String message) {
        sendObject(message);
    }

    // send a drawing event e to student
    void send(DrawingEvent e) {
        sendObject(e);
    }

    // send tutor's name info to the student
    void send(NameInfo nameInfo) {
        sendObject(nameInfo);
    }

    // send multimedia tab event e to student
    void send(MultimediaTabEvent e) {
        sendObject(e);
    }

    // send browsing event e to student
    void send(BrowsingEvent e) {
        sendObject(e);
    }

    // send object to student
    private void sendObject(Object o) {
        try {
            out.writeObject(o);
            out.flush();
        } catch (IOException e1) {
        }
    }

    // report to controller incoming messages and their types
    void reportMessage(Object message, MessageType type) {
        fireMessageInEvent(new MessageInEvent(this, message, type));
    }

    // fire message-in event e
    void fireMessageInEvent(MessageInEvent e) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == MessageInListener.class) {
                ((MessageInListener) listeners[i + 1]).messageInEventOccured(e);
            }
        }
    }

    // add listener for incoming messages from the student (client)
    void addMessageInListener(MessageInListener listener) {
        listenerList.add(MessageInListener.class, listener);
    }

    // remove listener for incoming messages from the student (client)
    void removeMessageInListener(MessageInListener listener) {
        listenerList.remove(MessageInListener.class, listener);
    }
}
