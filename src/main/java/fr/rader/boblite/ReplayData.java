package fr.rader.boblite;

import com.google.gson.Gson;
import fr.rader.boblite.utils.DataReader;
import fr.rader.boblite.utils.ReplayZip;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReplayData {

    private Map<String, Object> metaData = new HashMap<>();

    private final ReplayZip replayZip;
    private final File mcprFile;

    private final Main main;

    public ReplayData(File mcprFile, Main main) throws NullPointerException {
        if (mcprFile == null) {
            throw new NullPointerException("The mcpr file cannot be null");
        }

        if (main == null) {
            throw new NullPointerException("main is null");
        }

        this.mcprFile = mcprFile;
        this.main = main;

        this.replayZip = new ReplayZip(mcprFile);
        readMetaData();
        checkUnofficialReplays();
    }

    private void readMetaData() {
        try {
            DataReader reader = new DataReader(replayZip.getEntry("metaData.json"));

            String readerMetaData = null;
            try {
                readerMetaData = reader.readString(reader.getLength());
            } catch (IOException e) {
                e.printStackTrace();
            }

            reader.close();

            if (readerMetaData != null) {
                metaData = new Gson().fromJson(readerMetaData, Map.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkUnofficialReplays() {
        if (replayZip.hasEntry("badlion.json") ||
                !replayZip.hasEntry("mods.json") ||
                !((String) metaData.get("generator")).startsWith("ReplayMod")) {
            stopBob();
        }
    }

    private void stopBob() {
        JOptionPane.showMessageDialog(null,  mcprFile.getName() + "\nBadlion/Lunar Replay detected, stopping Bob.\nPlease use the official ReplayMod.");

        // clear last opened project and delete files
        main.getProjects().removeProject(main.getProjectName());
        main.getProjects().saveProjects();

        System.exit(0);
    }

    public Object getMetaData(String key) {
        return metaData.get(key);
    }

    public ReplayZip getReplayZip() {
        return replayZip;
    }
}
