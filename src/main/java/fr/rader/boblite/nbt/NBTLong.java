package fr.rader.boblite.nbt;

import fr.rader.boblite.utils.DataReader;
import fr.rader.boblite.utils.DataWriter;

import java.io.IOException;

public class NBTLong extends NBTBase {

    private long value;

    public NBTLong(String name, long value) {
        setId(0x04);
        setName(name);

        this.value = value;
    }

    public NBTLong(long value) {
        this.value = value;
    }

    public NBTLong(DataReader reader) {
        try {
            this.value = reader.readLong();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public void writeNBT(DataWriter writer) {
        if(getName() != null) {
            writer.writeByte(getId());
            writer.writeShort(getName().length());
            writer.writeString(getName());
        }

        writer.writeLong(value);
    }
}
