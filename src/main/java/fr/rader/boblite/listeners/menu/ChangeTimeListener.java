package fr.rader.boblite.listeners.menu;

import fr.rader.boblite.guis.Menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChangeTimeListener implements ActionListener {

    private final Menu menu;

    public ChangeTimeListener(Menu menu) {
        this.menu = menu;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // set the newTimeSpinner enabled state
        // to the changeTimeCheckBox selected state.
        // if the checkbox is checked, the new time spinner will be enabled,
        // if the checkbox is unchecked, the new time spinner will be disabled
        menu.getNewTimeSpinner().setEnabled(menu.isChangeTimeChecked());
    }
}
