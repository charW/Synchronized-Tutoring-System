package tutoringSession;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class DiscussionPanel extends JPanel {

    private JTextArea chatHistory;
    private JPanel enterPanel;
    private EventListenerList listenerList;

    /* constructor: constructs a discussion panel (= the area where
       you enter the message + area where the message is displayed) with
       the specified width and height */
    public DiscussionPanel(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Discussion"));

        chatHistory = new JTextArea();
        chatHistory.setEditable(false);
        chatHistory.setLineWrap(true);
        chatHistory.setWrapStyleWord(true);
        JScrollPane chatHistoryPane = new JScrollPane(chatHistory);
        enterPanel = makeEnterPanel();
        listenerList = new EventListenerList();

        add(chatHistoryPane, BorderLayout.CENTER);
        add(enterPanel, BorderLayout.SOUTH);
    }


    /* make the enter panel through which the user types comments that add to
	   chat history or get posted onto the board */
    public JPanel makeEnterPanel() {
        JPanel enterPanel = new JPanel(new GridBagLayout());

        JTextArea chatEnter = new JTextArea();
        chatEnter.setLineWrap(true);
        chatEnter.setWrapStyleWord(true);
        chatEnter.setRows(3);
        JScrollPane chatEnterPane = new JScrollPane(chatEnter);
        
        JCheckBox enterSend = new JCheckBox("Press enter to send");
        enterSend.setSelected(true); // set default

        JButton sendBtn = new JButton("Send");

        // enable user to send message by: --------------------
        // 1. send text by pressing enter if the user has chosen to do so
        setEnterKeyAction(chatEnter, enterSend);

        // 2. send or post text through the corresponding buttons
        sendBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = chatEnter.getText();
                fireDiscussionEvent(new DiscussionEvent(this, text));
                chatEnter.setText("");
            }
        });

        // work with layout and add components to panel-------------------------
        GridBagConstraints c = new GridBagConstraints();

        // FIRST ROW
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 5;
        enterPanel.add(chatEnterPane, c);

        
        // SECOND ROW
        // first column
        c.gridwidth = 1;
        c.weightx = 0.25;
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LAST_LINE_START;
        enterPanel.add(enterSend, c);

        // fourth column
        c.gridx = 3;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LAST_LINE_END;
        enterPanel.add(sendBtn, c);

        // return the finished panel --------------------------------------------
        return enterPanel;
    }


    /* enable user to send text in chatEnter by pressing the enter key
       if the user has selected to do so by selecting enterSend; otherwise
       add newline to the text in chatEnter when enter key is pressed
       (default action when editing text area) */
    private void setEnterKeyAction(JTextArea chatEnter, JCheckBox enterSend) {
        InputMap inputMap = chatEnter.getInputMap(WHEN_FOCUSED);
        ActionMap actionMap = chatEnter.getActionMap();
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);

        inputMap.put(enter, enter.toString());

        actionMap.put(enter.toString(), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (enterSend.isSelected()) {
                    String text = chatEnter.getText();
                    fireDiscussionEvent(new DiscussionEvent(this, text));
                    chatEnter.setText("");
                } else {
                    chatEnter.setText(chatEnter.getText() + "\n");
                }
            }
        });
    }

    // update chat history by adding message to it
    void updateChatHistory(String message) {
        chatHistory.append(message);
    }

    // fire send event (for texts to be sent through networking)
    void fireDiscussionEvent(DiscussionEvent event) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == DiscussionListener.class) {
                ((DiscussionListener) listeners[i + 1]).discussionEventOccured(event);
            }
        }
    }

    // add listener for discussion events
    void addDiscussionListener(DiscussionListener listener) {
        listenerList.add(DiscussionListener.class, listener);
    }

    // remove listener for discussion events
    void removeDiscussionListener(DiscussionListener listener) {
        listenerList.remove(DiscussionListener.class, listener);
    }
}
