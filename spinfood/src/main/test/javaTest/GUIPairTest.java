import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import models.service.PairsAndGroupsManager;
import models.service.PairsPerformanceIndicatorsCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.service.query.EmptyNodeQueryException;
import org.testfx.service.query.PointQuery;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.assertions.api.Assertions.assertThat;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(ApplicationExtension.class)
public class GUIPairTest extends ApplicationTest  {
    @Override
    public void start(Stage stage) throws Exception {
        // Laden Sie Ihre Haupt-FXML-Datei
        Parent mainNode = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/pairs.fxml")));
        stage.setScene(new Scene(mainNode));
        stage.show();
    }
    /**
 * Tests the visibility of all main application windows or tabs by simulating user interactions.
 * This method navigates through the "Successor Participants", "Performance Indicators", and "All Pairs" tabs,
 * verifying that each respective UI element is visible after selection. It ensures that the main sections
 * of the application are accessible and displayed correctly to the user.
 *
 * @param robot FxRobot instance for simulating user interactions with the UI.
 */
    @Test
    void  testAllWindowsShowing(FxRobot robot){
        robot.clickOn("Successor Participants");
        robot.sleep(500);
        assertThat(robot.lookup("#successorParticipantsTableView").tryQuery().isPresent()).isTrue();
        robot.clickOn("Performance Indicators");
        robot.sleep(500);
        assertThat(robot.lookup("#indicatorsListView").tryQuery().isPresent()).isTrue();
        robot.clickOn("All Pairs");
        robot.sleep(500);
        assertThat(robot.lookup("#allPairsTableView").tryQuery().isPresent()).isTrue();
    }
    @Test
    void testAddPairWindowShowing(FxRobot robot) {
        // Simulieren Sie das Klicken auf den "Successor Participants" Tab
        robot.clickOn("Successor Participants");

        // Simulieren Sie das Klicken auf den "Add Pair" Button
        robot.clickOn("Add Pair");

        // Überprüfen Sie, ob das Dialogfenster angezeigt wird
        assertThat(robot.window("Reassemble Pair")).isShowing();
    }

    /**
     * Tests the functionality of adding two equal participants as a pair.
     * This test simulates the user interaction for adding a pair with the same participant for both positions.
     * It clicks through the UI to select the same participant for both the "Participant 1" and "Participant 2" dropdowns,
     * then attempts to submit the pair for reassembly. The expected outcome is that an alert dialog will pop up,
     * indicating that two different participants must be selected to form a pair. The test verifies this behavior
     * by checking the content of the alert dialog.
     *
     * @param robot FxRobot instance for simulating user interactions with the UI.
     */
    @Test
    void testAddTwoEqualPairs(FxRobot robot){
        // Simulieren Sie das Klicken auf den "Successor Participants" Tab
        robot.clickOn("Successor Participants");

        // Simulieren Sie das Klicken auf den "Add Pair" Button
        robot.clickOn("Add Pair");

        robot.clickOn("#participant1ComboBox");
        robot.sleep(100);
        // Select the first item from the dropdown
        robot.type(KeyCode.DOWN); // Move to the first item
        robot.type(KeyCode.ENTER); // Select the item

        robot.clickOn("#participant2ComboBox");
        robot.sleep(100);
        // Select the first item from the dropdown
        robot.type(KeyCode.DOWN); // Move to the first item
        robot.type(KeyCode.ENTER);

        robot.clickOn("Reassemble Pair");

        // Wait for the alert dialog to appear and verify its content
        assertThat(robot.lookup(".dialog-pane .content").queryLabeled().getText())
                .isEqualTo("Please select two different participants to form a pair.");

        // Close the alert dialog
        robot.clickOn(".dialog-pane .button");
    }

