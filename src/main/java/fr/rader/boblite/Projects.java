package fr.rader.boblite;

import fr.rader.boblite.nbt.NBTBase;
import fr.rader.boblite.nbt.NBTCompound;
import fr.rader.boblite.nbt.NBTList;
import fr.rader.boblite.nbt.NBTString;
import fr.rader.boblite.utils.DataReader;
import fr.rader.boblite.utils.IO;
import fr.rader.boblite.utils.OS;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Projects {

    // The projects file, this holds a list of Bob projects in NBT format
    private final File PROJECTS_FILE = new File(OS.getBobFolder() + "projects/projects.nbt");

    // The list of Bob projects
    private final List<String> projects = new ArrayList<>();

    public Projects() {
        // create the "projects" folder if it does not already exist
        if (!PROJECTS_FILE.getParentFile().exists()) {
            PROJECTS_FILE.getParentFile().mkdirs();
        }

        // if the projects.nbt file does not exist,
        // we save the projects to create it
        if (!PROJECTS_FILE.exists()) {
            saveProjects();
        }

        // we read the projects from the projects.nbt file
        readProjects();
    }

    public void saveProjects() {
        // create the compound and list that will hold our projects
        NBTCompound tag = new NBTCompound("").addList("projects", new NBTList("projects", 0x08));

        // we get the list from the compound tag
        // this allows bob to be a little faster because it does not
        // have to get the list in the for loop
        NBTList list = tag.getComponent("projects").getAsList();

        // add all the projects in the list tag
        for (String name : projects) {
            list.addString(name);
        }

        // write the compound tag to the projects.nbt file
        IO.writeNBTFile(PROJECTS_FILE, tag);
    }

    private void readProjects() {
        try {
            // create a data reader to read PROJECTS_FILE
            DataReader reader = new DataReader(PROJECTS_FILE);

            // get the project list from the nbt compound
            NBTList tag = reader.readNBT().getComponent("projects").getAsList();
            // for each element in the projects list
            for (NBTBase base : tag.getComponents()) {
                // quick check to see if the tag is the correct tag
                if (base instanceof NBTString) {
                    // if the tag is a string, we add it to the projects list
                    addProject(base.getAsString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addProject(String projectName) {
        // check if the project doesn't already exist
        if (!projects.contains(projectName)) {
            // if it doesn't, we add it to the list
            projects.add(projectName);

            // and we create its folders
            initFolders(projectName);
        } else {
            // if it does exist, then we show a nice error to the user
            JOptionPane.showMessageDialog(null, "A project already exists with the name \"" + projectName + "\"");
        }
    }

    public void removeProject(String projectName) {
        // check if the project exist
        if (projects.contains(projectName)) {
            // if it does, we remove it from the list
            projects.remove(projectName);

            // and we delete its directory
            IO.deleteDirectory(new File(OS.getBobFolder() + "projects/" + projectName));
        } else {
            // if it doesn't exist, then we show a nice error to the user
            JOptionPane.showMessageDialog(null, "The project \"" + projectName + "\" does not exists");
        }
    }

    private void initFolders(String projectName) {
        // we get the project folder from the project name
        String projectFolder = OS.getBobFolder() + "projects/" + projectName;

        // we check if the file doesn't exist
        File file = new File(projectFolder);
        if (!file.exists()) {
            // if it doesn't, we create the folder
            file.mkdirs();
        }
    }

    public List<String> getProjectsNames() {
        return projects;
    }

    public List<File> getProjectsFiles() {
        List<File> out = new ArrayList<>();

        for (String name : projects) {
            out.add(new File(OS.getBobFolder() + "projects/" + name));
        }

        return out;
    }
}
