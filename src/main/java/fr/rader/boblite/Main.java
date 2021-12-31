package fr.rader.boblite;

import fr.rader.boblite.guis.Menu;
import fr.rader.boblite.guis.ProjectSelector;
import fr.rader.boblite.utils.*;

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

public class Main {

    public static final Image BOB_LOGO = new ImageIcon(Main.class.getResource("/bob_logo.png")).getImage();

    private Projects projects;
    private String projectName;

    public void start() {
        // get the projects list
        projects = new Projects();

        // open the project selector
        ProjectSelector selector = new ProjectSelector(projects);
        // get the project name from the selector
        this.projectName = selector.createWindow();

        // we quit the program if the file is null (if we closed the window)
        if (this.projectName == null) {
            return;
        }

        // get the project folder from the project name
        File projectFolder = new File(OS.getBobFolder() + "projects/" + this.projectName);

        File[] projectFiles = projectFolder.listFiles();

        if (projectFiles == null) {
            System.out.println("'" + projectFolder.getAbsolutePath() + "' is not a valid path.");
            System.exit(0);
        }

        // Load .mcpr files from the project folder.
        List<ReplayData> replays = new ArrayList<ReplayData>();
        for (File file : projectFiles) {
            if (file.getName().endsWith(".mcpr") && !file.isDirectory()) {
                replays.add(
                        new ReplayData(
                        file,
                        projectFolder,
                        this)
                );
            }
        }

        // If no .mcpr files existed in the project folder, ask the user to select one or more .mcpr files.
        if(replays.isEmpty()) {

            File[] files = IO.openFilePrompt(OS.getMinecraftFolder() + "replay_recordings/", "Replay File", "mcpr");

            if(files != null) {
                for (File file : files) {
                    // It should be impossible for non .mcpr files to be returned, but I feel like wasting a few more CPU cycles just to be safe.
                    if (file.getName().endsWith(".mcpr") && !file.isDirectory()) {
                        File newFile = new File(projectFolder.getAbsolutePath() + "/" + file.getName());
                        try {
                            Files.copy(file.toPath(), newFile.toPath());
                            replays.add(
                                    new ReplayData(
                                    file,
                                    projectFolder,
                                    this)
                            );
                        } catch (IOException exception) {
                            // If there is a problem, just stop.
                            System.out.println("Failed to copy file from " + file.getAbsolutePath().toString() + " to " + newFile.getAbsolutePath().toString());
                            System.exit(0);
                        }
                    }
                }
            }
            // If the user didn't select any files, exit.
            if(replays.isEmpty()) {
                System.out.println("No Replays selected, stopping.");
                System.exit(0);
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

        // Ask the user where temporary file(s) should be located
        File tempFileDirectory = IO.saveFilePrompt(null);

        if (tempFileDirectory == null) {
            System.exit(0);
        }

        // Okay, time to get working!

        // Get the total number of system CPU threads.
        int totalSystemThreadNumber = Runtime.getRuntime().availableProcessors();
        int executorThreadNumber;
        if (replays.size() < totalSystemThreadNumber) {
            executorThreadNumber = replays.size();
        } else {
            // Leave at least one system thread idle, don't make the computer unusable.
            executorThreadNumber = totalSystemThreadNumber - 1;
            // If for whatever reason the computer only has one CPU core/thread, just use one thread.
            if(executorThreadNumber < 1) {
                executorThreadNumber = 1;
            }
        }

        System.out.println("Using " + executorThreadNumber + " thread(s) for " + replays.size() + " task(s)...");

        ExecutorService executor = Executors.newFixedThreadPool(executorThreadNumber);

        long startTime = System.currentTimeMillis();

        // Submit all tasks.
        for(ReplayData replay : replays){
            executor.submit(
                    new EditReplayTask(
                    replay,
                    tempFileDirectory,
                    menu)
            );
        }

        // Wait for all task to be completed.
        executor.shutdown();
        try {
            // Timeout doesn't really matter in this use case as we will want to wait for all tasks to complete before terminating.
            executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException exception) {
            // This should never happen.
            System.out.println("Interrupted while waiting for all tasks to complete.");
            executor.shutdownNow();
            exception.printStackTrace();
            System.exit(0);
        }


        long finishTime = System.currentTimeMillis();
        System.out.println("Completed " + replays.size() + " task(s) in " + (int)(((finishTime - startTime) / 1000) / 60) + "m " + (int)(((finishTime - startTime) / 1000) % 60) + "s");
        System.out.println("File(s) located at " + projectFolder.getAbsolutePath().toString());

        // Open the file explorer where the replay(s) are saved.
        IO.openFileExplorer(projectFolder);
    }

    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        try {
            // set the app to use the system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (IllegalAccessException | ClassNotFoundException | UnsupportedLookAndFeelException | InstantiationException e) {
            e.printStackTrace();
        }

        // start bob
        start();
    }

    public Projects getProjects() {
        return projects;
    }
    public String getProjectName() {
        return this.projectName;
    }

}
