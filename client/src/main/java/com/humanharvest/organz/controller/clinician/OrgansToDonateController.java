package com.humanharvest.organz.controller.clinician;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.Notifications;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.Hospital;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.controller.components.ClientFullNameCell;
import com.humanharvest.organz.controller.components.DurationUntilExpiryCell;
import com.humanharvest.organz.controller.components.FormattedLocalDateTimeCell;
import com.humanharvest.organz.controller.components.TouchAlertTextController;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.Session;
import com.humanharvest.organz.state.Session.UserType;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.DonatedOrganSortOptionsEnum;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.exceptions.IfMatchFailedException;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.utilities.view.WindowContext.WindowContextBuilder;
import com.humanharvest.organz.views.client.DonatedOrganView;
import com.humanharvest.organz.views.client.PaginatedDonatedOrgansList;
import com.humanharvest.organz.views.clinician.DonatedOrganSortPolicy;

public class OrgansToDonateController extends SubController {

    private static final int ROWS_PER_PAGE = 30;
    private static final Logger LOGGER = Logger.getLogger(OrgansToDonateController.class.getName());
    private static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("d MMM yyyy hh:mm a");

    private final Session session;
    private final ClientManager manager;

    private final ObservableList<DonatedOrgan> observableOrgansToDonate = FXCollections.observableArrayList();
    private final SortedList<DonatedOrgan> sortedOrgansToDonate = new SortedList<>(observableOrgansToDonate);

    private final ObservableList<Hospital> hospitals = FXCollections.observableArrayList();
    private final FilteredList<Hospital> filteredHospitals = new FilteredList<>(hospitals);
    private final SortedList<Hospital> sortedHospitals = new SortedList<>(filteredHospitals);

    @FXML
    private Pane menuBarPane;

    @FXML
    private TableView<DonatedOrgan> tableView;
    @FXML
    private TableColumn<DonatedOrgan, String> clientCol;
    @FXML
    private TableColumn<DonatedOrgan, Organ> organCol;
    @FXML
    private TableColumn<DonatedOrgan, String> regionCol;
    @FXML
    private TableColumn<DonatedOrgan, LocalDateTime> timeOfDeathCol;
    @FXML
    private TableColumn<DonatedOrgan, Duration> timeUntilExpiryCol;
    @FXML
    private ListView<Client> potentialRecipients;
    @FXML
    private Pagination pagination;
    @FXML
    private CheckComboBox<String> regionFilter;
    @FXML
    private CheckComboBox<Organ> organFilter;
    @FXML
    private Text displayingXToYOfZText;
    @FXML
    private Button scheduleTransplantBtn;
    @FXML
    private DatePicker transplantDatePicker;
    @FXML
    private ChoiceBox<Hospital> transplantHospitalChoice;

    private DonatedOrgan selectedOrgan;
    private Set<String> regionsToFilter;
    private EnumSet<Organ> organsToFilter;

    private Label placeholder;

    /**
     * Gets the client manager from the global state.
     */
    public OrgansToDonateController() {
        session = State.getSession();
        manager = State.getClientManager();
    }

    // ---------------- Setup methods ----------------

    /**
     * Sets up the page, setting its title, loading the menu bar and doing the first refresh of the data.
     *
     * @param mainController The main controller that defines which window this subcontroller belongs to.
     */
    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.setTitle("Organs to Donate");

