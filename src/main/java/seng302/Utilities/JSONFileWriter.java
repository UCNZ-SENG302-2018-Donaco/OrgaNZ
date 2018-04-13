package seng302.Utilities;

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

public class JSONFileWriter<T> implements Closeable {

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .enableComplexMapKeySerialization()
            .create();
    private final Type listType;
    private final File file;
    private FileWriter writer;

    public JSONFileWriter(File file, Class<T> dataClass) {
        Type dataType = TypeToken.get(dataClass).getType();
        this.listType = TypeToken.getParameterized(List.class, dataType).getType();
        this.file = file;
    }

    private void beginWriting() throws IOException {
        if (writer != null) {
            writer.close();
        }
        writer = new FileWriter(file);
    }

    private List<T> getCurrentObjectsInFile() throws IOException {
        JsonReader reader = new JsonReader(new FileReader(file));
        List<T> objects = gson.fromJson(reader, listType);
        reader.close();

        return objects;
    }

    public void overwriteWith(List<T> objects) throws IOException {
        beginWriting();
        gson.toJson(objects, listType, writer);
        writer.flush();
    }

    public void appendOne(T newObject) throws IOException {
        List<T> objects = getCurrentObjectsInFile();
        objects.add(newObject);

        beginWriting();
        gson.toJson(objects, listType, writer);
        writer.flush();
    }

    public void appendMany(Collection<T> newObjects) throws IOException {
        List<T> objects = getCurrentObjectsInFile();
        objects.addAll(newObjects);

        beginWriting();
        gson.toJson(objects, listType, writer);
        writer.flush();
    }

    @Override
    public void close() throws IOException {
        if (writer != null) {
            writer.close();
        }
    }
}
