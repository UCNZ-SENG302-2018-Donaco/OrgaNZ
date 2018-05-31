package com.humanharvest.organz.controller.components;

import java.io.OutputStream;

/**
 * An output stream that does nothing with the text until the getNewText function is called, which then resets and
 * returns the text that has been streamed
 */
public class BatchedTextStream extends OutputStream {

    private String textToAdd = "";

    /**
     * Get the text since the last request
     * @return The text sent to the stream since the last getNewText request
     */
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
