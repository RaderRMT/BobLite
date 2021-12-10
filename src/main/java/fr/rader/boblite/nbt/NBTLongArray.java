package fr.rader.boblite.nbt;

import fr.rader.boblite.utils.DataWriter;

public class NBTLongArray extends NBTBase {

    private long[] value;

    public NBTLongArray(String name, long[] value) {
        setId(0x0c);
        setName(name);

        this.value = value;
    }

    public NBTLongArray(long[] value) {
        this.value = value;
    }

    public long[] getValue() {
        return value;
    }

    public void setValue(long[] value) {
        this.value = value;
    }

    public void writeNBT(DataWriter writer) {
        if(getName() != null) {
            writer.writeByte(getId());
            writer.writeShort(getName().length());
            writer.writeString(getName());
        }

        writer.writeInt(value.length);
        writer.writeLongArray(value);
    }
}
