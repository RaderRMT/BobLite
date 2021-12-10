package fr.rader.boblite.listeners.selector;

import fr.rader.boblite.Projects;
import fr.rader.boblite.guis.ProjectSelector;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NewProjectListener implements ActionListener {

    private final ProjectSelector projectSelector;
    private final Projects projects;

    public NewProjectListener(ProjectSelector projectSelector) {
        this.projectSelector = projectSelector;
        this.projects = projectSelector.getProjects();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // ask for the project name when creating a new project
        String name = JOptionPane.showInputDialog(projectSelector.getDialog(), "Project name:");

        // if the project name is not null (so if the user didn't cancel),
        // if the name isn't empty and if the name length is less than 65535 chars,
        if (name != null && !name.isEmpty() && name.length() <= 0xffff) {
            // we add the project to the projects list
            projects.addProject(name);
            // we save the projects.nbt with the new project
            projects.saveProjects();
            // and we update the projects list
            projectSelector.updateList();
        }
    }
}
