package com.humanharvest.organz.utilities.serialisation;

import com.fasterxml.jackson.databind.JavaType;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides functionality to serialize objects of a given datatype to a JSON file, by either overwriting it or
 * appending to it as required.
 *
 * @param <T> The datatype to serialize to the JSON file (must also be the datatype already stored in the file if
 *            appending to an existing file).
 */
public class JSONFileWriter<T> implements Closeable {

    private static final Logger LOGGER = Logger.getLogger(JSONFileWriter.class.getName());

    private final JavaType listType;
    private final OutputStream output;
    private List<T> currentObjects;

    /**
     * Creates a new JSONFileWriter to write to to the given stream. The class of the datatype must also be
     * provided because of Java's type erasure.
     *
     * @param output    The JSON stream to write to.
     * @param dataClass The class of the datatype to serialize to the JSON file.
     */
    public JSONFileWriter(OutputStream output, Class<T> dataClass) {
        listType = JSONMapper.Mapper.getTypeFactory().constructCollectionType(List.class, dataClass);
        this.output = output;
        currentObjects = Collections.emptyList();
    }

    /**
     * Creates a new JSONFileWriter to write/append to to the given file. The class of the datatype must also be
     * provided because of Java's type erasure.
     *
     * @param file      The JSON file to write/append to.
     * @param dataClass The class of the datatype to serialize to the JSON file.
     */
    public JSONFileWriter(File file, Class<T> dataClass) throws IOException {
        listType = JSONMapper.Mapper.getTypeFactory().constructCollectionType(List.class, dataClass);
        if (file.exists()) {
            try {
                currentObjects = JSONMapper.Mapper.readValue(file, listType);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "IO error when loading json file: " + file, e);
                currentObjects = Collections.emptyList();
            }
        } else {
            currentObjects = Collections.emptyList();
        }
        output = new FileOutputStream(file);
    }

    /**
     * Returns a list of the objects of the given datatype currently stored in the file.
     *
     * @return The objects of the given datatype currently stored in the file.
     */
    private List<T> getCurrentObjectsInFile() {
        return currentObjects;
    }

    /**
     * Overwrites the current contents of the file with the list of objects provided.
     *
     * @param objects The objects of the given datatype to write to the JSON file.
     * @throws IOException If some IO error occurs when writing to the file.
     */
    public void overwriteWith(List<T> objects) throws IOException {
        JSONMapper.Mapper.writeValue(output, objects);
    }

    /**
     * Appends the given object to the JSON array currently stored in the file. The file must contain a JSON array,
     * and the objects currently stored in the array must be of the same datatype.
     *
     * @param newObject The object of the given datatype to append to the JSON file.
     * @throws IOException If some IO error occurs when appending the object to the file.
     */
    public void appendOne(T newObject) throws IOException {
        List<T> objects = getCurrentObjectsInFile();
        objects.add(newObject);

        JSONMapper.Mapper.writeValue(output, objects);
    }

    /**
     * Appends the given objects to the JSON array currently stored in the file. The file must contain a JSON array,
     * and the objects currently stored in the array must be of the same datatype.
     *
     * @param newObjects The objects of the given datatype to append to the JSON file.
     * @throws IOException If some IO error occurs when appending the object to the file.
     */
    public void appendMany(Collection<T> newObjects) throws IOException {
        List<T> objects = getCurrentObjectsInFile();
        objects.addAll(newObjects);

        JSONMapper.Mapper.writeValue(output, objects);
    }

    /**
     * Closes the JSONFileReader and its underlying file writer.
     *
     * @throws IOException If an IO error occurs while closing the JSONFileReader.
     */
    @Override
    public void close() throws IOException {
        output.close();
    }
}
