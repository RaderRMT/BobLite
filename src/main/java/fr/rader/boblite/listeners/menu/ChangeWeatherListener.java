package fr.rader.boblite.listeners.menu;

import fr.rader.boblite.guis.Menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChangeWeatherListener implements ActionListener {

    private final Menu menu;

    public ChangeWeatherListener(Menu menu) {
        this.menu = menu;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // set the newWeatherComboBox enabled state
        // to the changeWeatherCheckBox selected state.
        // if the checkbox is checked, the new weather combo box will be enabled,
        // if the checkbox is unchecked, the new weather combo box will be disabled
        menu.getNewWeatherComboBox().setEnabled(menu.isChangeWeatherChecked());
    }
}
