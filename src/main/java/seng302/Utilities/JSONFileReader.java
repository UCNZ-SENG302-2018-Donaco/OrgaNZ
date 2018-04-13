package seng302.Utilities;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.channels.FileChannel;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

/**
 * Provides functionality to read a JSON file as a stream of objects of a given datatype. Also provides the ability
 * to get the file's size and the reader's current position within it.
 * @param <T> The datatype stored in (and hence to read from) the JSON file.
 */
public class JSONFileReader<T> implements Closeable {

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .enableComplexMapKeySerialization()
            .create();
    private final FileChannel channel;
    private final JsonReader reader;
    private final Type dataType = new TypeToken<T>() {}.getType();
    private boolean readingAsStream = false;

    /**
     * Creates a new JSONFileReader to read from the given file.
     * @param file The JSON file to read from.
     * @throws FileNotFoundException If the given file cannot be found (or cannot be opened).
     */
    public JSONFileReader(File file) throws FileNotFoundException {
        FileInputStream inputStream = new FileInputStream(file);
        reader = new JsonReader(new InputStreamReader(inputStream));
        channel = inputStream.getChannel();
    }

    /**
     * Returns the size of the JSON file we are reading from.
     * @return The current size of the file, measured in bytes.
     * @throws IOException If some IO error occurs when measuring the file's size.
     */
    public long getFileSize() throws IOException {
        return channel.size();
    }

    /**
     * Returns the current position of the JSONFileReader within the file.
     * @return The number of bytes from the start of the file to the current position.
     * @throws IOException If some IO error occurs when measuring the current position.
     */
    public long getFilePosition() throws IOException {
        return channel.position();
    }

    /**
     * Starts reading from the JSON file as a stream of objects of the given datatype. This assumes that the JSON file
     * contains a single JSON array, which holds only objects of the given datatype.
     * @throws IOException If the JSON file does not contain a single array, or some other IO error occurs.
     */
    public void startStream() throws IOException {
        reader.beginArray();
        readingAsStream = true;
    }

    /**
     * Reads the next object of the given datatype from the stream. This requires that {@link #startStream()} has
     * already been called.
     * @return The next object of the given datatype in the stream, or null if there are no more objects left in the
     * stream.
     * @throws IOException If an IO error occurs when reading from the file.
     */
    public T getNext() throws IOException {
        if (!readingAsStream) {
            throw new IllegalStateException("Must have called startStream before calling getNext.");
        } else if (reader.hasNext()) {
            return gson.fromJson(reader, dataType);
        } else {
            return null;
        }
    }

    /**
     * Reads all of the objects of the given datatype in the JSON file. This assumes that the JSON file
     * contains a single JSON array, which holds only objects of the given datatype. {@link #startStream()} must NOT
     * have already been called, or an {@link IllegalStateException} will be thrown.
     * @return A list of all the objects of the given datatype in the JSON file.
     */
    public List<T> getAll() {
        if (readingAsStream) {
            throw new IllegalStateException("Cannot use getAll after started reading as stream.");
        } else {
            Type listType = new TypeToken<List<T>>() {}.getType();
            return gson.fromJson(reader, listType);
        }
    }

    /**
     * Closes the JSONFileReader and its underlying file input stream.
     * @throws IOException If an IO error occurs while closing the JSONFileReader.
     */
    public void close() throws IOException {
        reader.close();
    }
}
