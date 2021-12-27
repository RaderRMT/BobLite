package fr.rader.boblite.listeners.selector;

import fr.rader.boblite.guis.ProjectSelector;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ProjectListSelectionListener implements ListSelectionListener {

    private final ProjectSelector projectSelector;

    public ProjectListSelectionListener(ProjectSelector projectSelector) {
        this.projectSelector = projectSelector;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        // we store true in the boolean if
        // the list of projects is not empty,
        // and false if it's empty
        boolean isListNotEmpty = ((JList<String>) e.getSource()).getModel().getSize() != 0;

        // then we enable/disable the buttons
        // depending on the boolean's value
        projectSelector.getOpenProjectButton().setEnabled(isListNotEmpty);
        projectSelector.getDeleteProjectButton().setEnabled(isListNotEmpty);
    }
}