        mainController.loadNavigation(menuBarPane);
        refresh();
    }

    /**
     * Initializes the page and the table/pagination properties.
     */
    @FXML
    private void initialize() {
        setupOrgansTable();
        placeholder = new Label("Select an available organ to show potential recipients");
        placeholder.setWrapText(true);
        placeholder.setPadding(new Insets(0, 0, 0, 8));
        placeholder.setTextFill(Color.GREY);
        potentialRecipients.setPlaceholder(placeholder);

        potentialRecipients.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super Client>)
                change -> handlePotentialRecipientChange());
        handlePotentialRecipientChange();

        tableView.setOnSort(event -> updateOrgansToDonateList());

        for (Region region : Region.values()) {
            regionFilter.getItems().add(region.toString());
        }
        regionFilter.getItems().add("International");

        organFilter.getItems().setAll(Organ.values());

        regionFilter.getCheckModel().getCheckedItems().addListener(
                (ListChangeListener<String>) change -> updateOrgansToDonateList());

        organFilter.getCheckModel().getCheckedItems().addListener(
                (ListChangeListener<Organ>) change -> updateOrgansToDonateList());

        //On pagination update call createPage
        pagination.setPageFactory(this::createPage);

        // Sort by time until expiry column by default.
        tableView.getSortOrder().clear();
        tableView.getSortOrder().add(timeUntilExpiryCol);

        // Setup transplant hospital choice picker
        hospitals.setAll(State.getConfigManager().getHospitals());
        transplantHospitalChoice.setItems(sortedHospitals);
    }

    /**
     * Upon pagination, update the table to show the correct items
     *
     * @return An empty pane as pagination requires a non null return. Not used.
     */
    private Node createPage(int ignored) {
        updateOrgansToDonateList();
        return new Pane();
    }

    /**
     * Sets up the table columns with their respective value factories and representation factories. Also registers a
     * mouse event handler for double-clicking on a record in the table to open up the appropriate client profile.
     */
    private void setupOrgansTable() {
        clientCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getDonor().getFullName()));
        organCol.setCellValueFactory(new PropertyValueFactory<>("organType"));
        regionCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getDonor().getRegionOfDeath()));
        timeOfDeathCol.setCellValueFactory(new PropertyValueFactory<>("dateTimeOfDonation"));
        timeUntilExpiryCol.setCellValueFactory(new PropertyValueFactory<>("durationUntilExpiry"));

        // Format all the datetime cells
        timeOfDeathCol.setCellFactory(cell -> new FormattedLocalDateTimeCell<>(dateTimeFormat));
        timeUntilExpiryCol.setCellFactory(DurationUntilExpiryCell::new);

        potentialRecipients.setCellFactory(listView -> new ClientFullNameCell());

        // Open the client profile when double-clicked
        potentialRecipients.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
                Client client = potentialRecipients.getSelectionModel().getSelectedItem();
                if (client != null) {
                    MainController newMain = PageNavigator.openNewWindow(mainController);
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

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Showing potential matches for the donated organ
            potentialRecipients.getItems().clear();
            if (newValue != null) {
                displayMatches(newValue);
            }
        });

        // Register the mouse event for double-clicking on a record to open the client profile.
        tableView.setOnMouseClicked(mouseEvent -> {

            // Double clicking brings up the profile of the client who has donated the organ
            if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
                DonatedOrgan organToDonate = tableView.getSelectionModel().getSelectedItem();
                if (organToDonate != null) {
                    Client client = organToDonate.getDonor();
                    MainController newMain = PageNavigator.openNewWindow(mainController);
                    if (newMain != null) {
                        newMain.setWindowContext(new WindowContextBuilder()
                                .setAsClinicianViewClientWindow()
                                .viewClient(client)
                                .build());
                        PageNavigator.loadPage(Page.VIEW_CLIENT, newMain);
                    }
                }

                // Right click to allow manual override of organ expiry
            } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                MenuItem manualExpireItem = new MenuItem();
                manualExpireItem.textProperty().setValue("Manually Override");
                selectedOrgan = tableView.getSelectionModel().getSelectedItem();
                manualExpireItem.setOnAction(event -> openManuallyExpireDialog());
                ContextMenu contextMenu = new ContextMenu(manualExpireItem);
                tableView.setContextMenu(contextMenu);
            }
        });

        // Attach timer to update table each second (for time until expiration)
        Timeline clock = new Timeline(new KeyFrame(
                javafx.util.Duration.millis(1000),
                event -> {
                    tableView.refresh();
                    observableOrgansToDonate.removeIf(donatedOrgan ->
                            donatedOrgan.getOverrideReason() != null ||
                                    donatedOrgan.getDurationUntilExpiry() != null &&
                                            donatedOrgan.getDurationUntilExpiry().minusSeconds(1).isNegative());
                }));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

        // Sets the comparator for sorting by organ column to alphabetical order of the organ name.
        organCol.setComparator(Comparator.comparing(Organ::toString));

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

        sortedOrgansToDonate.comparatorProperty().bind(tableView.comparatorProperty());
    }

    /**
     * Filters the regions based on the RegionChoices current state and updates the organsToFilter Collection.
     */
    private void filterRegions() {
        regionsToFilter = new HashSet<>();
        regionsToFilter.addAll(
                regionFilter.getCheckModel().getCheckedItems());
    }

    /**
     * Filters the organs based on the OrganChoices current state and updates the organsToFilter Collection.
     */
    private void filterOrgans() {
        organsToFilter = EnumSet.noneOf(Organ.class);
        organsToFilter.addAll(organFilter.getCheckModel().getCheckedItems());
    }

    private void updateOrgansToDonateList() {
        DonatedOrganSortPolicy sortPolicy = getSortPolicy();
        filterOrgans();
        filterRegions();

        PaginatedDonatedOrgansList newOrgansToDonate = manager.getAllOrgansToDonate(
                pagination.getCurrentPageIndex() * ROWS_PER_PAGE,
                ROWS_PER_PAGE,
                regionsToFilter,
                organsToFilter,
                sortPolicy.getSortOption(),
                sortPolicy.isReversed());

        observableOrgansToDonate.setAll(newOrgansToDonate.getDonatedOrgans().stream()
                .map(DonatedOrganView::getDonatedOrgan)
                .collect(Collectors.toList()));

        int newPageCount = Math.max(1, (newOrgansToDonate.getTotalResults() + ROWS_PER_PAGE - 1) / ROWS_PER_PAGE);
        if (pagination.getPageCount() != newPageCount) {
            pagination.setPageCount(newPageCount);
        }

        setupDisplayingXToYOfZText(newOrgansToDonate.getTotalResults());
    }

    private void scheduleOptionsEnabled(boolean state) {
        scheduleTransplantBtn.setDisable(!state);
        transplantDatePicker.setDisable(!state);
        transplantHospitalChoice.setDisable(!state);
    }

    private void handlePotentialRecipientChange() {
        List<Client> selectedClients = potentialRecipients.getSelectionModel().getSelectedItems();
        if (selectedClients.size() != 1) {
            // Not a single potential recipient selected
            scheduleOptionsEnabled(false);

            transplantHospitalChoice.setValue(null);
            transplantDatePicker.setValue(null);
        } else {
            // A single recipient is selected

            Client selected = potentialRecipients.getSelectionModel().getSelectedItem();
            DonatedOrgan organToDonate = tableView.getSelectionModel().getSelectedItem();
            filteredHospitals.setPredicate(hospital -> hospital.hasTransplantProgram(organToDonate.getOrganType()));

            if (filteredHospitals.isEmpty()) {
                // No valid hospitals for this transplant program
                scheduleOptionsEnabled(false);
                return;
            }

            scheduleOptionsEnabled(true);

            Hospital clientHospital = Hospital.getHospitalForClient(selected, hospitals);
            if (clientHospital == null) {
                // Recipient has no region, just sort hospitals in alphabetical order instead
                sortedHospitals.setComparator(Comparator.comparing(Hospital::getName));
            } else {
                Hospital nearestWithProgram = clientHospital
                        .getNearestWithTransplantProgram(organToDonate.getOrganType(), filteredHospitals);

                transplantHospitalChoice.setValue(nearestWithProgram);

                transplantDatePicker.setValue(LocalDateTime.now()
                        .plus(nearestWithProgram.calculateTimeTo(selected.getHospital())).toLocalDate());

                sortedHospitals.setComparator(Comparator.comparing(
                        hospital -> hospital.calculateTimeTo(nearestWithProgram)));
            }
        }
    }

    @FXML
    private void scheduleTransplant() {
        // First make the TransplantRequest
        List<Client> selectedClients = potentialRecipients.getSelectionModel().getSelectedItems();
        if (selectedClients.size() != 1) {
            return;
        }

        LocalDate transplantDate = transplantDatePicker.getValue();
        if (transplantDate == null || transplantDate.isBefore(LocalDate.now())) {
            PageNavigator.showAlert(AlertType.ERROR,
                    "Invalid Transplant Date",
                    "Please enter a valid date for the transplant to take place. "
                            + "A valid date is one that is today or in the future.",
                    mainController.getStage());
            return;
        }

        Hospital transplantHospital = transplantHospitalChoice.getValue();
        if (transplantHospital == null) {

            PageNavigator.showAlert(AlertType.ERROR,
                    "Missing Transplant Hospital",
                    "Please select a hospital for the transplant to take place in.",
                    mainController.getStage());
            return;
        }

        DonatedOrgan organToDonate = tableView.getSelectionModel().getSelectedItem();

        Client receiver = selectedClients.get(0);
        List<TransplantRequest> requests = State.getClientResolver().getTransplantRequests(receiver);

        Optional<TransplantRequest> optionalRequest = requests.stream()
                .filter(r -> r.getRequestedOrgan() == organToDonate.getOrganType())
                .findFirst();
        if (!optionalRequest.isPresent()) {
            PageNavigator.showAlert(AlertType.ERROR,
                    "Could not find the transplant",
                    "The specified transplant request could not be located, maybe it was deleted?",
                    mainController.getStage());
            return;
        }

        TransplantRequest request = optionalRequest.get();

        if (!transplantHospital.hasTransplantProgram(request.getRequestedOrgan())) {
            PageNavigator.showAlert(AlertType.CONFIRMATION,
                    "Unsupported organ transplant location",
                    "The hospital does not support this type of organ transplant. Do you wish to continue?",
                    mainController.getStage(),
                    isOk -> {
                        if (isOk) {
                            finishTransplantRequest(request, organToDonate,
                                    transplantHospital, transplantDate, receiver);
                        }
                    });

            return;
        }
        finishTransplantRequest(request, organToDonate, transplantHospital, transplantDate, receiver);
    }

    private void finishTransplantRequest(TransplantRequest request, DonatedOrgan organToDonate,
            Hospital transplantHospital, LocalDate transplantDate, Client receiver) {
        // Refresh the client to get the latest etag
        request.setClient(State.getClientManager()
                .getClientByID(request.getClient().getUid())
                .orElseThrow(IllegalStateException::new));

        State.getClientResolver().scheduleTransplantProcedure(organToDonate, request,
                transplantHospital, transplantDate);
        Notifications.create()
                .title("Scheduled Transplant")
                .text(String.format("A transplant for %s from %s to %s has been scheduled on %s.",
                        request.getRequestedOrgan(), organToDonate.getDonor().getFullName(), receiver.getFullName(),
                        transplantDate))
                .showInformation();

        // Reset values
        transplantDatePicker.setValue(null);

        PageNavigator.refreshAllWindows();
    }

    /**
     * Used to detect the current sort policy of the table and convert it to a value that the server will understand.
     *
     * @return A {@link DonatedOrganSortPolicy} that maps to one of the SortOptions and a boolean if the sort should
     * be reversed.
     */
    private DonatedOrganSortPolicy getSortPolicy() {
        ObservableList<TableColumn<DonatedOrgan, ?>> sortOrder = tableView.getSortOrder();
        if (sortOrder.isEmpty()) {
            return new DonatedOrganSortPolicy(DonatedOrganSortOptionsEnum.TIME_UNTIL_EXPIRY, false);
        }
        TableColumn<DonatedOrgan, ?> sortColumn = tableView.getSortOrder().get(0);

        DonatedOrganSortOptionsEnum sortOption;
        switch (sortColumn.getId()) {
            case "clientCol":
                sortOption = DonatedOrganSortOptionsEnum.CLIENT;
                break;
            case "organCol":
                sortOption = DonatedOrganSortOptionsEnum.ORGAN_TYPE;
                break;
            case "regionCol":
                sortOption = DonatedOrganSortOptionsEnum.REGION_OF_DEATH;
                break;
            case "timeOfDeathCol":
                sortOption = DonatedOrganSortOptionsEnum.TIME_OF_DEATH;
                break;
            case "timeUntilExpiryCol":
                sortOption = DonatedOrganSortOptionsEnum.TIME_UNTIL_EXPIRY;
                break;
            default:
                sortOption = DonatedOrganSortOptionsEnum.TIME_UNTIL_EXPIRY;
                break;
        }
        return new DonatedOrganSortPolicy(sortOption, sortColumn.getSortType() == SortType.DESCENDING);
    }

    private void displayMatches(DonatedOrgan selectedOrgan) {
        try {
            boolean noPossibleHospital = State.getConfigManager().getHospitals()
                    .stream()
                    .noneMatch(hospital -> hospital.hasTransplantProgram(selectedOrgan.getOrganType()));

            if (noPossibleHospital) {
                potentialRecipients.setItems(FXCollections.emptyObservableList());
                placeholder.setText("There are no hospitals that can transplant this organ. "
                        + "Please contact your system administrator.");
            } else {
                List<Client> matches = State.getClientManager().getOrganMatches(selectedOrgan);
                potentialRecipients.setItems(FXCollections.observableArrayList(matches));

                if (matches.isEmpty()) {
                    placeholder.setText("No potential recipients for this organ");
                }
            }

        } catch (NotFoundException e) {
            LOGGER.log(Level.WARNING, "Organ not found", e);
            Notifications.create()
                    .title("Organ not found")
                    .text("The organ could not be found on the server, it may have been deleted")
                    .showWarning();
        } catch (ServerRestException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            Notifications.create()
                    .title("Server error")
                    .text("Could not access the server, please try again later")
                    .showError();
        }
    }

    private void openManuallyExpireDialog() {
        // Create a popup with a text field to enter the reason

        TouchAlertTextController controller = PageNavigator.showTextAlert("Manually Override Organ",
                "Enter the reason for overriding this organ:", mainController.getStage());

        if (controller.getResultProperty().getValue() != null) {
            if (controller.getResultProperty().getValue()) {
                overrideOrgan(controller.getText());
            }
        } else {
            controller.getResultProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    overrideOrgan(controller.getText());
                }
            });
        }
    }

    private void overrideOrgan(String reason) {
        try {
            StringBuilder overrideReason = new StringBuilder(reason);
            overrideReason.append("\n").append(LocalDateTime.now().format(dateTimeFormat));
            if (session.getLoggedInUserType() == UserType.CLINICIAN) {
                overrideReason.append(String.format("%nOverriden by clinician %d (%s)",
                        session.getLoggedInClinician().getStaffId(), session.getLoggedInClinician().getFullName()));
            } else if (session.getLoggedInUserType() == UserType.ADMINISTRATOR) {
                overrideReason.append(String.format("%nOverriden by admin '%s'.",
                        session.getLoggedInAdministrator().getUsername()));
            }
            State.getClientResolver().manuallyOverrideOrgan(selectedOrgan, overrideReason.toString());
            PageNavigator.refreshAllWindows();
        } catch (IfMatchFailedException exc) {
            // TODO deal with outdated error
        } catch (NotFoundException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            Notifications.create()
                    .title("Client/Organ Not Found")
                    .text("The client/donated organ could not be found on the server; it may have been deleted.")
                    .showWarning();
        } catch (ServerRestException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            Notifications.create()
                    .title("Server Error")
                    .text("A server error occurred when overriding this donated organ; please try again later.")
                    .showError();
        }
    }

    /**
     * Refreshes the data in the transplants waiting list table. Should be called whenever any page calls a global
     * refresh.
     */
    @Override
    public void refresh() {
        updateOrgansToDonateList();
        tableView.setItems(sortedOrgansToDonate);
    }

    // ---------------- Format methods ----------------

    private void setupDisplayingXToYOfZText(int totalCount) {
        int fromIndex = pagination.getCurrentPageIndex() * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, totalCount);
        if (totalCount < 2 || fromIndex + 1 == toIndex) {
            // 0 or 1 items OR the last item, on its own page
            displayingXToYOfZText.setText(String.format("Displaying %d of %d",
                    totalCount,
                    totalCount));
        } else {
            displayingXToYOfZText.setText(String.format("Displaying %d-%d of %d", fromIndex + 1, toIndex,
                    totalCount));
        }
    }
}
