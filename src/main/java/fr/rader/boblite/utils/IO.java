package fr.rader.boblite.utils;

import fr.rader.boblite.nbt.NBTCompound;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class IO {

    private static final int BUFFER_SIZE = 1024;

    /**
     * Open a JFileChooser at the given path, and show a description
     * with the given valid extensions
     *
     * @param path path to where the JFileChooser is opening to
     * @param description a description of the files we want
     * @param extensions a list of valid file extensions
     * @return {@link File} - if the user selected a file and validated<br>
     *         {@code null} - otherwise
     */
    public static File openFilePrompt(String path, String description, String... extensions) {
        JFileChooser fileChooser = new JFileChooser(path);
        // i don't know why this is here and i'm too scared to remove it
        fileChooser.setAcceptAllFileFilterUsed(false);
        // disable the ability to select multiple files
        fileChooser.setMultiSelectionEnabled(false);
        // set a file filter to only see files with the given extensions
        fileChooser.setFileFilter(new ReplayFileFilter(description, extensions));

        // show the file chooser to the user and wait for an answer
        int option = fileChooser.showOpenDialog(null);
        // if the user selected a file,
        // we then return it
        if (option == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }

        // if they cancelled, we return false
        return null;
    }

    public static void writeNBTFile(File destination, NBTCompound compound) {
        // create a data writer to write the compound to an input stream
        DataWriter writer = new DataWriter();

        // write the compound to the data writer
        compound.writeNBT(writer);

        // write the data writer's data to the destination file
        writeFile(destination, writer.getInputStream());
    }

    public static void writeFile(File destination, InputStream inputStream) {
        try {
            // open a file output stream writing to destination
            FileOutputStream outputStream = new FileOutputStream(destination);

            // this is the amount of byte we read from the input stream
            int length;
            // this is our buffer, where our data will temporally be
            byte[] buffer = new byte[BUFFER_SIZE];
            // while we still have bytes in our buffer
            while ((length = inputStream.read(buffer)) > 0) {
                // we write them to the file output stream,
                // effectively writing to the file.
                outputStream.write(buffer, 0, length);
            }

            // flushing the toilets and closing the door
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteDirectory(File directory) {
        // checking if the directory exists
        // and if it is indeed a directory
        if (directory.exists() && directory.isDirectory()) {
            // if it is a directory and it exists,
            // we then get all the files it contains
            File[] files = directory.listFiles();

            // if the directory does not exist, we return
            if (files == null) {
                return;
            }

            // for each file in our files array
            for (File file : files) {
                // we check if it's a directory
                if (file.isDirectory()) {
                    // if it is, we recursively call this method
                    // to delete the folder
                    deleteDirectory(file);
                } else {
                    // if it is a file, we delete it
                    if (!file.delete()) {
                        // if we can't delete the file,
                        // we'll just print a nice message in the console
                        System.out.println("Couldn't delete file: " + file.getAbsolutePath());
                    }
                }
            }
        }

        // then delete the directory
        if (!directory.delete()) {
            // if we can't delete the directory,
            // we'll just print a nice message in the console
            System.out.println("Couldn't delete folder: " + directory.getAbsolutePath());
        }
    }

    public static void openFileExplorer(File file) {
        try {
            // check if the desktop is supported
            if (Desktop.isDesktopSupported()) {
                // if it is, we open the file in the explorer
                Desktop.getDesktop().open(file);
            } else {
                // if it isn't, we print a nice message in the console
                System.out.println("Desktop is not supported");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
