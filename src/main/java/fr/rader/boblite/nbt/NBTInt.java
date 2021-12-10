package fr.rader.boblite.nbt;

import fr.rader.boblite.utils.DataReader;
import fr.rader.boblite.utils.DataWriter;

import java.io.IOException;

public class NBTInt extends NBTBase {

    private int value;

    public NBTInt(String name, int value) {
        setId(0x03);
        setName(name);

        this.value = value;
    }

    public NBTInt(int value) {
        this.value = value;
    }

    public NBTInt(DataReader reader) {
        try {
            this.value = reader.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void writeNBT(DataWriter writer) {
        if(getName() != null) {
            writer.writeByte(getId());
            writer.writeShort(getName().length());
            writer.writeString(getName());
        }

        writer.writeInt(value);
    }
}
