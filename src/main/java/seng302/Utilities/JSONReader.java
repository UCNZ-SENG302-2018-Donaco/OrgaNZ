package seng302.Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

public class JSONReader<T> {

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .enableComplexMapKeySerialization()
            .create();

    private File file;
    private JsonReader reader;
    private boolean readingAsStream = false;
    private Type dataType = new TypeToken<T>(){}.getType();

    public JSONReader(File file) throws FileNotFoundException {
        this.file = file;
        this.reader = new JsonReader(new InputStreamReader(new FileInputStream(file)));
    }

    public void startStream() throws IOException {
        reader.beginArray();
        readingAsStream = true;
    }

    public T getNext() throws IOException {
        if (!readingAsStream) {
            throw new IllegalStateException("Must have called startStream before calling getNext.");
        } else if (reader.hasNext()) {
            return gson.fromJson(reader, dataType);
        } else {
            return null;
        }
    }

    public List<T> getAll() {
        if (readingAsStream) {
            throw new IllegalStateException("Cannot use getAll after started reading as stream.");
        } else {
            Type listType = new TypeToken<List<T>>(){}.getType();
            return gson.fromJson(reader, listType);
        }
    }

    public void close() throws IOException {
        reader.close();
    }
}
