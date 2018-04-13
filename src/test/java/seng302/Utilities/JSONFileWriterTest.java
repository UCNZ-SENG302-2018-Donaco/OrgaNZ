package seng302.Utilities;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import seng302.Donor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class JSONFileWriterTest {

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .enableComplexMapKeySerialization()
            .create();

    private final List<Donor> testDonors = Arrays.asList(
            new Donor("First", null, "Last", LocalDate.of(1970, 1, 1), 1),
            new Donor("Second", null, "Sur", LocalDate.of(2001, 5, 8), 2),
            new Donor("Third", "Bobby", "Man", LocalDate.of(1996, 3, 4), 3)
    );

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private List<Donor> readDonorsFromFile(File file) throws Exception {
        JsonReader reader = new JsonReader(new FileReader(file));
        List<Donor> readDonors = gson.fromJson(reader, new TypeToken<List<Donor>>() {}.getType());
        reader.close();

        return readDonors;
    }

    @Test
    public void writeToNewFileTest() throws Exception {
        File file = new File(tempFolder.getRoot(), "new_file.json");
        JSONFileWriter<Donor> donorFileWriter = new JSONFileWriter<>(file, Donor.class);
        donorFileWriter.overwriteWith(testDonors);
        donorFileWriter.close();

        List<Donor> readDonors = readDonorsFromFile(file);
        assertEquals(testDonors, readDonors);
    }

    @Test
    public void appendOneToExistingFileTest() throws Exception {
        File file = new File(tempFolder.getRoot(), "existing_file.json");
        FileWriter fw = new FileWriter(file);
        gson.toJson(testDonors.subList(0, 2), TypeToken.getParameterized(List.class, Donor.class).getType(), fw);
        fw.close();

        JSONFileWriter<Donor> donorFileWriter = new JSONFileWriter<>(file, Donor.class);
        donorFileWriter.appendOne(testDonors.get(2));
        donorFileWriter.close();

        List<Donor> readDonors = readDonorsFromFile(file);
        assertEquals(testDonors, readDonors);
    }

    @Test
    public void appendManyToExistingFileTest() throws Exception {
        File file = new File(tempFolder.getRoot(), "existing_file.json");
        FileWriter fw = new FileWriter(file);
        gson.toJson(testDonors.subList(0, 1), TypeToken.getParameterized(List.class, Donor.class).getType(), fw);
        fw.close();

        JSONFileWriter<Donor> donorFileWriter = new JSONFileWriter<>(file, Donor.class);
        donorFileWriter.appendMany(testDonors.subList(1, 3));
        donorFileWriter.close();

        List<Donor> readDonors = readDonorsFromFile(file);
        assertEquals(testDonors, readDonors);
    }
}
