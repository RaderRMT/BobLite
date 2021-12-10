package fr.rader.boblite.listeners.menu;

import fr.rader.boblite.guis.Menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GoListener implements ActionListener {

    private final Menu menu;

    public GoListener(Menu menu) {
        this.menu = menu;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // change the state of the goPressed boolean
        menu.setGoPressed(true);
        // dispose of the dialog because
        // we don't need it anymore
        menu.getDialog().dispose();
    }
}
