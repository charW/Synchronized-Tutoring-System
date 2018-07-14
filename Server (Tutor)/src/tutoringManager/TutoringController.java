package tutoringManager;

import java.io.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import tutoringSession.*;

// controller
public class TutoringController extends JFrame {

    private JTabbedPane homePane;
    private JPanel homePanel, tabPanel;
    private JTextArea display;
    private int WIDTH, HEIGHT;
    private TutoringModel tutoringModel;
    private String tutorName;

    // constructor 
    public TutoringController() throws IOException {
        setLookAndFeel();
        
        // set size of the frame
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        WIDTH = (int) (screenSize.width * 0.87);
        HEIGHT = (int) (screenSize.height * 0.87);
        setSize(WIDTH, HEIGHT);

        // create main panel and add it to pane
        display = new JTextArea();
        display.setEditable(false);
        homePanel = new JPanel();
        homePanel.setLayout(new BorderLayout());
        homePanel.add(display, BorderLayout.CENTER);
        homePane = new JTabbedPane();
        homePane.addTab("Home", homePanel);

        // configure the frame and add pane to it
        setLayout(new BorderLayout());
        add(homePane, BorderLayout.CENTER);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        askForTutorName();
        
        setTutoringModel();
    }
    
    /*
    initialize the tutoring model and add to it listener for new student
    events so that a new tutoring session can be created with each new student
    */
    private void setTutoringModel() {
        tutoringModel = new TutoringModel();
        tutoringModel.addNewStudentListener(new NewStudentListener() {
            @Override
            public void newStudentEventOccured(final NewStudentEvent e) {
                int sWidth = (int) (0.9 * WIDTH);
                int sHeight = (int) (0.9 * HEIGHT);
                TutoringSessionController newSession = new TutoringSessionController(
                        sWidth, sHeight, e.getSocket(), tutorName);
                Thread session = new Thread(newSession);
                session.start();
                newSession.addStudentNameListener(new StudentNameListener() {
                    @Override
                    public void studentNameEventOccured(StudentNameEvent e1) {
                        homePane.addTab("Tutoring", newSession);
                        int index = homePane.getTabCount() - 1;
                        tabPanel = makeTabPanel(e1, newSession);
                        homePane.setTabComponentAt(index, tabPanel);
                        homePane.setSelectedComponent(newSession);
                    }
                });
            }
        });
    }

    /*
    initialize and configure a panel for tabs, which in particular has
    a button that allows the user to close it
    */
    private JPanel makeTabPanel(StudentNameEvent e, 
            TutoringSessionController session) {
        JPanel tabPanel = new JPanel(new GridBagLayout());
        tabPanel.setOpaque(false);
        JLabel tutorName = new JLabel(e.getStudentName()+ "  ");
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

        // set layout constraints
        GridBagConstraints gbc = new GridBagConstraints();

        // left -------------------------
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        tabPanel.add(tutorName, gbc);

        // right-------------------------
        gbc.gridx = 4;
        gbc.weightx = 0;
        tabPanel.add(close, gbc);

        return tabPanel;
    }

    /* 
    begins tutoring; initializes the process of creating a separate MCV for
    each student client
    */
    public void begin() {
        try {
            tutoringModel.beginTutoring(WIDTH, HEIGHT);
        } catch (IOException e) {
        }
    }

    /* 
     keep asking for tutor's name until a valid string input is received
     (i.e. string is not null or empty or comprised of only spaces); and
     update tutorName to a trimmed version of the user input
    */
    private void askForTutorName() {
        tutorName = JOptionPane.showInputDialog("Please enter your name.");
        while (tutorName == null || tutorName.trim().isEmpty()) {
            tutorName = (String) JOptionPane.showInputDialog(
                    "Invalid input; please enter your name again.");
        }
        display.setText("Welcome, " + tutorName + "!\n");
        tutorName = tutorName.trim();
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
}
