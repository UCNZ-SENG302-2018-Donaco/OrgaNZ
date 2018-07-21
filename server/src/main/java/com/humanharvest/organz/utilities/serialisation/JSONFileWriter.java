package com.humanharvest.organz.utilities.serialisation;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Provides functionality to serialize objects of a given datatype to a JSON file, by either overwriting it or
 * appending to it as required.
 * @param <T> The datatype to serialize to the JSON file (must also be the datatype already stored in the file if
 * appending to an existing file).
 */
public class JSONFileWriter<T> implements Closeable {

    private final OutputStream output;

    /**
     * Creates a new JSONFileWriter to write/append to to the given file.
     * @param file The JSON file to write/append to.
     */
    public JSONFileWriter(File file) throws FileNotFoundException {
        output = new FileOutputStream(file);
    }

    /**
     * Creates a new JSONFileWriter to write/append to to the given output stream.
     * @param output The Output stream to write/append to.
     */
    public JSONFileWriter(OutputStream output) {
        this.output = output;
    }

    /**
     * Overrides the current contents of the file with the list of objects provided.
     * @param objects The objects of the given datatype to write to the JSON file.
     * @throws IOException If some IO error occurs when writing to the file.
     */
    public void overrideWith(List<T> objects) throws IOException {
        JSONMapper.Mapper.writeValue(output, objects);
    }

    @Override
    public void close() throws IOException {
        output.close();
    }
}
