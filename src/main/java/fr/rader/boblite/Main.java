package fr.rader.boblite;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        try {
            // set the app to use the system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (IllegalAccessException
                | ClassNotFoundException
                | UnsupportedLookAndFeelException
                | InstantiationException e) {
            e.printStackTrace();
        }

        BobLite bobLite = new BobLite();
        bobLite.start();
    }
}
