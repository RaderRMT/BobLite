package fr.rader.boblite;

import com.google.gson.Gson;
import fr.rader.boblite.utils.DataReader;
import fr.rader.boblite.utils.IO;
import fr.rader.boblite.utils.OS;
import fr.rader.boblite.utils.ReplayZip;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class ReplayData {

    private Map<String, Object> metaData = new HashMap<>();

    private final ReplayZip replayZip;
    private final File project;
    private final Main main;

    private File mcprFile;

    public ReplayData(File project, Main main) {
        this.project = project;
        this.main = main;

        boolean alreadyHasReplay = false;
        for (File file : project.listFiles()) {
            if (file.getName().endsWith(".mcpr")) {
                alreadyHasReplay = true;
                mcprFile = file;
            }
        }

        if (!alreadyHasReplay) {
            mcprFile = IO.openFilePrompt(OS.getMinecraftFolder() + "replay_recordings/", "Replay File", "mcpr");
        }

        if (mcprFile == null) {
            System.out.println("No Replay selected, stopping.");
            System.exit(0);
        }

        File oldMcprFile = new File(project.getAbsolutePath() + "/" + this.mcprFile.getName());
        if (!alreadyHasReplay) {
            try {
                Files.copy(this.mcprFile.toPath(), oldMcprFile.toPath());
            } catch (IOException ignored) {}

            mcprFile = oldMcprFile;
        }

        replayZip = new ReplayZip(mcprFile);
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
                !replayZip.hasEntry("recording.tmcpr.crc32") ||
                !metaData.containsKey("serverName") ||
                !((String) metaData.get("generator")).startsWith("ReplayMod")) {
            stopBob();
        }
    }

    private void stopBob() {
        JOptionPane.showMessageDialog(null, "Badlion/Lunar Replay detected, stopping Bob.\nPlease use the official ReplayMod.");

        // clear last opened project and delete files
        main.getProjects().removeProject(project.getName());
        main.getProjects().saveProjects();

        System.exit(0);
    }

    public File getMcprFile() {
        return mcprFile;
    }

    public Object getMetaData(String key) {
        return metaData.get(key);
    }

    public int getProtocolVersion() {
        return (int) ((double) metaData.get("protocol"));
    }

    public ReplayZip getReplayZip() {
        return replayZip;
    }
}
