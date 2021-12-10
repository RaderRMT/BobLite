package fr.rader.boblite.nbt;

import fr.rader.boblite.utils.DataWriter;

public class NBTString extends NBTBase {

    private String value;

    public NBTString(String name, String value) {
        setId(0x08);
        setName(name);

        this.value = value;
    }

    public NBTString(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void writeNBT(DataWriter writer) {
        if(getName() != null) {
            writer.writeByte(getId());
            writer.writeShort(getName().length());
            writer.writeString(getName());
        }

        writer.writeShort(value.length());
        writer.writeString(value);
    }
}