    /**
 * Tests the functionality of adding a new pair of participants.
 * This test simulates the user interaction for adding a pair by selecting two random participants
 * from the available options and submitting the form. It verifies that the pair has been successfully
 * added by checking if the size of the pairs list in the table view has increased by one.
 *
 * @param robot FxRobot instance for simulating user interactions with the UI.
 */
    @Test
    void testAddPair(FxRobot robot) {
        // Simulate clicking on the "Successor Participants" tab
        robot.clickOn("Successor Participants");

        // Simulate clicking on the "Add Pair" button
        robot.clickOn("Add Pair");
        // Get the initial size of the table view
        int initialSize = robot.lookup("#allPairsTableView").queryTableView().getItems().size();

        // Wait for the dialog to be visible and populated
        waitForFxEvents();

        // Select two random participants from the dropdowns
        robot.clickOn("#participant1ComboBox");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        waitForFxEvents();
        robot.clickOn("#participant2ComboBox");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        // Simulate clicking on the "Reassemble Pair" button
        robot.clickOn("Reassemble Pair");

        // Close the dialog by clicking on the default close button (X button)
        try {
            robot.clickOn((PointQuery) robot.lookup(".dialog-pane .window .close-button").query()); // Assuming this is the close button
        } catch (EmptyNodeQueryException ignored) {
            // Handle if the close button is not present
            robot.type(KeyCode.ESCAPE); // Or close with ESC key
        }

        // Switch to the "All Pairs" tab
        robot.clickOn("All Pairs");

        // Wait for the table view to be visible and populated
        waitForFxEvents();


        // Verify if the size of the table view has increased by one
        assertThat(robot.lookup("#allPairsTableView").queryTableView().getItems().size())
                .isEqualTo(initialSize + 1);
    }

    /**
 * Tests the undo functionality after adding a new pair of participants.
 * This test first adds a new pair by selecting two random participants and submitting the form,
 * then it invokes the undo functionality. It verifies that the undo operation was successful by
 * checking if the size of the pairs list in the table view has decreased by one, indicating that
 * the recently added pair was removed.
 *
 * @param robot FxRobot instance for simulating user interactions with the UI.
 */
    @Test
    void testUndoAddPair(FxRobot robot){
        // Navigate to the "Successor Participants" tab
        robot.clickOn("Successor Participants");
        // Click on the "Add Pair" button
        robot.clickOn("Add Pair");
        robot.sleep(100);
        // Get the initial size of the pairs list
        int initialSize = robot.lookup("#allPairsTableView").queryTableView().getItems().size();
        // Select two random participants
        robot.clickOn("#participant1ComboBox");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.sleep(100);

        robot.clickOn("#participant2ComboBox");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        // Submit the form to add the pair
        robot.clickOn("Reassemble Pair");
        // Close the dialog
        robot.type(KeyCode.ESCAPE);
        // Invoke the undo functionality
        robot.clickOn("Undo");
        robot.sleep(100);
        // Wait for any asynchronous events to process
        waitForFxEvents();
        // Verify that the size of the pairs list has decreased by one, indicating the pair was removed
        assertThat(robot.lookup("#allPairsTableView").queryTableView().getItems().size())
                .isEqualTo(initialSize);

    }

    /**
 * Tests the redo functionality after undoing the addition of a new pair of participants.
 * This test first adds a new pair by selecting two random participants and submitting the form,
 * then it undoes this addition and subsequently redoes it. It verifies that the redo operation was
 * successful by checking if the size of the pairs list in the table view has returned to its size
 * after the initial addition, indicating that the pair was successfully re-added.
 *
 * @param robot FxRobot instance for simulating user interactions with the UI.
 */
    @Test
    void testRedoAddPair(FxRobot robot){
        // Navigate to the "Successor Participants" tab
        robot.clickOn("Successor Participants");
        // Click on the "Add Pair" button
        robot.clickOn("Add Pair");
        robot.sleep(100);
        // Get the initial size of the pairs list
        int initialSize = robot.lookup("#allPairsTableView").queryTableView().getItems().size();
        // Select two random participants

        robot.clickOn("#participant1ComboBox");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.sleep(100);

        robot.clickOn("#participant2ComboBox");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.sleep(100);
        // Submit the form to add the pair
        robot.clickOn("Reassemble Pair");
        // Close the dialog
        robot.type(KeyCode.ESCAPE);
        // Invoke the undo functionality
        robot.clickOn("Undo");
        robot.sleep(200);
        robot.clickOn("Redo");
        robot.sleep(200);

        // Wait for any asynchronous events to process
        waitForFxEvents();
        // Verify that the size of the pairs list has decreased by one, indicating the pair was removed
        assertThat(robot.lookup("#allPairsTableView").queryTableView().getItems().size())
                .isEqualTo(initialSize+1);
    }


