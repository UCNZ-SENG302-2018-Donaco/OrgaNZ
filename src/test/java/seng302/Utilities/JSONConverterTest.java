package seng302.Utilities;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import seng302.Client;
import seng302.State.ClientManager;
import seng302.State.State;

import org.junit.Test;

public class JSONConverterTest {
    @Test
    public void saveTest() throws IOException {
        Client client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        List<Client> clients = new ArrayList<>();
        clients.add(client);
        State.init();
        ClientManager manager = State.getClientManager();
        manager.setClients(clients);

        byte[] data;
        try(ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            JSONConverter.saveToStream(stream);
            data = stream.toByteArray();
        }

        StringBuilder builder = new StringBuilder();

        String json;
        try(ByteArrayInputStream stream = new ByteArrayInputStream(data)) {
            try (Scanner scanner = new Scanner(stream, "utf-8")) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    builder.append(line);
                }

                json = builder.toString();
            }
        }

        assertTrue(json.contains("\"firstName\": \"First\""));
        assertTrue(json.contains("\"lastName\": \"Last\""));
    }
}