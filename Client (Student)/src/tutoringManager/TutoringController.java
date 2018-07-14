package tutoringManager;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import tutoringSession.*;

// (client side)
public final class TutoringController extends JFrame {

    final private JTabbedPane homePane;
    final private JPanel homePanel;
    private String studentName;
    final private int WIDTH, HEIGHT;

    // constructor 
    public TutoringController() {
        setLookAndFeel();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        WIDTH = (int) (screenSize.width * 0.87);
        HEIGHT = (int) (screenSize.height * 0.87);
        setSize(WIDTH, HEIGHT);
        setLayout(new BorderLayout());
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        homePanel = new JPanel(new BorderLayout());
        homePanel.add(makeConnectBtn(), BorderLayout.CENTER);

        homePane = new JTabbedPane();
        homePane.addTab("Home", homePanel);
        add(homePane, BorderLayout.CENTER);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                 askForStudentName();
            }
        });

    }

    /* return a button that, when clikced, asks the user for a tutor IP and 
       then starting a new tutoring session
     */
    private JButton makeConnectBtn() {
        JButton connectBtn = new JButton("Connect to a new tutor");
        connectBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tutorIP = askForTutorIP();
                if (tutorIP == null) {
                    return;
                }
                int sessionWidth = (int) (0.9 * WIDTH);
                int sessionHeight = (int) (0.9 * HEIGHT);
                initAndStartNewSession(sessionWidth, sessionHeight, tutorIP);
            }
        });
        return connectBtn;
    }

    /* intialize a panel for a new tutoring session with width and height,
       and add the new session panel to the main pane after a connection is 
       made to tutorIP and the corresponding tutor name is received.
       requires: tutor IP is not null and is a valid IP address of a server
     */
    private void initAndStartNewSession(int width, int height, String tutorIP) {
        final TutoringSessionController newSession = new TutoringSessionController(
                width, height, tutorIP, studentName);
        newSession.addNewSessionListener(new NewSessionListener() {
            @Override
            public void newSessionEventOccured(NewSessionEvent e) {
                Thread session = new Thread(newSession);
                session.start();
            }
        });

        newSession.connect();
        newSession.addTutorNameListener(new TutorNameListener() {
            @Override
            public void tutorNameEventOccured(TutorNameEvent e1) {
                homePane.addTab("Tutoring", newSession);
                int index = homePane.getTabCount() - 1;
                JPanel tabPanel = makeTabPanel(e1, newSession);
                homePane.setTabComponentAt(index, tabPanel);
                homePane.setSelectedComponent(newSession);
            }
        });
    }


    /* initialize and configure a panel for tabs that in particular adds
       a button to allow users to close each tab 
     */
    private JPanel makeTabPanel(TutorNameEvent e,
            TutoringSessionController session) {

        JPanel tabPanel = new JPanel(new GridBagLayout());
        tabPanel.setOpaque(false);
        JLabel tutorName = new JLabel(e.getTutorName() + "  ");
        JButton close = new JButton("x");

        close.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (session != null) {
                    
                    session.end();
                    homePane.remove(session);
                }
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 2;
        tabPanel.add(tutorName, gbc);

        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.gridx = 4;
        gbc.weightx = 0;
        tabPanel.add(close, gbc);

        return tabPanel;
    }

    /* keep asking for student's name until a valid string input is received
       (i.e. string is neither null nor empty); and then, updates studentName
       to a trimmed version of the user input 
     */
    private void askForStudentName() {
        studentName = (String) JOptionPane.
                showInputDialog("Please enter your name.");
        while (studentName == null || studentName.trim().isEmpty()) {
            studentName = (String) JOptionPane.showInputDialog(
                    "Invalid input; please enter your name again.");
        }
        studentName = studentName.trim();
    }

    /* keep asking user to input the IP address of the tutor until a valid
       String is received (i.e. string is not null or empty or comprised 
       of only spaces) or the user cancelled inputting
     */
    private String askForTutorIP() {
        String tutorIP = (String) JOptionPane.showInputDialog(
                "Enter tutor IP address (127.0.0.1 for localhost).");
        while (tutorIP != null && tutorIP.trim().equals((""))) {
            tutorIP = (String) JOptionPane.showInputDialog(
                    "Invalid input; please enter the tutor IP again.");
        }
        if (tutorIP != null) {
            tutorIP = tutorIP.trim();
        }
        return tutorIP;
    }

    // set a Nimbus look and feel for the GUI
    public void setLookAndFeel() {
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
}
