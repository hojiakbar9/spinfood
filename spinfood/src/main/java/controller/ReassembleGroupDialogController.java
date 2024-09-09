package controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import controller.command.CommandHistory;
import controller.command.ReassembleGroupCommand;
import models.data.GroupModel;
import models.data.PairModel;

public class ReassembleGroupDialogController {
    @FXML
    private ComboBox<PairModel> participant1ComboBox;

    @FXML
    private ComboBox<PairModel> participant2ComboBox;
    @FXML
    private ComboBox<PairModel> participant3ComboBox;

    private ObservableList<PairModel> successorParticipants;
    private CommandHistory commandHistory;
    private ObservableList<GroupModel> allGroups;

    public void setSuccessorPairs(ObservableList<PairModel> successorPairs) {
        this.successorParticipants = successorPairs;
        participant1ComboBox.setItems(successorPairs);
        participant2ComboBox.setItems(successorPairs);
        participant3ComboBox.setItems(successorPairs);
    }

    public void setCommandHistory(CommandHistory commandHistory) {
        this.commandHistory = commandHistory;
    }

    public void setAllGroups(ObservableList<GroupModel> allGroups) {
        this.allGroups = allGroups;
    }

    @FXML
    private void handleReassembleGroup() {
        PairModel pair1 = participant1ComboBox.getSelectionModel().getSelectedItem();
        PairModel pair2 = participant2ComboBox.getSelectionModel().getSelectedItem();
        PairModel pair3 = participant3ComboBox.getSelectionModel().getSelectedItem();

        if (pair1 != null && pair2 != null && pair3 != null && pair1 != pair2 && pair1 != pair3 && pair2 != pair3) {
            commandHistory.execute(new ReassembleGroupCommand(pair1, pair2, pair3, allGroups, successorParticipants));
            Stage stage = (Stage) participant1ComboBox.getScene().getWindow();
            stage.close();
        } else {
            // Display error message or highlight invalid selections
            showErrorAlert();
        }
    }
    private void showErrorAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Selection");
        alert.setHeaderText("Invalid participant selection");
        alert.setContentText("Please ensure all three participants are selected and are unique.");
        alert.showAndWait();
    }
}
