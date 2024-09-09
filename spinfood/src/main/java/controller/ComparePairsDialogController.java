package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import models.data.PairModel;
import models.service.PairsPerformanceIndicatorsCalculator;

import java.util.ArrayList;
import java.util.List;

public class ComparePairsDialogController {
    @FXML
    private ComboBox<String> pair1ComboBox;

    @FXML
    private ComboBox<String> pair2ComboBox;
    @FXML
    private Label comparisonResultLabel;

    @FXML
    private ListView<String> comparisonDetailsListView;

    @FXML
    private ListView<String> comparisonDetailsListView2;

    List<List<PairModel>> pairListHistory = new ArrayList<>();


    public void setPairComboBox(ObservableList<String> pairListHistoryComboBox) {
        this.pair1ComboBox.setItems(pairListHistoryComboBox);
        this.pair2ComboBox.setItems(pairListHistoryComboBox);
    }

    void setPairListHistory(List<List<PairModel>> pairListHistory) {
        this.pairListHistory = pairListHistory;
    }


    @FXML
    public void initialize() {
    }

    /**
 * Handles the comparison of pairs selected from two ComboBoxes.
 * This method is triggered by a mouse event, typically a button click, initiating the comparison process
 * between two different sets of pairs selected by the user. It retrieves the selected indices from both
 * ComboBoxes, validates that two distinct selections have been made, and then proceeds to compare the
 * performance indicators of the selected pairs.
 *
 * The comparison involves calculating performance indicators for each selected pair list using a
 * PairsPerformanceIndicatorsCalculator. The results are then displayed in two separate ListView controls,
 * allowing the user to visually compare these indicators side by side.
 *
 * If the user fails to select two different pairs or selects invalid options, an informational alert is
 * displayed, guiding them to make appropriate selections.
 *
 * @param actionEvent The mouse event that triggers the comparison process.
 */
    public void handleComparePairs(MouseEvent actionEvent) {
        int index1 = pair1ComboBox.getSelectionModel().getSelectedIndex();
        int index2 = pair2ComboBox.getSelectionModel().getSelectedIndex();

        if (index1 >= 0 && index2 >= 0 && index1 != index2) {
            List<PairModel> pairList1 = pairListHistory.get(index1);
            List<PairModel> pairList2 = pairListHistory.get(index2);


            PairsPerformanceIndicatorsCalculator calculatorForPairList1 = new PairsPerformanceIndicatorsCalculator(pairList1);
            calculatorForPairList1.calculatePerformanceIndicators2();
            String[] result = calculatorForPairList1.getPerformanceIndicators2();

            PairsPerformanceIndicatorsCalculator calculatorForPairList2 = new PairsPerformanceIndicatorsCalculator(pairList2);
            calculatorForPairList2.calculatePerformanceIndicators2();
            String[] result2 = calculatorForPairList2.getPerformanceIndicators2();

            // displaying comparison details
            ObservableList<String> listPair1Details = FXCollections.observableArrayList(result);
            ObservableList<String> listPair2Details = FXCollections.observableArrayList(result2);

            // Add actual comparison details here
            comparisonDetailsListView.setItems(listPair1Details);
            comparisonDetailsListView2.setItems(listPair2Details);
        }
        else {
            // show msg
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Information Dialog");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Please select two different pairs to compare.");
            successAlert.showAndWait();
        }
    }}
