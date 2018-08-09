package com.humanharvest.organz.controller.clinician;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.utilities.view.WindowContext.WindowContextBuilder;

public class OrgansToDonateController extends SubController {

    private static final int ROWS_PER_PAGE = 30;
    private static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("d MMM yyyy hh:mm a");

    @FXML
    private HBox menuBarPane;

    @FXML
    private TableView<DonatedOrgan> tableView;

    @FXML
    private TableColumn<DonatedOrgan, String> clientCol;

    @FXML
    private TableColumn<DonatedOrgan, Organ> organCol;

    @FXML
    private TableColumn<DonatedOrgan, LocalDateTime> timeOfDeathCol;

    @FXML
    private TableColumn<DonatedOrgan, Duration> timeUntilExpiryCol;

    @FXML
    private Pagination pagination;

    @FXML
    private Text displayingXToYOfZText;

    private ClientManager manager;
    private ObservableList<DonatedOrgan> observableOrgansToDonate = FXCollections.observableArrayList();
    private FilteredList<DonatedOrgan> filteredOrgansToDonate = new FilteredList<>(observableOrgansToDonate);
    private SortedList<DonatedOrgan> sortedOrgansToDonate = new SortedList<>(filteredOrgansToDonate);

    /**
     * Gets the client manager from the global state.
     */
    public OrgansToDonateController() {
        manager = State.getClientManager();
    }

    // ---------------- Setup methods ----------------

