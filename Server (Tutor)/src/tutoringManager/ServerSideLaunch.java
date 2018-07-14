package tutoringManager;

import java.io.IOException;
import javax.swing.JFrame;

public class ServerSideLaunch {

    public static void main(String[] args) throws IOException {
        TutoringController tutorController = new TutoringController();
        tutorController.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        tutorController.begin();
    }
}
