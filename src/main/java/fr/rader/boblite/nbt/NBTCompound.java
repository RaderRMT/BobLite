package fr.rader.boblite.nbt;

import fr.rader.boblite.utils.DataReader;
import fr.rader.boblite.utils.DataWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NBTCompound extends NBTBase {

    private List<NBTBase> components = new ArrayList<>();
    private List<String> names = new ArrayList<>();

    public NBTCompound(String name) {
        setId(0x0a);
        setName(name);
    }

    public NBTCompound() {
        setId(0x0a);
    }

    public NBTCompound(String name, DataReader reader) {
        setId(0x0a);
        setName(name);

        try {
            readData(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public NBTCompound(DataReader reader, boolean hasName) {
        try {
            if(hasName) {
                setId(reader.readByte());
                setName(reader.readString(reader.readShort()));
            }

            readData(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readData(DataReader reader) throws IOException {
        while(true) {
            int tagID = reader.readByte();
            if(tagID == 0) return;

            String tagName = reader.readString(reader.readShort());

            switch(tagID) {
                case 1:
                    addByte(tagName, reader.readByte());
                    break;
                case 2:
                    addShort(tagName, reader.readShort());
                    break;
                case 3:
                    addInt(tagName, reader.readInt());
                    break;
                case 4:
                    addLong(tagName, reader.readLong());
                    break;
                case 5:
                    addFloat(tagName, reader.readFloat());
                    break;
                case 6:
                    addDouble(tagName, reader.readDouble());
                    break;
                case 7:
                    addByteArray(tagName, reader.readFollowingBytes(reader.readInt()));
                    break;
                case 8:
                    addString(tagName, reader.readString(reader.readShort()));
                    break;
                case 9:
                    addList(tagName, new NBTList(tagName, reader));
                    break;
                case 10:
                    addCompound(tagName, new NBTCompound(tagName, reader));
                    break;
                case 11:
                    addIntArray(tagName, reader.readIntArray(reader.readInt()));
                    break;
                case 12:
                    addLongArray(tagName, reader.readLongArray(reader.readInt()));
                    break;
                default:
                    throw new IllegalStateException("Unexpected tag: " + Integer.toHexString(tagID));
            }
        }
    }

    public NBTBase getComponent(String name) {
        return components.get(names.indexOf(name));
    }

    public NBTBase[] getComponents() {
        return components.toArray(new NBTBase[0]);
    }

    public boolean contains(String componentName) {
        return names.contains(componentName);
    }

    public void writeNBT(DataWriter writer) {
        if(getName() != null) {
            writer.writeByte(getId());
            writer.writeShort(getName().length());
            writer.writeString(getName());
        }

        for(NBTBase component : components) {
            component.writeNBT(writer);
        }

        writer.writeByte(0);
    }

    public NBTCompound addComponent(NBTBase component) {
        components.add(component);
        names.add(component.getName());
        return this;
    }

    public NBTCompound addByte(String name, int value) {
        return addComponent(new NBTByte(name, value));
    }

    public NBTCompound addShort(String name, int value) {
        return addComponent(new NBTShort(name, value));
    }

    public NBTCompound addInt(String name, int value) {
        return addComponent(new NBTInt(name, value));
    }

    public NBTCompound addLong(String name, long value) {
        return addComponent(new NBTLong(name, value));
    }

    public NBTCompound addFloat(String name, float value) {
        return addComponent(new NBTFloat(name, value));
    }

    public NBTCompound addDouble(String name, double value) {
        return addComponent(new NBTDouble(name, value));
    }

    public NBTCompound addByteArray(String name, byte[] value) {
        return addComponent(new NBTByteArray(name, value));
    }

    public NBTCompound addString(String name, String value) {
        return addComponent(new NBTString(name, value));
    }

    public NBTCompound addList(String name, NBTList value) {
        if(!name.equals(value.getName())) throw new IllegalArgumentException("Both names must be equal");
        return addComponent(value);
    }

    public NBTCompound addCompound(String name, NBTCompound value) {
        if(!name.equals(value.getName())) throw new IllegalArgumentException("Both names must be equal");
        return addComponent(value);
    }

    public NBTCompound addIntArray(String name, int[] value) {
        return addComponent(new NBTIntArray(name, value));
    }

    public NBTCompound addLongArray(String name, long[] value) {
        return addComponent(new NBTLongArray(name, value));
    }

    public void removeComponentAt(int index) {
        components.remove(index);
        names.remove(index);
    }
}