    /**
     * Sets up the page, setting its title, loading the menu bar and doing the first refresh of the data.
     * @param mainController The main controller that defines which window this subcontroller belongs to.
     */
    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.setTitle("Organs to donate");
        mainController.loadMenuBar(menuBarPane);
        refresh();
    }

    /**
     * Initializes the page and the table/pagination properties.
     */
    @FXML
    private void initialize() {
        setupTable();

        //On pagination update call createPage
        pagination.setPageFactory(this::createPage);
    }

    /**
     * Sets up the table columns with their respective value factories and representation factories. Also registers a
     * mouse event handler for double-clicking on a record in the table to open up the appropriate client profile.
     */
    private void setupTable() {
        clientCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getDonor().getFullName()));
        organCol.setCellValueFactory(new PropertyValueFactory<>("organType"));
        timeOfDeathCol.setCellValueFactory(new PropertyValueFactory<>("dateTimeOfDonation"));
        timeUntilExpiryCol.setCellValueFactory(new PropertyValueFactory<>("durationUntilExpiry"));

        // Format all the datetime cells
        timeOfDeathCol.setCellFactory(cell -> formatDateTimeCell());
        timeUntilExpiryCol.setCellFactory(cell -> formatDurationCell());

        // Register the mouse event for double-clicking on a record to open the client profile.
        tableView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                DonatedOrgan organToDonate = tableView.getSelectionModel().getSelectedItem();
                if (organToDonate != null) {
                    Client client = organToDonate.getDonor();
                    MainController newMain = PageNavigator.openNewWindow();
                    if (newMain != null) {
                        newMain.setWindowContext(new WindowContextBuilder()
                                .setAsClinicianViewClientWindow()
                                .viewClient(client)
                                .build());
                        PageNavigator.loadPage(Page.VIEW_CLIENT, newMain);
                    }
                }
            }
        });

        // Attach timer to update table each second (for time until expiration)
        final Timeline clock = new Timeline(new KeyFrame(
                javafx.util.Duration.millis(1000),
                event -> tableView.refresh()));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

        // Sets the comparator for sorting by organ column.
        organCol.setComparator(new Comparator<Organ>() {
            /**
             * Alphabetical order of the organ name.
             */
            @Override
            public int compare(Organ o1, Organ o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });

        // Sets the comparator for sorting by duration column.
        timeUntilExpiryCol.setComparator((o1, o2) -> {
            if (o1 == o2) {
                return 0;
            } else if (o1 == null) {
                return 1; // o1 is "biggest"
            } else if (o2 == null) {
                return -1; //o2 is "biggest"
            } else {
                return o1.compareTo(o2);
            }
        });

        filteredOrgansToDonate.setPredicate(donatedOrgan -> donatedOrgan.getDurationUntilExpiry() == null
                || !donatedOrgan.getDurationUntilExpiry().isZero());
        sortedOrgansToDonate.comparatorProperty().bind(tableView.comparatorProperty());
    }

    /**
     * Upon pagination, update the table to show the correct items
     * @param pageIndex The page we're now on (starts at 0)
     * @return An empty pane as pagination requires a non null return. Not used.
     */
    private Node createPage(int pageIndex) {
        updateOrgansToDonateList();
        return new Pane();
    }

    private void updateOrgansToDonateList() {
        Collection<DonatedOrgan> newOrgansToDonate = manager.getAllOrgansToDonate();

        observableOrgansToDonate.setAll(newOrgansToDonate);
        tableView.getSortOrder().setAll(timeUntilExpiryCol);

        /* TODO decide whether we need to paginate or not
        int newPageCount = Math.max(1, (newOrgansToDonate.getTotalResults() + ROWS_PER_PAGE - 1) / ROWS_PER_PAGE);
        if (pagination.getPageCount() != newPageCount) {
            pagination.setPageCount(newPageCount);
        }

        setupDisplayingXToYOfZText(newOrgansToDonate.getTotalResults());
        */
    }

    /**
     * Refreshes the data in the transplants waiting list table. Should be called whenever any page calls a global
     * refresh.
     */
    @Override
    public void refresh() {
        observableOrgansToDonate.setAll(manager.getAllOrgansToDonate());
        tableView.setItems(sortedOrgansToDonate);
    }

    // ---------------- Format methods ----------------

    /**
     * Formats a table cell that holds a {@link LocalDateTime} value to display that value in the date time format.
     * @return The cell with the date time formatter set.
     */
    private static TableCell<DonatedOrgan, LocalDateTime> formatDateTimeCell() {
        return new TableCell<DonatedOrgan, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item.format(dateTimeFormat));
                }
            }
        };
    }

    /**
     * Formats a table cell that holds a {@link Duration} value to display that value in the right format.
     * @return The cell with the duration formatter set.
     */
    private static TableCell<DonatedOrgan, Duration> formatDurationCell() {
        return new TableCell<DonatedOrgan, Duration>() {

            @Override
            protected void updateItem(Duration item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setText(null);

                } else if (item == null) { // no expiration

                    Duration timeSinceDeath = Duration.between(
                            getTableView().getItems().get(getIndex()).getDateTimeOfDonation(),
                            LocalDateTime.now());
                    setText("N/A (" + getFormattedDuration(timeSinceDeath) + " since death)");

                } else if (item.isZero() || item.isNegative()
                        || item.equals(Duration.ZERO) || item.minusSeconds(1).isNegative()) {
                    // Duration is less than 1 second
                    setText("0 seconds");

                } else {
                    // Split duration string into words, e.g. ["3", "days", "2", "hours", "10", "minutes",...]
                    // It then takes the first 4 words (except for seconds, then it just takes up to the seconds)
                    // and stores that in displayedDuration, e.g. "3 days 2 hours"
                    String displayedDuration = getFormattedDuration(item);

                    // Progress as a decimal. starts at 0 (at time of death) and goes to 1.
                    double progressDecimal = getTableView().getItems().get(getIndex()).getProgressDecimal();
                    double fullMarker = getTableView().getItems().get(getIndex()).getFullMarker();

                    // Calculate colour
                    String style = getStyleForProgress(progressDecimal, fullMarker);

                    if (progressDecimal >= fullMarker) {
                        this.setTextFill(Color.WHITE);
                        if (this.isSelected()) {
                            this.setTextFill(Color.BLACK);
                        }
                    } else {
                        this.setTextFill(Color.BLACK);
                        if (this.isSelected()) {
                            this.setTextFill(Color.WHITE);
                        }
                    }

                    setText(displayedDuration);
                    setStyle(style);
                }
            }
        };
    }

    /**
     * Generates a stylesheet instruction for setting the background colour of a cell.
     * The colour is based on progressForColour, and how much the cell is filled in is based on totalProgress.
     * @param progress how far along the bar should be, from 0 to 1: 0 maps to empty, and 1 maps to full
     * @param fullMarker how far along the lower bound starts; this area will be grey (e.g. 0.9 for near the end)
     * @return stylesheet instruction, in the form "-fx-background-color: linear-gradient(...)"
     */
    private static String getStyleForProgress(double progress, double fullMarker) {

        String green;
        String red;
        String blue = "00"; // no blue

        double lowerPercent;
        double higherPercent;
        double progressForColour;
        String maroonColour = "aa0000";
        String whiteColour = "transparent";
        String greyColour = "aaaaaa";
        String middleColour;

        double percent = progress * 100;
        if (percent < 0.01) {
            percent = 0;
        }

        // Calculate percentages and the middle colour (white if it's not reached lower bound, maroon if it has)
        if (progress < fullMarker) { // Hasn't reached lower bound yet
            progressForColour = progress / fullMarker;
            lowerPercent = percent;
            higherPercent = fullMarker * 100;
            middleColour = whiteColour;
        } else { // In lower bound
            progressForColour = 1;
            lowerPercent = fullMarker * 100;
            higherPercent = percent;
            middleColour = maroonColour;
        }

        // Calculate colours
        if (progressForColour < 0.5) { // less than halfway, mostly green
            int redNumber = (int) Math.round(progressForColour * 255 * 2);
            red = Integer.toHexString(redNumber);
            if (red.length() == 1) {
                red = "0" + red;
            }
            green = "ff";

        } else { // over halfway, mostly red
            red = "ff";
            int greenNumber = (int) Math.round((1 - progressForColour) * 255 * 2);
            green = Integer.toHexString(greenNumber);
            if (green.length() == 1) {
                green = "0" + green;
            }
        }

        // Generate style string
        String colour = red + green + blue;
        return String.format("-fx-background-color: "
                        + "linear-gradient(to right, #%s 0%%, #%s %s%%, %s %s%%, %s %s%%, #%s %s%%, #%s 100%%);",
                colour, colour, lowerPercent, middleColour, lowerPercent, middleColour, higherPercent,
                greyColour, higherPercent, greyColour);
    }

    /**
     * Returns the duration, formatted to display x hours, y minutes (or x hours, y seconds if there are less than 60
     * seconds).
     * @param duration the duration to format
     * @return the formatted string
     */
    private static String getFormattedDuration(Duration duration) {
        String formattedDuration = duration.toHours() + " hours ";
        long minutes = duration.toMinutes() % 60;
        if (minutes != 0) { // has some minutes
            formattedDuration += minutes + " minutes";
        } else { // no minutes, just seconds (and perhaps hours)
            formattedDuration += duration.getSeconds() % 3600 + " seconds";
        }
        return formattedDuration;
    }

    /* TODO this is for pagination
     * Set the text that advises the currently viewed and pending amount of results
     * @param totalCount The total amount of current results matching filter options
     *
    private void setupDisplayingXToYOfZText(int totalCount) {
        int fromIndex = pagination.getCurrentPageIndex() * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, totalCount);
        if (totalCount < 2 || fromIndex + 1 == toIndex) {
            // 0 or 1 items OR the last item, on its own page
            displayingXToYOfZText.setText(String.format("Displaying %d of %d",
                    totalCount,
                    totalCount));
        } else {
            displayingXToYOfZText.setText(String.format("Displaying %d-%d of %d",
                    fromIndex + 1, toIndex,
                    totalCount));
        }
    }*/
}
