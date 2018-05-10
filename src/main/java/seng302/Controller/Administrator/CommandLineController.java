package seng302.Controller.Administrator;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import seng302.Commands.BaseCommand;
import seng302.Commands.CommandParser;
import seng302.Controller.Components.BatchedTextStream;
import seng302.Controller.MainController;
import seng302.Controller.SubController;

import picocli.CommandLine;

public class CommandLineController extends SubController {

    @FXML
    public HBox sidebarPane;

    public TextField inputTextField;

    @FXML
    public TextArea outputTextArea;

    @FXML
    public BorderPane borderPane;

    private List<String> commandHistoryList = new ArrayList<>();
    private int currentHistoryIndex = 0;
    private String unexecutedPreviousText;

    private BatchedTextStream batchedTextStream;

    private BaseCommand command;
    private PrintStream outputStream;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.setTitle("Administrator command line interface");
        //TODO: Enable the sidebar
        //mainController.loadSidebar(sidebarPane);
    }

    @FXML
    private void initialize() {
        batchedTextStream = new BatchedTextStream();
        outputStream = new PrintStream(batchedTextStream);

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

    private void onDown() {
        if (currentHistoryIndex + 1 < commandHistoryList.size()) {
            currentHistoryIndex++;
            inputTextField.setText(commandHistoryList.get(currentHistoryIndex));
        } else if (unexecutedPreviousText != null && currentHistoryIndex + 1 == commandHistoryList.size()) {
            currentHistoryIndex++;
            inputTextField.setText(unexecutedPreviousText);
        }
        inputTextField.end();
    }

    private void onEnter() {
        String commandText = inputTextField.getText();
        if (commandText.isEmpty()) {
            return;
        }

        for (String line : commandText.split("\n")) {
            commandHistoryList.add(commandText);
            currentHistoryIndex = commandHistoryList.size();
            inputTextField.setText("");
            createAndRunCommand(line);
        }
    }

    private void createAndRunCommand(String commandText) {
        Task task = new Task<Void>() {
            @Override
            protected Void call() {
                outputTextArea.appendText("> " + commandText + "\n");

                System.setOut(outputStream);
                CommandLine.run(command, System.out, CommandParser.parseCommands(commandText));
                outputTextArea.appendText(batchedTextStream.getNewText());
                outputTextArea.appendText("\n");
                System.setOut(System.out);
                return null;
            }
        };
        executor.execute(task);
    }
}
