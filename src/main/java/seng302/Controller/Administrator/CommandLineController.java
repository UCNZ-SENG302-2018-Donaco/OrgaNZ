package seng302.Controller.Administrator;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import seng302.Commands.BaseCommand;
import seng302.Commands.CommandParser;
import seng302.Controller.Components.BatchedTextStream;
import seng302.Controller.MainController;
import seng302.Controller.SubController;

import picocli.CommandLine;

public class CommandLineController extends SubController {

    @FXML
    public HBox menuBarPane;

    private TextField inputTextField;

    @FXML
    private TextArea outputTextArea;

    @FXML
    private BorderPane borderPane;

    private List<String> commandHistoryList = new ArrayList<>();
    private int currentHistoryIndex = 0;
    private String unexecutedPreviousText = "";

    private BatchedTextStream batchedTextStream;

    private BaseCommand command;
    private PrintStream outputStream;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.setTitle("Administrator command line interface");
        mainController.loadMenuBar(menuBarPane);
    }

    @FXML
    private void initialize() {
        batchedTextStream = new BatchedTextStream();
        outputStream = new PrintStream(batchedTextStream);

        //Create a custom TextField that overrides the paste method to allow multiline execution
        inputTextField = new TextField() {
            @Override
            public void paste() {
                Clipboard c = Clipboard.getSystemClipboard();
                if (c.hasString()) {
                    String[] strings = c.getString().split("\n");
                    if (strings.length > 1) {
                        for (int i = 0; i < strings.length - 1; i++) {
                            createAndRunCommand(strings[i]);
                        }
                        replaceSelection(strings[strings.length - 1]);
                    } else {
                        replaceSelection(strings[0]);
                    }
                }
            }
        };
        //Set the ID of the TextField for testing purpose
        inputTextField.setId("inputTextField");

        //Place the TextField in the scene, as we create it programmatically rather than in the FXML
        BorderPane.setMargin(inputTextField, new Insets(0, 20, 20, 20));
        borderPane.setBottom(inputTextField);

        inputTextField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.DOWN)) {
                onDown();
                event.consume();
            } else if (event.getCode().equals(KeyCode.UP)) {
                onUp();
                event.consume();
            } else if (event.getCode().equals(KeyCode.ENTER)) {
                onEnter();
                event.consume();
            }
        });

        command = new BaseCommand();
        System.setOut(outputStream);
        CommandLine.usage(command, outputStream);
        outputTextArea.appendText(batchedTextStream.getNewText());
        System.setOut(System.out);
    }

    /**
     * Iterate up the commandHistoryList, if we're at the bottom the firstly save the unexecuted text so we can come
     * back to it
     */
    private void onUp() {
        if (commandHistoryList.size() == 0) {
            return;
        }
        if (currentHistoryIndex - 1 >= 0) {
            if (currentHistoryIndex == commandHistoryList.size()) {
                unexecutedPreviousText = inputTextField.getText();
            }
            currentHistoryIndex--;
            inputTextField.setText(commandHistoryList.get(currentHistoryIndex));
        }
        inputTextField.end();
    }

    /**
     * Iterate down on the commandHistoryList, if we're at the bottom, then set the text to the previous unexecuted text
     * instead
     */
    private void onDown() {
        if (currentHistoryIndex + 1 < commandHistoryList.size()) {
            currentHistoryIndex++;
            inputTextField.setText(commandHistoryList.get(currentHistoryIndex));
        } else if (currentHistoryIndex + 1 == commandHistoryList.size()) {
            currentHistoryIndex++;
            inputTextField.setText(unexecutedPreviousText);
        }
        inputTextField.end();
    }


    /**
     * When the enter key is pressed, if there is text in the input then add it to the command history
     * then execute that command
     */
    private void onEnter() {
        String commandText = inputTextField.getText();
        if (!commandText.isEmpty()) {
            commandHistoryList.add(commandText);
            currentHistoryIndex = commandHistoryList.size();
            inputTextField.setText("");
            createAndRunCommand(commandText);
        }
    }

    /**
     * Create a new Task that first adds the run command to the output text area
     * then run the command using CommandLine.run.
     * Once the task has run, append the resulting text to the output text area
     * Run using a SingleThreadExecutor to ensure multiple commands are run in order rather than concurrently
     * @param commandText The string to run as a command
     */
    private void createAndRunCommand(String commandText) {
        Task task = new Task<Void>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> outputTextArea.appendText("> " + commandText + "\n"));

                System.setOut(outputStream);
                CommandLine.run(command, System.out, CommandParser.parseCommands(commandText));
                Platform.runLater(() -> outputTextArea.appendText(batchedTextStream.getNewText() + "\n"));
                System.setOut(System.out);
                return null;
            }
        };
        executor.execute(task);
    }
}
