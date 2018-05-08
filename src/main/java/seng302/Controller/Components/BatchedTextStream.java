package seng302.Controller.Components;

import java.io.OutputStream;

public class BatchedTextStream extends OutputStream {

    private String textToAdd = "";

    public BatchedTextStream() {
    }

    public String getNewText() {
        String toReturn = textToAdd;
        textToAdd = "";
        return toReturn;
    }

    @Override
    public void write(int character) {
        textToAdd += String.valueOf((char) character);
    }
}
