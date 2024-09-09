package controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import controller.command.CommandHistory;
import controller.command.ReassemblePairCommand;
import models.data.PairModel;
import models.data.ParticipantModel;

public class ReassemblePairDialogController {
    @FXML
    private ComboBox<ParticipantModel> participant1ComboBox;

    @FXML
    private ComboBox<ParticipantModel> participant2ComboBox;

    private ObservableList<ParticipantModel> successorParticipants;
    private CommandHistory commandHistory;
    private ObservableList<PairModel> allPairs;

    public void setSuccessorParticipants(ObservableList<ParticipantModel> successorParticipants) {
        this.successorParticipants = successorParticipants;
        participant1ComboBox.setItems(successorParticipants);
        participant2ComboBox.setItems(successorParticipants);
    }

    public void setCommandHistory(CommandHistory commandHistory) {
        this.commandHistory = commandHistory;
    }

    public void setAllPairs(ObservableList<PairModel> allPairs) {
        this.allPairs = allPairs;
    }

    @FXML
    private void handleReassemblePair() {
        ParticipantModel participant1 = participant1ComboBox.getSelectionModel().getSelectedItem();
        ParticipantModel participant2 = participant2ComboBox.getSelectionModel().getSelectedItem();

        if (participant1 != null && participant2 != null && participant1 != participant2) {
            commandHistory.execute(new ReassemblePairCommand(participant1, participant2, allPairs, successorParticipants));
            Stage stage = (Stage) participant1ComboBox.getScene().getWindow();
            stage.close();
        } else {
            // Display error message or highlight invalid selections
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Invalid Selection");
            alert.setHeaderText(null);
            if (participant1 == null || participant2 == null) {
                alert.setContentText("Please select both participants to form a pair.");
            } else {
                alert.setContentText("Please select two different participants to form a pair.");
            }
            alert.showAndWait();
        }
        }
    }
