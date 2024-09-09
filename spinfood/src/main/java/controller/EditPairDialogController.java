package controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import controller.command.CommandHistory;
import controller.command.EditPairCommand;
import models.data.PairModel;
import models.data.ParticipantModel;
import models.enums.Courses;
import models.enums.FoodPreferences;

public class EditPairDialogController {
    @FXML
    private TextField participant1NameField;
    @FXML
    private TextField participant2NameField;
    @FXML
    private TextField jointRegistrationField;
    @FXML
    private TextField kitchenLongitudeField;
    @FXML
    private TextField kitchenLatitudeField;
    @FXML
    private TextField mainFoodPreferenceField;
    @FXML
    private TextField pairNumberField;
    @FXML
    private TextField appetizerGroupNumberField;
    @FXML
    private TextField mainCourseGroupNumberField;
    @FXML
    private TextField dessertGroupNumberField;
    @FXML
    private TextField kitchenSupplierField;
    @FXML
    private TextField cookingCourseField;
    private CommandHistory commandHistory;
    private PairModel pair;
    private ObservableList<PairModel> allPairs;

    public void setPair(PairModel pair) {

        this.pair = pair;
        participant1NameField.setText(pair.getParticipant1Name());
        participant2NameField.setText(pair.getParticipant2Name());
        jointRegistrationField.setText(String.valueOf(pair.isJointRegistration()));
        kitchenLongitudeField.setText(String.valueOf(pair.getKitchenLongitude()));
        kitchenLatitudeField.setText(String.valueOf(pair.getKitchenLatitude()));
        mainFoodPreferenceField.setText(String.valueOf(pair.getMainFoodPreference()));
        pairNumberField.setText(String.valueOf(pair.getPairNumber()));
        appetizerGroupNumberField.setText(String.valueOf(pair.getAppetizerGroupNumber()));
        mainCourseGroupNumberField.setText(String.valueOf(pair.getMainCourseGroupNumber()));
        dessertGroupNumberField.setText(String.valueOf(pair.getDessertGroupNumber()));
        kitchenSupplierField.setText(String.valueOf(pair.getKitchenSupplier()));
        cookingCourseField.setText(String.valueOf(pair.getCookingCourse()));
    }

    @FXML
    private void handleSave() {
        ParticipantModel first = pair.getParticipant1();
        first.setName(participant1NameField.getText());
        ParticipantModel second = pair.getParticipant2();
        second.setName(participant2NameField.getText());
        pair.setJointRegistration(Boolean.getBoolean(jointRegistrationField.getText()));
        pair.setKitchenLongitude(Double.parseDouble(kitchenLongitudeField.getText()));
        pair.setKitchenLatitude(Double.parseDouble(kitchenLatitudeField.getText()));
        pair.setMainFoodPreference(FoodPreferences.valueOf(mainFoodPreferenceField.getText()));
        pair.setPairNumber(Integer.parseInt(pairNumberField.getText()));
        pair.setAppetizerGroupNumber(Integer.parseInt(appetizerGroupNumberField.getText()));
        pair.setMainCourseGroupNumber(Integer.parseInt(mainCourseGroupNumberField.getText()));
        pair.setDessertGroupNumber(Integer.parseInt(dessertGroupNumberField.getText()) );
        pair.setKitchenSupplier(Boolean.getBoolean(kitchenSupplierField.getText()));
        pair.setCookingCourse(Courses.valueOf(cookingCourseField.getText()));

        commandHistory.execute(new EditPairCommand(pair, pair, allPairs));

        Stage stage = (Stage) participant1NameField.getScene().getWindow();
        stage.close();
    }

    public void setCommandHistory(CommandHistory commandHistory) {
        this.commandHistory = commandHistory;
    }

    public void setAllPairs(ObservableList<PairModel> allPairs) {
        this.allPairs = allPairs;
    }
}
