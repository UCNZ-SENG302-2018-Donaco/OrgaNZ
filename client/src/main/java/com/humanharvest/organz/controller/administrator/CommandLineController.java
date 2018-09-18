package com.humanharvest.organz.controller.administrator;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.view.PageNavigator;

public class CommandLineController extends SubController {

    private final List<String> commandHistoryList = new ArrayList<>();

    @FXML
    private HBox menuBarPane;

    private TextField inputTextField;

    @FXML
    private TextArea outputTextArea;

    @FXML
    private BorderPane borderPane;

    private int currentHistoryIndex;
    private String unexecutedPreviousText = "";

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.setTitle("Administrator command line interface");
        mainController.loadMenuBar(menuBarPane);
    }

    @FXML
    private void initialize() {
        //Create a custom TextField that overrides the paste method to allow multiline execution
        inputTextField = new TextField() {
            @Override
            public void paste() {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                if (clipboard.hasString()) {
                    String[] strings = clipboard.getString().split("\n");
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
            switch (event.getCode()) {
                case DOWN:
                    onDown();
                    event.consume();
                    break;
                case UP:
                    onUp();
                    event.consume();
                    break;
                case ENTER:
                    onEnter();
                    event.consume();
                    break;
                default:
                    // don't worry about other keypresses
            }
        });

        createAndRunCommand("help");
    }

    /**
     * Iterate up the commandHistoryList, if we're at the bottom the firstly save the unexecuted text so we can come
     * back to it
     */
    private void onUp() {
        if (commandHistoryList.isEmpty()) {
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
     *
     * @param commandText The string to run as a command
     */
    private void createAndRunCommand(String commandText) {
        Platform.runLater(() -> outputTextArea.appendText("> " + commandText + '\n'));
        String result = State.getCommandRunner().execute(commandText);
        Platform.runLater(() -> {
            outputTextArea.appendText(result + '\n');
            PageNavigator.refreshAllWindows();
        });
    }
}