    /**
 * Tests the deletion of a pair from the "All Pairs" table in the application.
 * This method simulates user interaction by navigating to the "All Pairs" tab,
 * selecting the first pair in the table, and then clicking the "Remove" button to delete it.
 * After the deletion, it verifies that the selected pair is no longer present in the table,
 * ensuring that the deletion functionality works as expected.
 *
 * @param robot FxRobot instance for simulating user interactions with the UI.
 */
    @Test
    void testDeletePair(FxRobot robot) {
        // Simulate clicking on the "All Pairs" Tab
        robot.clickOn("All Pairs");
        // Wait for the table view to be visible and populated
        robot.sleep(500);
        // Select the first row in the table view
        robot.clickOn("#allPairsTableView").type(KeyCode.DOWN).type(KeyCode.ENTER);
        // Remember the selected pair for verification later
        String selectedPairText = robot.lookup("#allPairsTableView .table-row-cell").nth(0).query().getAccessibleText();
        // Simulate clicking on the "Remove" Button
        robot.clickOn("Remove");
        // Verify that the pair is removed from the table view
        assertThat(robot.lookup("#allPairsTableView").queryTableView().getItems())
                .filteredOn(pair -> pair.toString().equals(selectedPairText))
                .isEmpty();

    }

    /**
 * Tests the deletion message functionality after removing a pair from the "All Pairs" table.
 * This test navigates to the "All Pairs" tab, selects the first pair in the table, and initiates its deletion.
 * After confirming the deletion, it waits for a success message dialog to appear and verifies that the message
 * matches the expected text, indicating that the pair has been successfully deleted. The test ensures that
 * the application correctly informs the user about the successful deletion of a pair.
 *
 * @param robot FxRobot instance for simulating user interactions with the UI.
 */
    @Test
    void testDeletionMsg(FxRobot robot){
        // Navigate to the "All Pairs" tab
        robot.clickOn("All Pairs");
        // Wait for the table view to be visible and populated
        robot.sleep(500); // Adjust timing as needed for your application
        // Select the first row in the table view
        robot.clickOn("#allPairsTableView").type(KeyCode.DOWN).type(KeyCode.ENTER);
        // Simulate clicking on the "Remove" Button
        robot.clickOn("Remove");
        // Confirm the deletion
        robot.clickOn("OK");
        // Wait for the success message dialog to appear
        robot.sleep(500);
        // Verify the success message
        assertThat(robot.lookup(".dialog-pane .content").queryLabeled().getText())
                .isEqualTo("The pair 13 has been successfully deleted.");
        // Close the success message dialog
        robot.clickOn("OK");
    }

