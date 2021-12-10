package fr.rader.boblite.nbt;

import fr.rader.boblite.utils.DataReader;
import fr.rader.boblite.utils.DataWriter;

import java.io.IOException;

public class NBTDouble extends NBTBase {

    private double value;

    public NBTDouble(String name, double value) {
        setId(0x06);
        setName(name);

        this.value = value;
    }

    public NBTDouble(double value) {
        this.value = value;
    }

    public NBTDouble(DataReader reader) {
        try {
            this.value = reader.readDouble();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void writeNBT(DataWriter writer) {
        if(getName() != null) {
            writer.writeByte(getId());
            writer.writeShort(getName().length());
            writer.writeString(getName());
        }

        writer.writeDouble(value);
    }
}
