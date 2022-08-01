package fr.rader.boblite;

import fr.rader.boblite.guis.Menu;
import fr.rader.boblite.guis.ProgressBar;
import fr.rader.boblite.utils.DataReader;
import fr.rader.boblite.utils.DataWriter;
import fr.rader.boblite.utils.ReplayZip;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class EditReplayTask implements Runnable {

    private final File tempFileDirectory;

    private final ReplayData replayData;

    private final boolean removeChat;
    private final boolean changeTime;
    private final boolean changeWeather;

    private final int newTime;
    private String newWeather;

    public EditReplayTask(ReplayData replayData, File tempFileDirectory, Menu menu) throws NullPointerException {
        this.tempFileDirectory = tempFileDirectory;

        this.replayData = replayData;

        this.removeChat = menu.isRemoveChatChecked();
        this.changeTime = menu.isChangeTimeChecked();
        this.changeWeather = menu.isChangeWeatherChecked();

        this.newTime = menu.getNewTime();
        this.newWeather = menu.getSelectedNewWeather();
    }

    @Override
    public void run() {
        // we get the minecraft version from the replay's metadata
        String minecraftVersion = (String) this.replayData.getMetaData("mcversion");

        // here, we get the correct packet id depending on the minecraft version
        int timePacketID = 0;
        int weatherPacketID = 0;
        int spawnPositionID = 0;
        int chatPacketID = 0;
        switch (minecraftVersion) {
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
                spawnPositionID = 0x05;
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
                spawnPositionID = 0x43;
                chatPacketID = 0x0F;
                break;

            case "1.12":
                spawnPositionID = 0x45;
            case "1.12.1":
            case "1.12.2":
                timePacketID = 0x47;
                weatherPacketID = 0x1E;

                if (spawnPositionID == 0) {
                    spawnPositionID = 0x46;
                }

                chatPacketID = 0x0F;
                break;

            case "1.14":
            case "1.14.1":
            case "1.14.2":
            case "1.14.3":
            case "1.14.4":
                timePacketID = 0x4E;
                weatherPacketID = 0x1E;
                spawnPositionID = 0x4D;
                chatPacketID = 0x0E;
                break;

            case "1.15":
            case "1.15.1":
            case "1.15.2":
                timePacketID = 0x4F;
                weatherPacketID = 0x1F;
                spawnPositionID = 0x4E;
                chatPacketID = 0x0F;
                break;

            case "1.16":
            case "1.16.1":
                timePacketID = 0x4E;
                weatherPacketID = 0x1E;
                spawnPositionID = 0x42;
                chatPacketID = 0x0E;
                break;
            case "1.16.2":
            case "1.16.3":
            case "1.16.4":
            case "1.16.5":
                timePacketID = 0x4E;
                weatherPacketID = 0x1D;
                spawnPositionID = 0x42;
                chatPacketID = 0x0E;
                break;

            case "1.17":
            case "1.17.1":
                timePacketID = 0x58;
                weatherPacketID = 0x1E;
                spawnPositionID = 0x4B;
                chatPacketID = 0x0F;
                break;

            case "1.18":
            case "1.18.1":
            case "1.18.2":
                timePacketID = 0x59;
                weatherPacketID = 0x1E;
                spawnPositionID = 0x4B;
                chatPacketID = 0x0F;
                break;

            case "1.19":
                timePacketID = 0x59;
                weatherPacketID = 0x1B;
                spawnPositionID = 0x4A;
                chatPacketID = 0x30;
                break;
            case "1.19.1":
                timePacketID = 0x5C;
                weatherPacketID = 0x1D;
                spawnPositionID = 0x4D;
                chatPacketID = 0x33;
                break;

            // we show an error and stop if the protocol isn't supported
            default:
                JOptionPane.showMessageDialog(null, "Error: unsupported Minecraft version: " + minecraftVersion);
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
            progressBar.setActionText("Editing " + replayData.getMcprFile().getName() + "...");
            // showing the progress bar
            progressBar.show();

            int i = 0;

            // iterate while we still have some data in the reader
            while (reader.getLength() != 0) {
                // get the timestamp, the size and the packet id
                int timestamp = reader.readInt();
                int size = reader.readInt();
                int packetID = reader.readVarInt();

                i++;
                if (i == 500) {
                    System.out.println("break");
                }

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

                // check if we change the weather
                if (this.changeWeather) {
                    if (packetID == weatherPacketID) {
                        int reason = reader.readByte();

                        // write the packet and continue if the reason does4n't affect the weather
                        if (reason != 1 && reason != 2 && reason != 7 && reason != 8) {
                            // if the packet does not change the rain, we write it
                            // packet header
                            writer.writeInt(timestamp);
                            writer.writeInt(size);
                            writer.writeVarInt(packetID);
                            // packet content
                            writer.writeByte(reason);
                            writer.writeFloat(reader.readFloat());
                            continue;
                        }

                        // if we want to clear the weather,
                        // we don't write the weather packets
                        if (this.newWeather.equals("Clear")) {
                            reader.skip(4);
                        } else {
                            // otherwise, we write the float to the recording
                            // and continue to the next packet
                            writer.writeFloat(reader.readFloat());
                        }

                        continue;
                    } else {
                        if (packetID == spawnPositionID) {
                            // we write the packet timestamp, size, packet id
                            writer.writeInt(timestamp);
                            writer.writeInt(size);
                            writer.writeVarInt(packetID);
                            // and finally, we write the packet data
                            writer.writeByteArray(reader.readFollowingBytes(size - 1));

                            // adding rain or thunder should tell the client
                            // that the rain is beginning and setting the rain level to max
                            if (this.newWeather.equals("Rain") || this.newWeather.equals("Thunder")) {
                                // begin rain packet
                                writer.writeInt(timestamp);
                                writer.writeInt(6);
                                writer.writeVarInt(weatherPacketID);
                                writer.writeByte(1);
                                writer.writeFloat(0);

                                // set rain level to max
                                writer.writeInt(timestamp);
                                writer.writeInt(6);
                                writer.writeVarInt(weatherPacketID);
                                writer.writeByte(7);
                                writer.writeFloat(1.0f);
                            }

                            // and if we change the time to thunder,
                            // then we set the thunder level to max
                            if (this.newWeather.equals("Thunder")) {
                                writer.writeInt(timestamp);
                                writer.writeInt(6);
                                writer.writeVarInt(weatherPacketID);

                                writer.writeByte(8);
                                writer.writeFloat(1.0f);
                            }

                            // then we change the new weather to clear
                            // so we clear the useless weather packets
                            newWeather = "Clear";
                            continue;
                        }
                    }
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
            progressBar.setActionText("Writing " + this.replayData.getMcprFile().getName() + ", this can take a while...");

            // we open the zip file, write the recording.tmcpr file and close the zip
            replayZip.open();
            InputStream inputStream = writer.getInputStream();
            replayZip.addFile(inputStream, "recording.tmcpr");
            replayZip.close();
            inputStream.close();

            writer.clear();

            // kill the progress bar window as it's now useless
            progressBar.kill();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