    /**
 * Tests the undo functionality after deleting a pair from the "All Pairs" table.
 * This test first deletes a pair by selecting it in the table and confirming the deletion,
 * then it invokes the undo functionality. It verifies that the undo operation was successful by
 * checking if the size of the pairs list in the table view returns to its original size before the deletion,
 * indicating that the deleted pair has been restored.
 *
 * @param robot FxRobot instance for simulating user interactions with the UI.
 */
    @Test
    void testUndoDeletePair(FxRobot robot){
        // Get the initial size of the pairs list
        int initialSize = robot.lookup("#allPairsTableView").queryTableView().getItems().size();
        // Navigate to the "All Pairs" tab
        robot.clickOn("All Pairs");
        // Wait for the table view to be visible and populated
        robot.sleep(500); // Adjust timing as needed for your application
        // Select the first row in the table view
        robot.clickOn("#allPairsTableView").type(KeyCode.DOWN).type(KeyCode.ENTER);
        // Simulate clicking on the "Remove" Button
        robot.clickOn("Remove");
        // Confirm the deletion
        robot.clickOn("OK");
        // Wait for the success message dialog to appear
        robot.sleep(500);
        robot.clickOn("OK");
        // Wait for the success message dialog to appear
        robot.sleep(500);
        // Invoke the undo functionality
        robot.clickOn("Undo");
        robot.sleep(500);
        // Wait for any asynchronous events to process
        waitForFxEvents();
        // Verify that the size of the pairs list has same size , indicating the redo pair was done
        assertThat(robot.lookup("#allPairsTableView").queryTableView().getItems().size())
                .isEqualTo(initialSize);
    }

    /**
 * Tests the redo functionality after undoing a deletion of a pair from the "All Pairs" table.
 * This test first deletes a pair and then undoes this deletion. Subsequently, it redoes the deletion
 * by invoking the redo functionality. It verifies that the redo operation was successful by checking
 * if the size of the pairs list in the table view has decreased by one, indicating that the pair was
 * successfully removed again.
 *
 * @param robot FxRobot instance for simulating user interactions with the UI.
 */
    @Test
    void testRedoDeletePair(FxRobot robot){
        // Get the initial size of the pairs list
        int initialSize = robot.lookup("#allPairsTableView").queryTableView().getItems().size();
        // Navigate to the "All Pairs" tab
        robot.clickOn("All Pairs");
        // Wait for the table view to be visible and populated
        robot.sleep(500); // Adjust timing as needed for your application
        // Select the first row in the table view
        robot.clickOn("#allPairsTableView").type(KeyCode.DOWN).type(KeyCode.ENTER);
        // Simulate clicking on the "Remove" Button
        robot.clickOn("Remove");
        // Confirm the deletion
        robot.clickOn("OK");
        // Wait for the success message dialog to appear
        robot.sleep(500);
        robot.clickOn("OK");
        // Wait for the success message dialog to appear
        robot.sleep(500);
        // Invoke the undo functionality
        robot.clickOn("Undo");
        robot.sleep(500);
        robot.clickOn("Redo");
        robot.sleep(500);
        // Wait for any asynchronous events to process
        waitForFxEvents();
        // Verify that the size of the pairs list has decreased by one, indicating the pair was removed
        assertThat(robot.lookup("#allPairsTableView").queryTableView().getItems().size())
                .isEqualTo(initialSize - 1);
    }


    /**
 * Tests the existence and functionality of the "Edit Pair" dialog in the application.
 * This method simulates user interaction by navigating to the "All Pairs" tab, selecting a pair,
 * and then clicking the "Edit" button. It verifies that the "Edit Pair" dialog window is correctly
 * displayed, ensuring that users can access the editing functionality for pairs.
 *
 * @param robot FxRobot instance for simulating user interactions with the UI.
 */
    @Test
    void testIfEditPairExist(FxRobot robot) {
        // Simulieren Sie das Klicken auf den "All Pairs" Tab
        robot.clickOn("All Pairs");

        // Simulieren Sie das Auswählen eines Paares
        robot.clickOn("#allPairsTableView");
        robot.type(KeyCode.DOWN); // Bewegen Sie sich zum ersten Element

        // Simulieren Sie das Klicken auf den "Edit" Button
        robot.clickOn("Edit");

        // Überprüfen Sie, ob das Dialogfenster angezeigt wird
        assertThat(robot.window("Edit Pair")).isShowing();
    }

