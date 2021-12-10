package fr.rader.boblite.utils;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class ReplayFileFilter extends FileFilter {

    // the first part of the description.
    // it should not contain any extensions
    private final String description;

    // the list of valid extensions
    private final String[] extensions;

    public ReplayFileFilter(String description, String[] extensions) {
        this.description = description;
        this.extensions = extensions;
    }

    @Override
    public boolean accept(File file) {
        // loop through all extensions
        for (String extension : extensions) {
            // check if the file is a file and
            // if it ends with a valid extension
            if (!file.isDirectory() && file.getName().endsWith(extension)) {
                // if it does, it's accepted
                return true;
            }
        }

        // if it doesn't end with a valid extension,
        // then we check if it's a directory
        return file.isDirectory();
    }

    @Override
    public String getDescription() {
        // we create a StringBuilder to create the description
        StringBuilder finalDescription = new StringBuilder(description + " (");

        // we loop through all the extensions to add
        // them in the description
        for (int i = 0; i < extensions.length; i++) {
            finalDescription
                    .append("*.")
                    .append(extensions[i])
                    // this will check if we're at the last extension.
                    // if we are, we write a ')' and not a comma
                    .append((i < extensions.length - 1) ? ", " : ")");
        }

        // return the description with all extensions
        return finalDescription.toString();
    }
}
