package fr.rader.boblite.listeners.selector;

import fr.rader.boblite.Projects;
import fr.rader.boblite.guis.ProjectSelector;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OpenProjectListener implements ActionListener {

    private final ProjectSelector projectSelector;
    private final Projects projects;

    public OpenProjectListener(ProjectSelector projectSelector) {
        this.projectSelector = projectSelector;
        this.projects = projectSelector.getProjects();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // check that we have at least one project in the projects list
        if (projects.getProjectsFiles().size() > 0) {
            // get the selected project
            String selectedProject = projectSelector.getProjectsList().getSelectedValue();

            // if the project is null (if no project has been selected),
            // we return because there's nothing we have to do
            if (selectedProject == null) {
                return;
            }

            // we set the current project to the selected project
            projectSelector.setProject(selectedProject);
            // and we dispose of this dialog
            // because we don't need it anymore
            projectSelector.getDialog().dispose();
        }
    }
}
