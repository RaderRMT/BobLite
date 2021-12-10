package fr.rader.boblite.nbt;

import fr.rader.boblite.utils.DataWriter;

public class NBTByteArray extends NBTBase {

    private byte[] value;

    public NBTByteArray(String name, byte[] value) {
        setId(0x07);
        setName(name);

        this.value = value;
    }

    public NBTByteArray(byte[] value) {
        this.value = value;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public void writeNBT(DataWriter writer) {
        if(getName() != null) {
            writer.writeByte(getId());
            writer.writeShort(getName().length());
            writer.writeString(getName());
        }

        writer.writeInt(value.length);
        writer.writeByteArray(value);
    }
}
