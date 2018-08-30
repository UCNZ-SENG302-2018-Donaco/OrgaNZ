package com.humanharvest.organz.utilities.serialisation;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.type.CollectionType;

/**
 * Provides functionality to read a JSON file as a stream of objects of a given datatype. Also provides the ability
 * to get the file's size and the reader's current position within it.
 *
 * @param <T> The datatype stored in (and hence to read from) the JSON file.
 */
public class JSONFileReader<T> implements Closeable {

    private final FileChannel channel;
    private final JsonParser parser;
    private final Class<T> dataClass;
    private boolean readingAsStream;

    /**
     * Creates a new JSONFileReader to read from the given file. The class of the datatype must also be provided
     * because of Java's type erasure.
     *
     * @param file The JSON file to read from.
     * @param dataClass The class of the datatype stored in the JSON file.
     * @throws FileNotFoundException If the given file cannot be found (or cannot be opened).
     */
    public JSONFileReader(File file, Class<T> dataClass) throws IOException {
        this.dataClass = dataClass;

        FileInputStream inputStream = new FileInputStream(file);

        JsonFactory factory = new JsonFactory();
        parser = factory.createParser(inputStream);
        channel = inputStream.getChannel();
    }

    /**
     * Returns the size of the JSON file we are reading from.
     *
     * @return The current size of the file, measured in bytes.
     * @throws IOException If some IO error occurs when measuring the file's size.
     */
    public long getFileSize() throws IOException {
        return channel.size();
    }

    /**
     * Returns the current position of the JSONFileReader within the file.
     *
     * @return The number of bytes from the start of the file to the current position.
     * @throws IOException If some IO error occurs when measuring the current position.
     */
    public long getFilePosition() throws IOException {
        return channel.position();
    }

    /**
     * Starts reading from the JSON file as a stream of objects of the given datatype. This assumes that the JSON file
     * contains a single JSON array, which holds only objects of the given datatype.
     *
     * @throws IOException If the JSON file does not contain a single array, or some other IO error occurs.
     */
    public void startStream() throws IOException {
        if (parser.nextToken() != JsonToken.START_ARRAY) {
            throw new JsonParseException(parser, "Expected start array token");
        }
        readingAsStream = true;
    }

    /**
     * Reads the next object of the given datatype from the stream. This requires that {@link #startStream()} has
     * already been called, or an {@link IllegalStateException} will be thrown.
     *
     * @return The next object of the given datatype in the stream, or null if there are no more objects left in the
     * stream.
     * @throws IOException If an IO error occurs when reading from the file.
     */
    public T getNext() throws IOException {
        if (readingAsStream) {
            JsonToken token = parser.nextToken();
            if (token == JsonToken.END_ARRAY || token == null) {
                return null;
            }

            return JSONMapper.Mapper.readValue(parser, dataClass);
        } else {
            throw new IllegalStateException("Must have called startStream before calling getNext.");
        }
    }

    /**
     * Reads all of the objects of the given datatype in the JSON file. This assumes that the JSON file
     * contains a single JSON array, which holds only objects of the given datatype. {@link #startStream()} must NOT
     * have already been called, or an {@link IllegalStateException} will be thrown.
     *
     * @return A list of all the objects of the given datatype in the JSON file.
     */
    public List<T> getAll() throws IOException {
        if (readingAsStream) {
            throw new IllegalStateException("Cannot use getAll after started reading as stream.");
        } else {
            CollectionType type = JSONMapper.Mapper.getTypeFactory().constructCollectionType(List.class, dataClass);
            return JSONMapper.Mapper.readValue(parser, type);
        }
    }

    /**
     * Closes the JSONFileReader and its underlying file input stream.
     *
     * @throws IOException If an IO error occurs while closing the JSONFileReader.
     */
    @Override
    public void close() throws IOException {
        parser.close();
    }
}
