package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import controller.command.CommandHistory;
import controller.command.DissolveGroupCommand;
import models.data.GroupModel;
import models.data.PairModel;
import models.enums.Courses;
import models.service.GroupPerformanceIndicatorsCalculator;
import models.service.PairsAndGroupsManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GroupsController {
    public TableView<GroupModel> dessertGroupsTable;
    public TableView<GroupModel> mainCourseGroupsTable;
    public TableView<GroupModel> appetizerGroupsTable;
    public TableView<PairModel> successorPairsTable;
    public ListView<String> performanceIndicatorsList;
   @FXML
   public TableView<GroupModel> allGroupsTable;
    private List<GroupModel> dessertGroups;
    private List<GroupModel> mainCourseGroups;
    private List<GroupModel> appetizerGroups;
    private List<PairModel> successorPairs;
    private ObservableList<PairModel> successorPairsAsObservable;
    private ObservableList<GroupModel> appetizerGroupsAsObservable;
    private ObservableList<GroupModel> mainCourseGroupsAsObservable;
    private ObservableList<GroupModel> dessertGroupsAsObservable;
    private ObservableList<GroupModel> allGroupsAsObservable;
    private final CommandHistory COMMANDHISTORY = new CommandHistory();
    private List<GroupModel> allGroups;

    @FXML
    private void initialize() {
         allGroups = PairsAndGroupsManager.getInstance().getGroups();
        dessertGroups = allGroups.stream()
                .filter(group -> group.getCourse().equals(Courses.DESSERT))
                .toList();
        mainCourseGroups = allGroups.stream()
                .filter(group -> group.getCourse().equals(Courses.MAIN))
                .toList();
         appetizerGroups = allGroups.stream()
                .filter(group -> group.getCourse().equals(Courses.APPETIZER))
                .toList();


         populateForCourseGroups(allGroupsTable);
         allGroupsAsObservable = FXCollections.observableArrayList(allGroups);
         allGroupsTable.setItems(allGroupsAsObservable);
         populateForCourseGroups(dessertGroupsTable);
        dessertGroupsAsObservable = FXCollections.observableArrayList(dessertGroups);
        dessertGroupsTable.setItems(dessertGroupsAsObservable);

        populateForCourseGroups(mainCourseGroupsTable);
        mainCourseGroupsAsObservable = FXCollections.observableArrayList(mainCourseGroups);
        mainCourseGroupsTable.setItems(mainCourseGroupsAsObservable);

        populateForCourseGroups(appetizerGroupsTable);
        appetizerGroupsAsObservable = FXCollections.observableArrayList(appetizerGroups);
        appetizerGroupsTable.setItems(appetizerGroupsAsObservable);

        populateSuccessorPairsTable();
        populateIndicatorsList();
    }
    private void populateForCourseGroups( TableView<GroupModel> groupTable){
        var columns =  groupTable.getColumns();
        var idColumn = columns.get(0);
        var firstPairColumn = columns.get(1);
        var secondPairColumn =  columns.get(2);
        var thirdPairColumn =  columns.get(3);
        var foodPreferenceColumn =  columns.get(4);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("groupID"));
        firstPairColumn.setCellValueFactory(new PropertyValueFactory<>("firstPair"));
        secondPairColumn.setCellValueFactory(new PropertyValueFactory<>("secondPair"));
        thirdPairColumn.setCellValueFactory(new PropertyValueFactory<>("thirdPair"));
        foodPreferenceColumn.setCellValueFactory(new PropertyValueFactory<>("groupFoodPreference"));
    }

    private void populateSuccessorPairsTable(){
        var columns = successorPairsTable.getColumns();
        var pairNumber = columns.get(0);
        var firstParticipant = columns.get(1);
        var secondParticipant = columns.get(2);
        var foodPreference = columns.get(3);
        var kitchenLongitude = columns.get(4);
        var kitchenLatitude = columns.get(5);

        pairNumber.setCellValueFactory(new PropertyValueFactory<>("pairNumber"));
        firstParticipant.setCellValueFactory(new PropertyValueFactory<>("participant1"));
        secondParticipant.setCellValueFactory(new PropertyValueFactory<>("participant2"));
        foodPreference.setCellValueFactory(new PropertyValueFactory<>("mainFoodPreference"));
        kitchenLongitude.setCellValueFactory(new PropertyValueFactory<>("kitchenLongitude"));
        kitchenLatitude.setCellValueFactory(new PropertyValueFactory<>("kitchenLatitude"));
        successorPairs = PairsAndGroupsManager.getInstance().getSuccessorPairs();
        successorPairsAsObservable = FXCollections.observableArrayList(successorPairs);
        successorPairsTable.setItems(successorPairsAsObservable);
    }
    private void populateIndicatorsList(){
       var dessertGroupsIndicatorsCalculator =  new GroupPerformanceIndicatorsCalculator(dessertGroups, successorPairs);
       var mainCourseGroupIndicatorsCalculator =  new GroupPerformanceIndicatorsCalculator(mainCourseGroups, successorPairs);
       var appetizerGroupsIndicatorsCalculator =  new GroupPerformanceIndicatorsCalculator(appetizerGroups, successorPairs);

       appetizerGroupsIndicatorsCalculator.calculatePerformanceIndicators();
       mainCourseGroupIndicatorsCalculator.calculatePerformanceIndicators();
       dessertGroupsIndicatorsCalculator.calculatePerformanceIndicators();

        ObservableList<String> results = getResultsForIndicators(appetizerGroupsIndicatorsCalculator,
                mainCourseGroupIndicatorsCalculator,
                dessertGroupsIndicatorsCalculator);

        performanceIndicatorsList.setItems(results);
    }

    private static ObservableList<String> getResultsForIndicators(
            GroupPerformanceIndicatorsCalculator appetizerGroupsIndicatorsCalculator,
            GroupPerformanceIndicatorsCalculator mainCourseGroupIndicatorsCalculator,
            GroupPerformanceIndicatorsCalculator dessertGroupsIndicatorsCalculator) {
        List<String> appetizerIndicators = new ArrayList<>(appetizerGroupsIndicatorsCalculator.getResults());
        List<String> mainIndicators = new ArrayList<>(mainCourseGroupIndicatorsCalculator.getResults());
        List<String> dessertIndicators = new ArrayList<>(dessertGroupsIndicatorsCalculator.getResults());
        appetizerIndicators.add(0, "Appetizer Groups Performance Indicators: ");
        appetizerIndicators.add(" ");

        mainIndicators.add(0, "Main Course Groups Performance Indicators: ");
        mainIndicators.add("");

        dessertIndicators.add(0, "Dessert Groups Performance Indicators: ");

        ObservableList<String> results = FXCollections.observableArrayList();
        results.addAll(appetizerIndicators);
        results.addAll(mainIndicators);
        results.addAll(dessertIndicators);

        return results;
    }

    public void update() {
        initialize();
    }



    public void deleteGroup() {
        GroupModel selectedGroup = dessertGroupsTable.getSelectionModel().getSelectedItem();
        if (selectedGroup != null) {
            COMMANDHISTORY.execute(new DissolveGroupCommand(selectedGroup, allGroups, successorPairs));
            update();
            return;
        }

        selectedGroup = appetizerGroupsTable.getSelectionModel().getSelectedItem();
        if (selectedGroup != null) {
            COMMANDHISTORY.execute(new DissolveGroupCommand(selectedGroup, allGroups, successorPairs));
            update();
            return;
        }

        selectedGroup = mainCourseGroupsTable.getSelectionModel().getSelectedItem();
        if (selectedGroup != null) {
            COMMANDHISTORY.execute(new DissolveGroupCommand(selectedGroup, allGroups, successorPairs));
            update();
        }
        selectedGroup = allGroupsTable.getSelectionModel().getSelectedItem();
        if (selectedGroup != null) {
            COMMANDHISTORY.execute(new DissolveGroupCommand(selectedGroup, allGroups, successorPairs));
            update();
        }
    }

    @FXML
    private void reassembleGroup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/reassemble_group.fxml"));
            Parent root = loader.load();
            ReassembleGroupDialogController dialogController = loader.getController();
            dialogController.setSuccessorPairs(successorPairsAsObservable);
            dialogController.setCommandHistory(COMMANDHISTORY);
            dialogController.setAllGroups(allGroupsAsObservable);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Reassemble Group");
            dialogStage.setScene(new Scene(root));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.showAndWait();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void undo() {
        COMMANDHISTORY.undo();
        update();
    }

    public void redo() {
        COMMANDHISTORY.redo();
        update();
    }



}

