package com.humanharvest.organz.utilities.serialization;

import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

/**
 * Provides functionality to serialize objects of a given datatype to a JSON file, by either overwriting it or
 * appending to it as required.
 * @param <T> The datatype to serialize to the JSON file (must also be the datatype already stored in the file if
 * appending to an existing file).
 */
public class JSONFileWriter<T> implements Closeable {

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .enableComplexMapKeySerialization()
            .create();
    private final Type listType;
    private final File file;
    private FileWriter writer;

    /**
     * Creates a new JSONFileWriter to write/append to to the given file. The class of the datatype must also be
     * provided because of Java's type erasure.
     * @param file The JSON file to write/append to.
     * @param dataClass The class of the datatype to serialize to the JSON file.
     */
    public JSONFileWriter(File file, Class<T> dataClass) {
        Type dataType = TypeToken.get(dataClass).getType();
        this.listType = TypeToken.getParameterized(List.class, dataType).getType();
        this.file = file;
    }

    /**
     * Clears the file's current contents and creates a new {@link FileWriter} to write to it. Will close (and flush)
     * the old FileWriter if one existed.
     * @throws IOException If some IO error occurs when beginning writing on the file.
     */
    private void beginWriting() throws IOException {
        if (writer != null) {
            writer.close();
        }
        writer = new FileWriter(file);
    }

    /**
     * Returns a list of the objects of the given datatype currently stored in the file.
     * @return The objects of the given datatype currently stored in the file.
     * @throws IOException If some IO error occurs when reading from the file.
     */
    private List<T> getCurrentObjectsInFile() throws IOException {
        JsonReader reader = new JsonReader(new FileReader(file));
        List<T> objects = gson.fromJson(reader, listType);
        reader.close();

        return objects;
    }

    /**
     * Overwrites the current contents of the file with the list of objects provided.
     * @param objects The objects of the given datatype to write to the JSON file.
     * @throws IOException If some IO error occurs when writing to the file.
     */
    public void overwriteWith(List<T> objects) throws IOException {
        beginWriting();
        gson.toJson(objects, listType, writer);
        writer.flush();
    }

    /**
     * Appends the given object to the JSON array currently stored in the file. The file must contain a JSON array,
     * and the objects currently stored in the array must be of the same datatype.
     * @param newObject The object of the given datatype to append to the JSON file.
     * @throws IOException If some IO error occurs when appending the object to the file.
     */
    public void appendOne(T newObject) throws IOException {
        List<T> objects = getCurrentObjectsInFile();
        objects.add(newObject);

        beginWriting();
        gson.toJson(objects, listType, writer);
        writer.flush();
    }

    /**
     * Appends the given objects to the JSON array currently stored in the file. The file must contain a JSON array,
     * and the objects currently stored in the array must be of the same datatype.
     * @param newObjects The objects of the given datatype to append to the JSON file.
     * @throws IOException If some IO error occurs when appending the object to the file.
     */
    public void appendMany(Collection<T> newObjects) throws IOException {
        List<T> objects = getCurrentObjectsInFile();
        objects.addAll(newObjects);

        beginWriting();
        gson.toJson(objects, listType, writer);
        writer.flush();
    }

    /**
     * Closes the JSONFileReader and its underlying file writer.
     * @throws IOException If an IO error occurs while closing the JSONFileReader.
     */
    @Override
    public void close() throws IOException {
        if (writer != null) {
            writer.close();
        }
    }
}