    /**
     * Tests the editing functionality for a pair in the application.
     * This method simulates the user interaction for editing the first pair in the "All Pairs" table.
     * It navigates to the "All Pairs" tab, selects the first pair, and clicks the "Edit" button.
     * Then, it modifies the details of the pair, including names of participants, cooking preferences,
     * and location coordinates. After saving the changes, it verifies that the edited details are correctly
     * updated in the table view by comparing the table's first item's string representation with the expected values.
     *
     * @param robot FxRobot instance for simulating user interactions with the UI.
     */
    @Test
    void testEditPair(FxRobot robot){
        robot.lookup("#allPairsTableView").queryTableView().getSelectionModel().select(0);
        robot.sleep(500);
        robot.clickOn("Edit");
        robot.sleep(500);
        robot.clickOn("#participant1NameField");
        robot.eraseText(20);
        robot.write("New Participant 1");
        robot.clickOn("#participant2NameField");
        robot.eraseText(20);
        robot.write("New Participant 2");
        robot.type(KeyCode.TAB);
        robot.eraseText(10);
        robot.write("false");
        robot.type(KeyCode.TAB);
        robot.eraseText(20);
        robot.write("2.433482932233");
        robot.type(KeyCode.TAB);
        robot.eraseText(20);
        robot.write("48.85884430000001");
        robot.type(KeyCode.TAB);
        robot.eraseText(10);
        robot.write("MEAT");
        robot.type(KeyCode.TAB);
        robot.eraseText(10);
        robot.write("100");
        robot.type(KeyCode.TAB);
        robot.eraseText(10);
        robot.write("50");
        robot.type(KeyCode.TAB);
        robot.eraseText(10);
        robot.write("51");
        robot.type(KeyCode.TAB);
        robot.eraseText(10);
        robot.write("52");
        robot.type(KeyCode.TAB);
        robot.eraseText(10);
        robot.write("true");
        robot.type(KeyCode.TAB);
        robot.eraseText(10);
        robot.write("DESSERT");
        robot.clickOn("Save");
        robot.sleep(500);
        robot.clickOn("OK");

        assertThat(robot.lookup("#allPairsTableView").queryTableView().getItems().get(0).toString())
                .contains("New Participant 1", "New Participant 2", "false", "2.433482932233", "48.85884430000001", "MEAT", "100", "50", "51", "52", "true", "DESSERT");
    }

    /**
     * Tests the success message display after editing a pair's information.
     * This method navigates to the "All Pairs" tab, selects the first pair, and opens the edit dialog.
     * It simulates editing the name of participant1 and then saves the changes. After saving, it verifies
     * that a success message dialog appears with the expected text, confirming that the pair has been
     * successfully updated. Finally, it closes the success message dialog.
     *
     * @param robot FxRobot instance for simulating user interactions with the UI.
     */
    @Test
    void testEditingPairMSG(FxRobot robot) {
        // Step 1: Navigate to the "All Pairs" tab and select the first pair
        robot.clickOn("All Pairs");
        robot.lookup("#allPairsTableView").queryTableView().getSelectionModel().select(0);
        robot.sleep(500); // Wait for selection

        // Step 2: Open the edit dialog by clicking on the "Edit" button
        robot.clickOn("Edit");
        robot.sleep(500); // Wait for the dialog to open

        // Step 3: Make changes and save
        // Example: Change the name of participant1 (adjust according to your actual UI components)
        robot.clickOn("#participant1NameField");
        robot.eraseText(10); // Adjust the number of characters to erase as needed
        robot.write("Edited Name");
        robot.clickOn("Save");
        robot.sleep(500); // Wait for the save operation to complete

        // Step 4: Verify the success message
        // Adjust the expected message to match what your application displays
        String expectedMessage = "The pair 1 has been successfully updated.";
        assertThat(robot.lookup(".dialog-pane .content").queryLabeled().getText()).isEqualTo(expectedMessage);

        // Step 5: Close the success message dialog
        robot.clickOn("OK");
    }


/**
 * Verifies that the "Performance Indicators" tab and its list view are visible and populated.
 * This test navigates to the "Performance Indicators" tab, waits for the list view to become visible,
 * and then checks that the list view exists and is not empty. It ensures that the UI correctly displays
 * the indicators list to the user.
 *
 * @param robot FxRobot instance for simulating user interactions with the UI.
 */
    @Test
    void testIfIndicatorsIsVisible(FxRobot robot){
        // Navigate to the "Indicators" tab
        robot.clickOn("Performance Indicators");
        // Wait for the list view to be visible and populated
        robot.sleep(500);
        // Verify that the "Indicators" list view exists
        assertThat(robot.lookup("#indicatorsListView").tryQuery().isPresent()).isTrue();
        // verify that the list view is not empty
        assertThat(robot.lookup("#indicatorsListView").queryListView().getItems()).isNotEmpty();
    }

