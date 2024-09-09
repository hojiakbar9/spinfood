package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import controller.command.CommandHistory;
import controller.command.DissolvePairCommand;
import models.data.PairModel;
import models.data.ParticipantModel;
import models.service.PairsAndGroupsManager;
import models.service.PairsPerformanceIndicatorsCalculator;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class PairController {
    public BorderPane borderPane;
    public TabPane pairsTabPane;
    public TableView<PairModel> allPairsTableView;
    public TableView<ParticipantModel> successorParticipantsTableView;
    public ListView<String> indicatorsListView;
    public ListView<String> compareListView;
    private ObservableList<PairModel> allPairs;
    private ObservableList<ParticipantModel> successorParticipants;
    private final CommandHistory commandHistory = new CommandHistory();
    private List<List<PairModel>> pairListHistory = new ArrayList<>();
    @FXML
    private ComboBox<String> versionSelector;


    /**
 * Updates the UI components related to pairs management.
 * This method performs several key functions:
 * 1. Retrieves and displays the current list of pairs by calling {@link #getPairs()}.
 * 2. Retrieves and displays the list of successor participants by calling {@link #getSuccessorParticipants()}.
 * 3. Populates the tables for all pairs and successor participants with relevant data.
 * 4. Initializes performance indicators for pairs by calling {@link #initialiseIndicators()}.
 * 5. Initializes the list for comparing different versions of pairs by calling {@link #initialiseListForCompare()}.
 *
 * The method ensures that the UI is up-to-date with the latest data from the model.
 */
    @FXML
    public void update() {
        getPairs();
        getSuccessorParticipants();

        String[] allPairsFieldNames = new String[]{"participant1Name", "participant2Name", "jointRegistration"
                ,"kitchenLongitude","kitchenLatitude", "mainFoodPreference", "pairNumber","appetizerGroupNumber",
                "mainCourseGroupNumber", "dessertGroupNumber","kitchenSupplier", "cookingCourse"
        };
        String[] successorParticipantFieldNames = new String[]{"name", "foodPreferences", "age", "sex", "hasKitchen",
        "story", "longitude", "latitude", "name2", "age2", "sex2"};
        // initialisiert die spalten der tableviews mit den entsprecheneden feldern
        populateColumns(allPairsFieldNames, allPairsTableView, allPairs);
        populateColumns(successorParticipantFieldNames, successorParticipantsTableView, successorParticipants);

        initialiseIndicators();
        initialiseListForCompare();
    }
    /**
 * Populates the columns of a TableView with data from an ObservableList.
 * This method dynamically assigns the cell value factories to the columns based on the provided field names,
 * allowing for flexible assignment of data properties to the TableView columns.
 *
 * @param fieldNames An array of strings representing the field names of the data model. These names should match
 *                   the property names in the data model class and are used to reflectively link the data to the
 *                   TableView columns.
 * @param tableView  The TableView instance to be populated. It is assumed that this TableView has been
 *                   pre-configured with the correct number of columns, and each column is intended to display
 *                   the data field corresponding to the field name at the same index in the fieldNames array.
 * @param items      The ObservableList containing the data items to be displayed in the TableView. Each item in
 *                   this list should be an instance of the data model class that contains the fields specified
 *                   in the fieldNames array.
 * @param <T>        The type parameter of the data model class that the TableView is intended to display.
 */
    private <T> void populateColumns(String[] fieldNames, TableView<T> tableView, ObservableList<T> items){
        for(int i = 0; i < tableView.getColumns().size();i++){
            tableView.getColumns().get(i).setCellValueFactory(new PropertyValueFactory<>(fieldNames[i]));
        }
        tableView.setItems(items);
    }

    /**
 * Handles the action to display the groups UI when the groups tab is clicked.
 * This method loads the groups FXML layout, initializes its controller, and sets it as the center of the main border pane.
 *
 */
    @FXML
    private void groups() {
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/groups.fxml")));
            root = loader.load();
            loader.getController();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        borderPane.setCenter(root);
    }

    /**
 * Handles the action to display the pairs UI when the pairs tab is clicked.
 * This method sets the pairs tab pane as the center of the main border pane, effectively
 * switching the view to display all pair-related information and controls.
 *
 */
    @FXML
    private void pairs(){
        borderPane.setCenter(pairsTabPane);
    }

    /**
 * Displays the criteria selection UI when the corresponding event is triggered.
 * This method is responsible for loading the criteria FXML layout, initializing its controller,
 * and setting it as the center of the main application's border pane. It is typically invoked
 * when a user interacts with a UI element designated for criteria selection, such as a button or menu item.
 *
 *              to capture and respond to the specific user action, ensuring the correct UI response.
 */
    @FXML
    private void showCriterias() {
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/criteria.fxml")));
            root = loader.load();
            CriteriaController criteriaController = loader.getController();
            criteriaController.setPairController(this);

            FXMLLoader groupsLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/groups.fxml")));
            groupsLoader.load();
            GroupsController groupsController = groupsLoader.getController();
            criteriaController.setGroupsController(groupsController);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        borderPane.setCenter(root);
    }

    private void getPairs() {
        allPairs = FXCollections.observableArrayList();
        List<PairModel> pairs = PairsAndGroupsManager.getInstance().getPairs();
        allPairs.addAll(pairs);
    }

    private void getSuccessorParticipants() {
        successorParticipants = FXCollections.observableArrayList();
        var participants = PairsAndGroupsManager.getInstance().getSuccessorParticipants();
        successorParticipants.addAll(participants);
    }

    /**
 * Initializes performance indicators for pairs.
 * This method calculates performance indicators for the current pairs and successor participants,
 * then updates the indicatorsListView with these calculated values. The performance indicators are
 * designed to evaluate the effectiveness or suitability of the pairings.
 *
 * The calculation is performed by creating an instance of PairsPerformanceIndicatorsCalculator,
 * which takes the current pairs and successor participants as input. After calculation,
 * the results are displayed in the indicatorsListView for the user to review.
 */
    private void initialiseIndicators() {
        PairsPerformanceIndicatorsCalculator calculator =  new PairsPerformanceIndicatorsCalculator(PairsAndGroupsManager.getInstance().getPairs(),
               PairsAndGroupsManager.getInstance().getSuccessorParticipants());
        calculator.calculatePerformanceIndicators();
        String[] indicators = calculator.getPerformanceIndicators();
       ObservableList<String> observableList = FXCollections.observableArrayList(indicators);
       indicatorsListView.setItems(observableList);
    }

    /**
 * Initializes the list for comparing different versions of pairs.
 * This method is responsible for maintaining a history of all pairs configurations and allowing the user
 * to select and view different versions from a ComboBox. It performs several key operations:
 * 1. Adds the current list of pairs to the history if it's not already the last entry, ensuring that each
 *    version is unique and sequentially stored.
 * 2. Populates the version selector ComboBox with the versions available in the history. Each entry in the
 *    ComboBox corresponds to a version of the pairs list, allowing users to select and view different versions.
 * 3. Sets up a listener for the version selector ComboBox to update the compareListView with the selected version
 *    of pairs, enabling users to compare the current list of pairs with previous versions.
 * 4. Automatically selects the latest version of pairs in the version selector ComboBox and displays it in the
 *    compareListView, ensuring that the user can immediately see the most recent configuration.
 * This method is crucial for providing a visual comparison feature, allowing users to track changes and revert
 * to previous configurations if necessary.
 */
    @FXML
    public void initialiseListForCompare() {
        // Add the current list of pairs to the history if it's not already the last entry
        if (pairListHistory.isEmpty() || !pairListHistory.get(pairListHistory.size() - 1).equals(allPairs)) {
            pairListHistory.add(new ArrayList<>(allPairs));
        }
        // Populate the version selector if it's empty or the number of versions has changed
        if (versionSelector.getItems().isEmpty() || versionSelector.getItems().size() != pairListHistory.size()) {
            ObservableList<String> versions = FXCollections.observableArrayList();
            for (int i = 0; i < pairListHistory.size(); i++) {
                versions.add("Version " + (i + 1));
            }
            versionSelector.setItems(versions);
            versionSelector.getSelectionModel().selectLast(); // Automatically select the latest version
        }

        // Listener for version selection changes
        versionSelector.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            // Get the index of the selected version
            int selectedVersionIndex = versionSelector.getSelectionModel().getSelectedIndex();
            // Check if the selected index is valid before attempting to access the list
            if (selectedVersionIndex >= 0) {
                List<PairModel> selectedVersionPairs = pairListHistory.get(selectedVersionIndex);

                // Convert the selected version of pairs into a suitable format for display
                ObservableList<String> observableList = FXCollections.observableArrayList();
                for (PairModel pair : selectedVersionPairs) {
                    observableList.add(pair.toString());
                }
                compareListView.setItems(observableList);
            }
        });

        // Display the latest version of pairs by default
        if (!pairListHistory.isEmpty()) {
            List<PairModel> latestVersionPairs = pairListHistory.get(pairListHistory.size() - 1);
            ObservableList<String> observableList = FXCollections.observableArrayList();
            for (PairModel pair : latestVersionPairs) {
                observableList.add(pair.toString());
            }
            compareListView.setItems(observableList);
        }
    }

    /**
 * Opens a dialog window to compare different versions of pairs.
 * This method is responsible for loading the "comparePairs.fxml" layout, initializing its controller,
 * and populating the version selector ComboBox with available versions of pair configurations.
 * Users can select a version to view detailed comparisons between different versions of pairs,
 * facilitating analysis and decision-making regarding pair configurations.
 *
 * The dialog displays a ComboBox for version selection and a detailed view of the selected version's
 * pair configurations. It allows users to visually compare changes across versions, aiding in understanding
 * the impact of modifications or the evolution of pairings over time.
 */
    @FXML
    public void comparePairs() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/comparePairs.fxml"));
            Parent root = loader.load();
            ComparePairsDialogController dialogController = loader.getController();
            ObservableList<String> versions = FXCollections.observableArrayList();
            for (int i = 0; i < pairListHistory.size(); i++) {
                versions.add("Version " + (i + 1));
            }
            dialogController.setPairComboBox(versions);
            dialogController.setPairListHistory(pairListHistory);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Compare Pairs");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void initialize() {
        update();
    }

    /**
 * Deletes the selected pair from the table view after confirmation.
 * If a pair is selected, a confirmation dialog is shown. Upon user confirmation, the pair is deleted using
 * the DissolvePairCommand, and the UI is updated. If no pair is selected, an informational alert prompts the user
 * to make a selection.
 */
    @FXML
    private void deletePair() {
        PairModel selectedPair = allPairsTableView.getSelectionModel().getSelectedItem();
        if (selectedPair != null) {
            int pairNumber = selectedPair.getPairNumber();

            // Show a confirmation dialog
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Deletion");
            confirmationAlert.setHeaderText(null);
            confirmationAlert.setContentText("Are you sure you want to delete the pair " + pairNumber + "?");

            // Wait for the user's response
            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // User chose OK, execute the deletion
                commandHistory.execute(new DissolvePairCommand(selectedPair, allPairs, successorParticipants));

                // Show an alert after successful deletion
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Deletion Successful");
                successAlert.setHeaderText(null);
                successAlert.setContentText("The pair " + pairNumber + " has been successfully deleted.");
                successAlert.showAndWait();
            }
        } else {
            // Show an alert if no pair is selected
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a pair to delete.");
            alert.showAndWait();
        }
    }


    /**
     * Reassembles a pair of participants. The pair is selected from the successor participants table view.
     */
    @FXML
    private void reassemblePair() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/reassemble_pair_dialog.fxml"));
            Parent root = loader.load();
            ReassemblePairDialogController dialogController = loader.getController();
            dialogController.setSuccessorParticipants(successorParticipants);
            dialogController.setCommandHistory(commandHistory);
            dialogController.setAllPairs(allPairs);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Reassemble Pair");
            dialogStage.setScene(new Scene(root));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
 * Opens a dialog for editing a selected pair's details.
 * If a pair is selected, the "edit_pair_dialog.fxml" is loaded and displayed in a modal dialog.
 * The dialog allows for editing and updating the pair's details. Upon closing, the UI is refreshed
 * to reflect any changes. If no pair is selected, an alert prompts the user to select one.
 */
    @FXML
    private void editPair() {
        PairModel selectedPair = allPairsTableView.getSelectionModel().getSelectedItem();
        if (selectedPair != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/edit_pair_dialog.fxml"));
                Parent root = loader.load();
                EditPairDialogController dialogController = loader.getController();
                dialogController.setPair(selectedPair);
                dialogController.setCommandHistory(commandHistory);
                dialogController.setAllPairs(allPairs);

                Stage dialogStage = new Stage();
                dialogStage.setTitle("Edit Pair");
                dialogStage.setScene(new Scene(root));
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.showAndWait();

                // Update the table view to reflect changes
                allPairsTableView.refresh();
                int pairNumber = selectedPair.getPairNumber();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Edition Successful");
                alert.setHeaderText(null);
                alert.setContentText("The pair " + pairNumber + " has been successfully updated.");
                alert.showAndWait();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            // Show an alert if no pair is selected
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a pair to edit.");
            alert.showAndWait();
        }
    }



    @FXML
    private void undo() {
        commandHistory.undo();
    }

    @FXML
    private void redo() {
        commandHistory.redo();
    }



}
