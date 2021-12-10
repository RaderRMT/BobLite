package fr.rader.boblite.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class ByteArrayInOutStream extends ByteArrayOutputStream {

    public ByteArrayInOutStream(int size) {
        super(size);
    }

    /**
     * Create a ByteArrayInputStream from the ByteArrayOutputStream's data
     *
     * @return the output stream converted to an input stream
     */
    public ByteArrayInputStream getInputStream() {
        // craete an input stream from the output stream's data
        ByteArrayInputStream inputStream = new ByteArrayInputStream(this.buf, 0, this.count);

        // set the buffer to null to clear it?
        this.buf = null;

        // and we return the newly created input stream
        return inputStream;
    }
}
