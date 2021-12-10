package fr.rader.boblite.nbt;

import fr.rader.boblite.utils.DataReader;
import fr.rader.boblite.utils.DataWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NBTList extends NBTBase {

    private List<NBTBase> values = new ArrayList<>();

    private int tagID;

    public NBTList(String name, int tagID) {
        this.tagID = tagID;

        setId(0x09);
        setName(name);
    }

    public NBTList(String tagName, DataReader reader) {
        setId(0x09);
        setName(tagName);

        try {
            readData(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public NBTList(DataReader reader, boolean hasName) {
        try {
            if(hasName) {
                setId(reader.readByte());
                setName(reader.readString(reader.readShort()));

                readData(reader);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public NBTList(int tagID) {
        this.tagID = tagID;
    }

    private void readData(DataReader reader) throws IOException {
        tagID = reader.readByte();

        int length = reader.readInt();
        for(int i = 0; i < length; i++) {
            switch(tagID) {

                case 1:
                    addByte(reader.readByte());
                    break;
                case 2:
                    addShort(reader.readShort());
                    break;
                case 3:
                    addInt(reader.readInt());
                    break;
                case 4:
                    addLong(reader.readLong());
                    break;
                case 5:
                    addFloat(reader.readFloat());
                    break;
                case 6:
                    addDouble(reader.readDouble());
                    break;
                case 7:
                    addByteArray(reader.readFollowingBytes(reader.readInt()));
                    break;
                case 8:
                    addString(reader.readString(reader.readShort()));
                    break;
                case 9:
                    addList(new NBTList(reader, false));
                    break;
                case 10:
                    addCompound(new NBTCompound(reader, false));
                    break;
                case 11:
                    addIntArray(reader.readIntArray(reader.readInt()));
                    break;
                case 12:
                    addLongArray(reader.readLongArray(reader.readInt()));
                    break;
                default:
                    throw new IllegalStateException("Unexpected tag: " + Integer.toHexString(tagID));
            }
        }
    }

    public NBTBase[] getComponents() {
        return values.toArray(new NBTBase[0]);
    }

    public void writeNBT(DataWriter writer) {
        if(getName() != null) {
            writer.writeByte(getId());
            writer.writeShort(getName().length());
            writer.writeString(getName());
        }

        writer.writeByte(this.tagID);
        writer.writeInt(values.size());

        for(NBTBase base : values) {
            base.writeNBT(writer);
        }
    }

    public NBTList addComponent(NBTBase base) {
        values.add(base);
        return this;
    }

    public NBTList addByte(int value) {
        return addComponent(new NBTByte(value));
    }

    public NBTList addShort(int value) {
        return addComponent(new NBTShort(value));
    }

    public NBTList addInt(int value) {
        return addComponent(new NBTInt(value));
    }

    public NBTList addLong(long value) {
        return addComponent(new NBTLong(value));
    }

    public NBTList addFloat(float value) {
        return addComponent(new NBTFloat(value));
    }

    public NBTList addDouble(double value) {
        return addComponent(new NBTDouble(value));
    }

    public NBTList addByteArray(byte[] value) {
        return addComponent(new NBTByteArray(value));
    }

    public NBTList addString(String value) {
        return addComponent(new NBTString(value));
    }

    public NBTList addList(NBTList value) {
        return addComponent(value);
    }

    public NBTList addCompound(NBTCompound value) {
        return addComponent(value);
    }

    public NBTList addIntArray(int[] value) {
        return addComponent(new NBTIntArray(value));
    }

    public NBTList addLongArray(long[] value) {
        return addComponent(new NBTLongArray(value));
    }

    public int getTagID() {
        return tagID;
    }

    public void setTagID(int tagID) {
        this.tagID = tagID;
    }

    public void removeComponentAt(int index) {
        values.remove(index);
    }
}
