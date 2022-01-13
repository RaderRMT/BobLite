package fr.rader.boblite;

import fr.rader.boblite.guis.Menu;
import fr.rader.boblite.guis.ProjectSelector;
import fr.rader.boblite.utils.IO;
import fr.rader.boblite.utils.OS;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BobLite {

    public static final Image BOB_LOGO = new ImageIcon(Main.class.getResource("/bob_logo.png")).getImage();

    private final Projects projects;

    private String projectName;

    public BobLite() {
        // get the projects list
        this.projects = new Projects();
    }

    void start() {
        // open the project selector
        ProjectSelector selector = new ProjectSelector(projects);
        // get the project name from the selector
        projectName = selector.createWindow();

        // we quit the program if the file is null (if we closed the window)
        if (projectName == null) {
            return;
        }

        // get the project folder from the project name
        File projectFolder = new File(OS.getBobFolder() + "projects/" + projectName);

        // get all the files in the project folder
        File[] projectFiles = projectFolder.listFiles();
        // if projectsFiles is null, that means
        // the project folder doesn't exist, so we exit
        if (projectFiles == null) {
            System.out.println("'" + projectFolder.getAbsolutePath() + "' is not a valid path.");
            System.exit(0);
        }

        // load .mcpr files from the project folder
        List<ReplayData> replays = new ArrayList<>();
        // we loop through all the files
        for (File file : projectFiles) {
            // we check if it's a file and has the correct extension
            if (!file.isDirectory() && file.getName().endsWith(".mcpr")) {
                // if it's a file with the correct extension,
                // we add it to the list of replays
                replays.add(
                        new ReplayData(
                                file,
                                this
                        )
                );
            }
        }

        // if no .mcpr files existed in the project folder, ask the user to select one or more .mcpr files.
        if (replays.isEmpty()) {
            File[] files = IO.openFilePrompt(OS.getMinecraftFolder() + "replay_recordings/", "Replay File", "mcpr");

            // if the user didn't select any files, exit.
            if (files == null) {
                System.out.println("No Replays selected, stopping.");
                System.exit(0);
            }

            // we loop through all the files the user selected
            for (File file : files) {
                File newFile = new File(projectFolder.getAbsolutePath() + "/" + file.getName());

                try {
                    // we copy the replay to the projects folder
                    Files.copy(
                            file.toPath(),
                            newFile.toPath()
                    );

                    // and we add the replay to the replays list
                    replays.add(
                            new ReplayData(
                                    newFile,
                                    this
                            )
                    );
                } catch (IOException exception) {
                    // if there is a problem, we send an error
                    System.out.println("Failed to copy file from " + file.getAbsolutePath() + " to " + newFile.getAbsolutePath());
                    // we print the exception
                    exception.printStackTrace();

                    // and we exit
                    System.exit(0);
                }
            }
        }

        // we then show the edit menu
        Menu menu = new Menu();
        menu.show();

        // if the user doesn't want to edit their replay,
        // we stop bob
        if (!menu.wasGoPressed()) {
            return;
        }

        // ask the user where temporary file(s) should be located
        File tempFileDirectory = IO.saveFilePrompt(null);
        // if no folder is given, we exit
        if (tempFileDirectory == null) {
            System.exit(0);
        }

        // Okay, time to get working!

        // we get the total number of system CPU threads.
        int totalSystemThreadNumber = Runtime.getRuntime().availableProcessors();

        int executorThreadNumber =
                (replays.size() < totalSystemThreadNumber)  // if we have less replays than threads
                        ?
                replays.size()                              // we set the number of threads to use to the number of replays we have to edit
                        :
                totalSystemThreadNumber - 1;                // else, we use all the threads - 1 to not make the computer unusable

        // if for whatever reason the computer only has one CPU core/thread, just use one thread.
        if (executorThreadNumber < 1) {
            executorThreadNumber = 1;
        }

        System.out.println("Using " + executorThreadNumber + " thread(s) for " + replays.size() + " task(s)...");

        // we create a thread pool for our edit replay tasks
        ExecutorService executor = Executors.newFixedThreadPool(executorThreadNumber);

        // we get the start of execution, to calculate
        // how long it took bob to edit the replays
        long startTime = System.currentTimeMillis();

        // we loop through all of our replays
        for (ReplayData replay : replays) {
            // and we submit them to
            // the executor to be executed
            executor.submit(
                    new EditReplayTask(
                            replay,
                            tempFileDirectory,
                            menu
                    )
            );
        }

        // wait for all task to be completed.
        executor.shutdown();

        try {
            // timeout doesn't really matter in this use case as we will want to wait for all tasks to complete before terminating.
            executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException exception) {
            // This should never happen.
            // just in case, we shutdown the executor
            executor.shutdownNow();

            // we print an error and the stacktrace
            System.out.println("Interrupted while waiting for all tasks to complete.");
            exception.printStackTrace();

            // we also show a message to the user
            JOptionPane.showMessageDialog(null,  "Error:\nInterrupted while waiting for all tasks to complete.");
            // and we exit
            System.exit(0);
        }

        // we calculate the total execution time
        long totalTime = (System.currentTimeMillis() - startTime) / 1000;
        // print the total execution time in human-readable format
        System.out.println("Completed " + replays.size() + " task(s) in " + (totalTime / 60) + "m " + (totalTime % 60) + "s");
        System.out.println("File(s) located at " + projectFolder.getAbsolutePath());

        // Open the file explorer where the replay(s) are saved.
        IO.openFileExplorer(projectFolder);
    }

    public Projects getProjects() {
        return projects;
    }

    public String getProjectName() {
        return projectName;
    }
}
