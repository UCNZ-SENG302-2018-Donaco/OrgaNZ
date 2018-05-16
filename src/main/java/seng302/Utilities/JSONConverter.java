package seng302.Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import seng302.Client;
import seng302.State.ClientManager;
import seng302.State.State;
import seng302.TransplantRequest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * Uses GSON to convert Java objects into JSON files and from JSON files
 * to Java objects.
 */
public final class JSONConverter {

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .enableComplexMapKeySerialization()
            .create();

    private JSONConverter() {
        // To ensure that this UTILITY class cannot be instantiated.
    }

    /**
     * If the given file does not exist, creates an empty JSON array in that file.
     * If the given file does exist, does nothing.
     * @param file The file to check/create.
     * @throws IOException If an error occurs while creating the file.
     */
    public static void createEmptyJSONFileIfNotExists(File file) throws IOException {
        try {
            if (file.createNewFile()) {
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("[]\n");
                }
            }
        } catch (IOException exc) {
            throw new IOException(String.format("An error occurred when creating this file: %s\n%s",
                    file.getName(), exc.getMessage()), exc);
        }
    }

    /**
     * Saves the current clients list to a specified file
     * @param file The file to be saved to
     * @throws IOException Throws IOExceptions
     */
    public static void saveToFile(File file) throws IOException {
        try(OutputStream outputStream = new FileOutputStream(file)) {
            saveToStream(outputStream);
        }
    }

    /**
     * Saves the current clients list to a specified stream
     * @param stream The stream to be saved to
     * @throws IOException Throws IOExceptions
     */
    public static void saveToStream(OutputStream stream) throws IOException {
        try(Writer writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8)) {
            ClientManager clientManager = State.getClientManager();
            gson.toJson(clientManager.getClients(), writer);
        }
    }

    /**
     * Loads the clients from a specified file. Overwrites any current clients
     * @param file The file to be loaded from
     * @throws IOException Throws IOExceptions
     */
    public static void loadFromFile(File file) throws IOException {
        try(InputStream fileStream = new FileInputStream(file)) {
            try(Reader reader = new InputStreamReader(fileStream, StandardCharsets.UTF_8)) {
                try {
                    ArrayList<Client> clients;
                    Type collectionType = new TypeToken<ArrayList<Client>>() {
                    }.getType();

                    clients = gson.fromJson(reader, collectionType);
                    for (Client client : clients) {
                        for (TransplantRequest request : client.getTransplantRequests()) {
                            request.setClient(client);
                        }
                    }
                    ClientManager clientManager = State.getClientManager();
                    clientManager.setClients(clients);
                }  catch (JsonSyntaxException e) {
                    throw new IllegalArgumentException("Not a valid json file", e);
                }
            }
        }
    }

    public static Gson getGson() {
        return gson;
    }
}