    /**
 * Tests the correctness of the displayed performance indicators against expected values.
 * This test navigates to the "Performance Indicators" tab, retrieves the displayed indicators from the UI,
 * and compares them with a set of expected indicators generated by the application's logic. It verifies
 * that all expected indicators are present in the UI, ensuring the accuracy of the performance metrics displayed.
 *
 * @param robot FxRobot instance for simulating user interactions with the UI.
 */
    @Test
    void testIfIndicatorsCorrect(FxRobot robot) {
        // Navigate to the "Indicators" tab
        robot.clickOn("Performance Indicators");
        // Wait for the list view to be visible and populated
        robot.sleep(500); // Adjust timing as needed for your application
        // Assuming you have a method to get expected indicators
        List<String> expectedIndicators = getExpectedIndicators();
        // Fetch the indicators from the UI
        List<String> uiIndicators = robot.lookup("#indicatorsListView").queryListView().getItems()
                .stream().map(Object::toString).collect(Collectors.toList());
        // Verify that all expected indicators are present in the UI
        assertThat(uiIndicators).containsAll(expectedIndicators);
    }

    /**
 * Tests the visibility and functionality of the compare pairs dialog in the application.
 * This test simulates a user action that triggers the display of the compare pairs dialog,
 * and then verifies that the dialog window is correctly shown by checking for a specific
 * element within the dialog. The presence of this element confirms that the dialog has
 * been displayed as expected.
 *
 * @param robot FxRobot instance for simulating user interactions with the UI.
 */
    @Test
    public void testComparePairsDialog(FxRobot robot) {
        // Simulate the action that triggers comparePairs()
        robot.clickOn("#PerformanceIndicators1"); // Adjust the selector to match your UI

        // Verify the dialog window is shown. This might involve checking for a specific element in the dialog.
        assertTrue(robot.lookup("#compareListView").tryQuery().isPresent(), "The compare pairs dialog should be displayed");

    }

    /**
 * Generates a list of expected performance indicators based on the current state of pairs and successor participants.
 * This method utilizes the {@link PairsPerformanceIndicatorsCalculator} to calculate performance indicators
 * such as the number of pairs, number of successor participants, gender diversity, average age difference,
 * and average preference deviation. It returns these indicators as a list of strings, ready for comparison
 * against the indicators displayed in the UI.
 *
 * @return List<String> containing the expected performance indicators.
 */
    private List<String> getExpectedIndicators() {
        // Implement logic to determine expected indicators
        var calculator =  new PairsPerformanceIndicatorsCalculator(PairsAndGroupsManager.getInstance().getPairs(),
                PairsAndGroupsManager.getInstance().getSuccessorParticipants());
        calculator.calculatePerformanceIndicators();
        var indicators = calculator.getPerformanceIndicators();
        return Arrays.asList(indicators);
    }

}
