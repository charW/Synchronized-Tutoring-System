package tutoringManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.event.EventListenerList;

public class TutoringModel {

    private ServerSocket serverSocket;
    private final int PORT_NUM = 6789;
    private EventListenerList listenerList;

    // constructor
    public TutoringModel() {
        listenerList = new EventListenerList();
    }

    /*
     accepts connection from students (clients) and let the controller know
      once new connections are made
     */
    public void beginTutoring(int width, int height) throws IOException {
        try {
            serverSocket = new ServerSocket(PORT_NUM);
            while (true) {
                Socket socket = serverSocket.accept();
                fireNewStudentEvent(new NewStudentEvent(this, socket));
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            serverSocket.close();
        }
    }

    // fire new student event e
    private void fireNewStudentEvent(NewStudentEvent e) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == NewStudentListener.class) {
                ((NewStudentListener) listeners[i + 1]).newStudentEventOccured(e);
            }
        }
    }

    // add listener for connections made with new students
    void addNewStudentListener(NewStudentListener listener) {
        listenerList.add(NewStudentListener.class, listener);
    }

    // remove listener for connections made with new students
    void removeNewStudentListener(NewStudentListener listener) {
        listenerList.remove(NewStudentListener.class, listener);
    }
}
