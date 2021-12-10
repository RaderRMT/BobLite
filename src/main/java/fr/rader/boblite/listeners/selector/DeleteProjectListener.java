package fr.rader.boblite.listeners.selector;

import fr.rader.boblite.Projects;
import fr.rader.boblite.guis.ProjectSelector;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DeleteProjectListener implements ActionListener {

    private final ProjectSelector projectSelector;
    private final Projects projects;

    public DeleteProjectListener(ProjectSelector projectSelector) {
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

            // if the user selected a project to delete:
            // we first remove it from the projects file
            projects.removeProject(selectedProject);
            // we then save it to the projects.nbt file
            projects.saveProjects();
            // and finally we update the projects list
            projectSelector.updateList();
        }
    }
}
