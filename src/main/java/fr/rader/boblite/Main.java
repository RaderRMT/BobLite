package fr.rader.boblite;

import fr.rader.boblite.guis.Menu;
import fr.rader.boblite.guis.ProgressBar;
import fr.rader.boblite.guis.ProjectSelector;
import fr.rader.boblite.utils.*;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class Main {

    private Projects projects;

    public void start() {
        // get the projects list
        projects = new Projects();

        // open the project selector
        ProjectSelector selector = new ProjectSelector(projects);
        // get the project name from the selector
        String projectName = selector.createWindow();

        // we quit the program if the file is null (if we closed the window)
        if (projectName == null) {
            return;
        }

        // get the project file from the project name
        File project = new File(OS.getBobFolder() + "projects/" + projectName);

        // get the replay data from the project file
        ReplayData replayData = new ReplayData(project, this);

        // here, we get the correct packet id depending on the protocol version
        int timePacketID;
        int weatherPacketID;
        switch (replayData.getProtocolVersion()) {
            // 1.16.x
            case 735:
            case 736:
            case 751:
            case 753:
            case 754:
                timePacketID = 0x4E;
                weatherPacketID = 0x1D;
                break;

            // 1.17.x
            case 755:
            case 756:
                timePacketID = 0x58;
                weatherPacketID = 0x1E;
                break;

            // 1.18(.1)
            case 757:
                timePacketID = 0x59;
                weatherPacketID = 0x1E;
                break;

            // we show an error and stop if the protocol isn't supported
            default:
                JOptionPane.showMessageDialog(null, "Error: unsupported protocol: " + replayData.getProtocolVersion() + ", Minecraft version: " + replayData.getMetaData("mcversion"));
                return;
        }

        // we then show the edit menu
        Menu menu = new Menu();
        menu.show();

        // if the user doesn't want to edit their replay,
        // we stop bob
        if (!menu.wasGoPressed()) {
            return;
        }

        try {
            // get the mcpr file as a zip file
            ReplayZip replayZip = replayData.getReplayZip();
            // we open the recording.tmcpr file in a data reader
            DataReader reader = new DataReader(replayZip.getEntry("recording.tmcpr"));
            // we also create a data writer to write the modified packets
            // or any packets that we don't want to edit
            DataWriter writer = new DataWriter();

            // getting the replay duration for the progress bar
            Double duration = (Double) replayData.getMetaData("duration");
            // creating the progress bar and setting the action text
            ProgressBar progressBar = new ProgressBar(duration.intValue());
            progressBar.setActionText("Editing the replay...");
            // showing the progress bar
            progressBar.show();

            // iterate while we still have some data in the reader
            while (reader.getLength() != 0) {
                // get the timestamp, the size and the packet id
                int timestamp = reader.readInt();
                int size = reader.readInt();
                int packetID = reader.readVarInt();

                // changing the progress bar value
                progressBar.setProgressBarValue(timestamp);

                // check if the packet id is a time packet,
                // and if the changeTime checkbox is checked
                if(menu.isChangeTimeChecked() && packetID == timePacketID) {
                    // writing packet header
                    writer.writeInt(timestamp);
                    writer.writeInt(size);
                    writer.writeVarInt(packetID);

                    // writing world age
                    writer.writeLong(reader.readLong());
                    // writing new time
                    writer.writeLong(menu.getNewTime());

                    // skip the next 8 bytes (old world time)
                    reader.skip(8);

                    continue;
                }

                // check if the packet id is a weather packet,
                // and if the removeRain checkbox is checked
                if (menu.isRemoveRainChecked() && packetID == weatherPacketID) {
                    // get the reason, we don't want to ignore it because
                    // this packet isn't always used for the weather
                    int reason = reader.readByte();

                    // reason == 1 -> end raining
                    // reason == 2 -> begin raining
                    // reason == 7 -> rain level change
                    // reason == 8 -> thunder level change
                    // iirc reason 1 and 2 are swapped
                    if (reason != 1 && reason != 2 && reason != 7 && reason != 8) {
                        // if the packet does not change the rain, we write it
                        // packet header
                        writer.writeInt(timestamp);
                        writer.writeInt(size);
                        writer.writeVarInt(packetID);

                        // packet content
                        writer.writeByte(reason);
                        writer.writeFloat(reader.readFloat());
                    } else {
                        // we skip the next 4 bytes (new rain/thunder level)
                        reader.skip(4);
                    }

                    continue;
                }

                // we write the packet timestamp, size, packet id
                writer.writeInt(timestamp);
                writer.writeInt(size);
                writer.writeVarInt(packetID);
                // and finally, we write the packet data
                writer.writeByteArray(reader.readFollowingBytes(size - 1));
            }

            // we then flush the writer
            writer.flush();

            // changing the progress bar action text,
            // as we're now doing something else
            progressBar.setActionText("Writing the replay, this can take a while...");

            // we open the zip file, write the recording.tmcpr file and close the zip
            replayZip.open();
            replayZip.addFile(writer.getInputStream(), "recording.tmcpr");
            replayZip.close();

            // kill the progress bar window as it's now useless
            progressBar.kill();

            // then open the file explorer to the folder that contains the replay
            IO.openFileExplorer(replayData.getMcprFile().getParentFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
