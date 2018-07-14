package tutoringSession;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.EventListenerList;

public class BrowserPanel extends JPanel {

    private WebEngine webEngine;
    private WebView browser;
    private BorderPane root;
    private EventListenerList listenerList;
    private JFXPanel fxPanel;
    private JTextField urlEnter;
    private int index, width, height;
    private ArrayList<String> historyList;
    private int currentHistoryPos;
    private boolean isBrowsingBack, isBrowsingForward, shouldSync;

    /*
    construct a browser panel with width and height under the
    index-th tab in the multimedia pane
     */
    public BrowserPanel(int width, int height, int index) {
        setPreferredSize(new Dimension(width, height));
        setLayout(new BorderLayout());

        this.index = index;
        this.width = width;
        this.height = height;
        historyList = new ArrayList();
        currentHistoryPos = -1;
        isBrowsingBack = false;
        shouldSync = true;
        listenerList = new EventListenerList();
        fxPanel = new JFXPanel();
        fxPanel.setPreferredSize(new Dimension(width, height));
        add(fxPanel, BorderLayout.CENTER);
        Platform.setImplicitExit(false);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(fxPanel);
            }
        });
    }

    /*
    create the url field which enable the user to enter the url they want to
    go to; only HTTPS connections can be made with the url
     */
    private TextField createURLField() {
        TextField urlField = new TextField();
        urlField.setPromptText("Enter URL Here. Don't login anywhere. "
                + "If playing a video, use the embedded URL instead.");
        urlField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    String url = urlField.getText();
                    if (url == null) {
                        return;
                    }
                    url = url.trim();
                    if (url.isEmpty()) {
                        return;
                    }
                    // enforce that only HTTPS connections are attempted
                    if (url.length() <= 8 ||
                        !(url.substring(0, 8).equalsIgnoreCase("https://"))) {
                        url = "https://" + url;
                    }
                    webEngine.load(url);
                }
            }
        });
        return urlField;
    }

    /*
    create a button that enable the user to navigate to the url in front
    (if there's one) of the user's current position in the browsing history
     */
    private Button createBrowsingForwardBtn() {
        Button forward = makeButton("forward.png");
        forward.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (currentHistoryPos >= historyList.size() - 1) {
                    return;
                }
                isBrowsingForward = true;
                String url = historyList.get(currentHistoryPos + 1);
                webEngine.load(url);
            }
        });
        return forward;
    }

    /*
    create a button that enable the user to navigate to the url behind
    (if there's one) of the user's current position in the browsing history
     */
    private Button createBrowsingBackBtn() {
        Button back = makeButton("back.png");
        back.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (currentHistoryPos <= 0) {
                    return;
                }
                isBrowsingBack = true;
                String url = historyList.get(currentHistoryPos - 1);
                webEngine.load(url);
            }
        });
        return back;
    }

    /*
    decide if the new url that has been successfully loaded should be
    synced, and whether it's part of the user's browsing forward or
    backward along his/her browsing history (or neither) and modify the
    browsing history accordingly
     */
    private void processNewURLLoaded(String newURL) {
        if (shouldSync) {
            fireBrowsingEvent(new BrowsingEvent(this, index,
                    newURL, isBrowsingForward, isBrowsingBack));
        } else {
            shouldSync = true;
        }

        if (!isBrowsingBack && !isBrowsingForward) {
            clearForwardHistory();
            ++currentHistoryPos;
            historyList.add(newURL);
        } else if (isBrowsingForward) {
            ++currentHistoryPos;
            isBrowsingForward = false;
        } else {
            --currentHistoryPos;
            isBrowsingBack = false;
        }
    }

    // alert the user that the web engine has failed to load a url
    private void alertWebLoadFailed() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Website Couldn't be Loaded");
        alert.setContentText("This's maybe because something happened \n"
                + "to the internet connection, or because secure\n"
                + "networking couldn't be established with this\n"
                + "website (i.e. HTTPS protocol not supported),\n"
                + "or because you have entered the url wrong."
        );
        alert.showAndWait();
    }

    /*
    listen to the web engine's load state and if the load is successful,
    then modify the web history and the display in urlField accordingly;
    alert the user if the load failed.
     */
    private void updatingFromWebEngineLoadState(TextField urlField) {
        webEngine.getLoadWorker().stateProperty().addListener(
                (ObservableValue<? extends Worker.State> observable,
                        Worker.State oldValue, Worker.State newValue) -> {
                    if (newValue == Worker.State.SUCCEEDED) {
                        String newURL = (String) (webEngine.locationProperty().getValue());
                        urlField.setText(newURL);
                        processNewURLLoaded(newURL);
                    } else if (newValue == Worker.State.FAILED) {
                        isBrowsingForward = false;
                        isBrowsingBack = false;
                        alertWebLoadFailed();
                    }
                }
        );
    }

    /*
    initialize and add components to fxPanel that contains the embedded
    browser
     */
    private void initFX(JFXPanel fxPanel) {
        root = new BorderPane();
        browser = new WebView();
        webEngine = browser.getEngine();
        TextField urlField = createURLField();
        updatingFromWebEngineLoadState(urlField);

        Button forward = createBrowsingForwardBtn();
        Button back = createBrowsingBackBtn();

        HBox navBtns = new HBox();
        navBtns.getChildren().addAll(back, forward);
        BorderPane navBar = new BorderPane();
        navBar.setLeft(navBtns);
        navBar.setCenter(urlField);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5);
        dropShadow.setOffsetX(3);
        dropShadow.setOffsetY(3);
        dropShadow.setColor(Color.GRAY);
        navBar.setEffect(dropShadow);

        root.setTop(navBar);
        root.setCenter(browser);
        root.setPrefSize(width, height);

        Scene scene = new Scene(root);
        fxPanel.setScene(scene);
    }

    /*
    navigate to the url in front of the user's current position in the 
    browsing history without syncing the browsing across the network
     */
    void browseForwardWithoutSync() {
        if (currentHistoryPos >= historyList.size() - 1) {
            return;
        }
        isBrowsingForward = true;
        String url = historyList.get(currentHistoryPos + 1);
        loadURLWithoutSync(url);

    }

    /*
    navigate to the url behind the user's current position in the 
    browsing history without syncing the browsing across the network
     */
    void browseBackWithoutSync() {
        if (currentHistoryPos <= 0) {
            return;
        }
        isBrowsingBack = true;
        String url = historyList.get(currentHistoryPos - 1);
        loadURLWithoutSync(url);
    }

    /*
    clear all of the url history in front of the user's current position
    in the browsing history
     */
    void clearForwardHistory() {
        int size = historyList.size();
        if (currentHistoryPos == -1 || currentHistoryPos >= size - 1) {
            return;
        }
        historyList.subList(currentHistoryPos + 1, size).clear();
    }

    // load url without syncing the browsing across the network
    void loadURLWithoutSync(String url) {
        shouldSync = false;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                webEngine.load(url);
            }
        });
    }

    /*
    load null without syncing; should use when the browsing panel is
    being closed
    */
    void endCurrentWebContent() {
        loadURLWithoutSync(null);
    }

    // fire browsing event e
    void fireBrowsingEvent(BrowsingEvent e) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == BrowsingListener.class) {
                ((BrowsingListener) listeners[i + 1]).
                        browsingEventOccured(e);
            }
        }
    }

    // add listener for browsing events
    void addBrowsingListener(BrowsingListener listener) {
        listenerList.add(BrowsingListener.class, listener);
    }

    // remove listener for browsing events
    void removeBrowsingListener(BrowsingListener listener) {
        listenerList.remove(BrowsingListener.class, listener);
    }

    // make and return button with icon from iconSource
    private Button makeButton(String iconSource) {
        Button btn = new Button();
        Image icon = new Image(getClass().getResourceAsStream(iconSource));
        ImageView iconView = new ImageView(icon);
        btn.setGraphic(iconView);
        return btn;
    }
}
