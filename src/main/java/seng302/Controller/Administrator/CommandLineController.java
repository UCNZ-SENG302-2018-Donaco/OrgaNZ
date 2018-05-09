package seng302.Controller.Administrator;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;

import seng302.Commands.BaseCommand;
import seng302.Commands.CommandParser;
import seng302.Controller.Components.BatchedTextStream;
import seng302.Controller.MainController;
import seng302.Controller.SubController;
import seng302.Utilities.View.PageNavigator;

import picocli.CommandLine;

public class CommandLineController extends SubController {

    @FXML
    public HBox sidebarPane;

    @FXML
    public TextField inputTextField;

    @FXML
    public TextArea outputTextArea;

    private List<String> commandHistoryList = new ArrayList<>();
    private int currentHistoryIndex = 0;
    private String unexecutedPreviousText;

    private BatchedTextStream batchedTextStream;

    private BaseCommand command;
    PrintStream outputStream;
    private Thread currentRunningCommandThread;

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

        inputTextField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.DOWN)) {
                onDown();
                event.consume();
            } else if (event.getCode().equals(KeyCode.UP)) {
                onUp();
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

    @FXML
    public void onEnter() {
        String commandText = inputTextField.getText();
        if (commandText.isEmpty()) {
            return;
        } else if (currentRunningCommandThread != null && currentRunningCommandThread.isAlive()) {
            PageNavigator.showAlert(AlertType.WARNING, "Command already running",
                    "Please wait until the last command has finished running.");
        }

        commandHistoryList.add(commandText);
        currentHistoryIndex = commandHistoryList.size();

        outputTextArea.appendText("> " + commandText + "\n");
        inputTextField.setText("");

        Task task = new Task<Void>() {
            @Override
            protected Void call() {
                System.setOut(outputStream);
                CommandLine.run(command, System.out, CommandParser.parseCommands(commandText));
                outputTextArea.appendText(batchedTextStream.getNewText());
                outputTextArea.appendText("\n");
                System.setOut(System.out);
                return null;
            }
        };
        currentRunningCommandThread = new  Thread(task);
        currentRunningCommandThread.run();
    }
}
