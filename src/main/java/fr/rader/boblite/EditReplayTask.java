package fr.rader.boblite;

import fr.rader.boblite.guis.Menu;
import fr.rader.boblite.guis.ProgressBar;
import fr.rader.boblite.utils.DataReader;
import fr.rader.boblite.utils.DataWriter;
import fr.rader.boblite.utils.ReplayZip;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class EditReplayTask implements Runnable {

    private final ReplayData replayData;
    private final File tempFileDirectory;
    private final boolean removeChat;
    private final boolean changeTime;
    private final int newTime;
    private final boolean removeRain;

    public EditReplayTask(ReplayData replayData, File tempFileDirectory, Menu menu) throws NullPointerException {
        if(replayData == null) throw new NullPointerException("replayData is null");
        if(tempFileDirectory == null) throw new NullPointerException("tempFileDirectory is null");
        if(menu == null) throw new NullPointerException("menu is null");

        this.replayData = replayData;
        this.tempFileDirectory = tempFileDirectory;
        this.removeChat = menu.isRemoveChatChecked();
        this.changeTime = menu.isChangeTimeChecked();
        this.newTime = menu.getNewTime();
        this.removeRain = menu.isRemoveRainChecked();
    }



    @Override
    public void run() {
        // here, we get the correct packet id depending on the protocol version
        int timePacketID;
        int weatherPacketID;
        int chatPacketID;
        switch ((String) replayData.getMetaData("mcversion")) {
            case "1.8":
            case "1.8.1":
            case "1.8.2":
            case "1.8.3":
            case "1.8.4":
            case "1.8.5":
            case "1.8.6":
            case "1.8.7":
            case "1.8.8":
            case "1.8.9":
                timePacketID = 0x03;
                weatherPacketID = 0x2B;
                chatPacketID = 0x02;
                break;

            case "1.9":
            case "1.9.1":
            case "1.9.2":
            case "1.9.3":
            case "1.9.4":
            case "1.10":
            case "1.10.1":
            case "1.10.2":
            case "1.11":
            case "1.11.1":
            case "1.11.2":
                timePacketID = 0x44;
                weatherPacketID = 0x1E;
                chatPacketID = 0x0F;
                break;

            case "1.12":
            case "1.12.1":
            case "1.12.2":
                timePacketID = 0x47;
                weatherPacketID = 0x1E;
                chatPacketID = 0x0F;
                break;

            case "1.14":
            case "1.14.1":
            case "1.14.2":
            case "1.14.3":
            case "1.14.4":
                timePacketID = 0x4E;
                weatherPacketID = 0x1E;
                chatPacketID = 0x0E;
                break;

            case "1.15":
            case "1.15.1":
            case "1.15.2":
                timePacketID = 0x4F;
                weatherPacketID = 0x1F;
                chatPacketID = 0x0F;
                break;

            case "1.16":
            case "1.16.1":
            case "1.16.2":
            case "1.16.3":
            case "1.16.4":
            case "1.16.5":
                timePacketID = 0x4E;
                weatherPacketID = 0x1D;
                chatPacketID = 0x0E;
                break;

            case "1.17":
            case "1.17.1":
                timePacketID = 0x58;
                weatherPacketID = 0x1E;
                chatPacketID = 0x0F;
                break;

            case "1.18":
            case "1.18.1":
                timePacketID = 0x59;
                weatherPacketID = 0x1E;
                chatPacketID = 0x0F;
                break;

            // we show an error and stop if the protocol isn't supported
            default:
                JOptionPane.showMessageDialog(null, "Error: unsupported Minecraft version: " + replayData.getMetaData("mcversion"));
                return;
        }

        try {
            // get the mcpr file as a zip file
            ReplayZip replayZip = replayData.getReplayZip();
            // we open the recording.tmcpr file in a data reader
            DataReader reader = new DataReader(replayZip.getEntry("recording.tmcpr"));
            // we also create a data writer to write the modified packets
            // or any packets that we don't want to edit
            DataWriter writer = new DataWriter(this.tempFileDirectory);

            writer.writeInt(reader.readInt());
            int length = reader.readInt();
            writer.writeInt(length);
            writer.writeByteArray(reader.readFollowingBytes(length));

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

                // check if the packet id is a chat message packet,
                // and if the removeChat checkbox is checked
                if (this.removeChat && packetID == chatPacketID) {
                    // if bob has to remove the chat packet,
                    // we skip it, and we don't write it
                    reader.skip(size - 1);
                    continue;
                }

                // check if the packet id is a time packet,
                // and if the changeTime checkbox is checked
                if (this.changeTime && packetID == timePacketID) {
                    // writing packet header
                    writer.writeInt(timestamp);
                    writer.writeInt(size);
                    writer.writeVarInt(packetID);

                    // writing world age
                    writer.writeLong(reader.readLong());
                    // writing new time
                    writer.writeLong(this.newTime);

                    // skip the next 8 bytes (old world time)
                    reader.skip(8);

                    continue;
                }

                // check if the packet id is a weather packet,
                // and if the removeRain checkbox is checked
                if (this.removeRain && packetID == weatherPacketID) {
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

            writer.clear();

            // kill the progress bar window as it's now useless
            progressBar.kill();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
