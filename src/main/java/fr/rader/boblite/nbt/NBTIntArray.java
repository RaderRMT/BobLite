package fr.rader.boblite.nbt;

import fr.rader.boblite.utils.DataWriter;

public class NBTIntArray extends NBTBase {

    private int[] value;

    public NBTIntArray(String name, int[] value) {
        setId(0x0b);
        setName(name);

        this.value = value;
    }

    public NBTIntArray(int[] value) {
        this.value = value;
    }

    public int[] getValue() {
        return value;
    }

    public void setValue(int[] value) {
        this.value = value;
    }

    public void writeNBT(DataWriter writer) {
        if(getName() != null) {
            writer.writeByte(getId());
            writer.writeShort(getName().length());
            writer.writeString(getName());
        }

        writer.writeInt(value.length);
        writer.writeIntArray(value);
    }
}
