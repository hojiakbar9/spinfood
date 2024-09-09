import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import models.data.GroupModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.testfx.assertions.api.Assertions.assertThat;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(ApplicationExtension.class)
public class GUIGroupTest extends ApplicationTest  {
    @Override
    public void start(Stage stage) throws Exception {

        // Laden Sie Ihre Haupt-FXML-Datei
        Parent mainNode = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/pairs.fxml")));
        stage.setScene(new Scene(mainNode));
        stage.show();
    }
    /**
     *  Tests the functionality of the "Add Group" button in the "Groups" tab.
     *  This test simulates the user interaction for adding a new group by clicking on the "Add" button,
     *  then verifies that the "Reassemble Group" dialog window is displayed. It ensures that the application
     *  correctly handles the addition of a new group and prompts the user to reassemble the group.
     * */
    @Test
    void testReassembleGroupWindowShowing(FxRobot robot) {
        // Simulate clicking on the "Groups" Tab
        robot.clickOn("Groups");

        // Wait for the table view to be visible and populated
        robot.sleep(500);


        robot.clickOn("Add");

        // Verify that the dialog window is showing
        assertThat(robot.window("Reassemble Group")).isShowing();
    }

    /**
     * Checks if the "Groups" tab is visible and the table view is populated with data.
     * This test navigates to the "Groups" tab and verifies that the table view is visible and populated
     * with the expected columns and data. It ensures that the UI correctly displays the groups data to the user.
     * */
    @Test
    void testInitialData(FxRobot robot) {
        robot.clickOn("Groups");

        TableView<GroupModel> allGroupsTable = robot.lookup("#allGroupsTable").queryTableView();
        assertNotNull(allGroupsTable);
        assertEquals(5, allGroupsTable.getColumns().size());
    }

    /**
     * Tests the functionality of adding a new group.
     * This test simulates the user interaction for deleting a group by selecting a group from the table view
     * */
    @Test
    void testDeleteGroup(FxRobot robot) {
        robot.clickOn("Groups");

        TableView<GroupModel> allGroupsTable = robot.lookup("#allGroupsTable").queryTableView();
        int initialSize = allGroupsTable.getItems().size();

        robot.clickOn("#allGroupsTable");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);

        robot.clickOn("Remove");

        assertThat(allGroupsTable.getItems()).hasSize(initialSize - 1);
    }

    /**
     *  Tests the functionality of the "Undo" button after removing a group.
     *  This test simulates the user interaction for removing a group and then clicking on the "Undo" button
     *  to restore the removed group. It verifies that the group is successfully restored by checking the size
     *  of the groups list in the table view.
     * */
    @Test
    void testUndoRemove(FxRobot robot) {
        robot.clickOn("Groups");

        TableView<GroupModel> allGroupsTable = robot.lookup("#allGroupsTable").queryTableView();
        int initialSize = allGroupsTable.getItems().size();

        robot.clickOn("#allGroupsTable");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);

        robot.clickOn("Remove");
        robot.sleep(500);
        assertThat(allGroupsTable.getItems()).hasSize(initialSize - 1);

        robot.clickOn("Undo");

        assertThat(allGroupsTable.getItems()).hasSize(initialSize);
    }

    /**
     *  Tests the functionality of the "Redo" button after undoing the removal of a group.
     *  This test simulates the user interaction for removing a group, undoing the removal, and then redoing
     *  the removal. It verifies that the group is successfully removed again by checking the size of the groups
     *  list in the table view.
     * */
    @Test
    void testRedoRemove(FxRobot robot) {
        robot.clickOn("Groups");

        TableView<GroupModel> allGroupsTable = robot.lookup("#allGroupsTable").queryTableView();
        int initialSize = allGroupsTable.getItems().size();

        robot.clickOn("#allGroupsTable");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);

        robot.clickOn("Remove");
        robot.sleep(500);
        assertThat(allGroupsTable.getItems()).hasSize(initialSize - 1);

        robot.clickOn("Undo");
        robot.sleep(500);
        assertThat(allGroupsTable.getItems()).hasSize(initialSize);
        robot.sleep(500);
        robot.clickOn("Redo");

        assertThat(allGroupsTable.getItems()).hasSize(initialSize - 1);
    }

    /**
 * Tests the functionality of adding a new group in the "Groups" tab.
 * This method simulates the user interaction for adding a new group by navigating to the "Groups" tab,
 * selecting participants for the new group, and submitting the form. It verifies that the group has been
 * successfully added by checking if the size of the groups list in the table view remains unchanged after
 * the operation, indicating that the new group has been added to the system.
 *
 * @param robot FxRobot instance for simulating user interactions with the UI.
 */
    @Test
    void testAddGroup(FxRobot robot) {
        robot.clickOn("Groups");

        TableView<GroupModel> allGroupsTable = robot.lookup("#allGroupsTable").queryTableView();
        int initialSize = allGroupsTable.getItems().size();

        robot.clickOn("#allGroupsTable");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);

        robot.clickOn("Remove");
        robot.sleep(500);
        robot.clickOn("Add");
        robot.sleep(500);


        robot.clickOn("#participant1ComboBox");
        robot.sleep(400);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);

        robot.clickOn("#participant2ComboBox");
        robot.sleep(400);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);

        robot.clickOn("#participant3ComboBox");
        robot.sleep(400);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);

        robot.clickOn("Reassemble Group");

        assertThat(allGroupsTable.getItems()).hasSize(initialSize);
    }

    /**
     *  Test the add group functionality with invalid selection.
     *  This test simulates the user interaction for adding a new group by selecting the same participant for all three positions.
     *  It verifies that an alert dialog is displayed, indicating that three different participants must be selected to form a group.
     * */
    @Test
    void testInvalidReassembleGroup(FxRobot robot) {
        robot.clickOn("Groups");
        robot.clickOn("#allGroupsTable");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);

        robot.clickOn("Remove");
        robot.sleep(500);
        robot.clickOn("Add");
        //select the same participant for all three positions
        robot.clickOn("#participant1ComboBox");
        robot.sleep(100);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);

        robot.clickOn("#participant2ComboBox");
        robot.sleep(100);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);

        robot.clickOn("#participant3ComboBox");
        robot.sleep(100);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);


        robot.clickOn("Reassemble Group");

        assertThat(robot.window("Invalid Selection")).isShowing();
    }

    /**
 * Tests the visibility and functionality of all side buttons in the application's UI.
 * This method simulates user interactions by clicking on each side button ("Groups", "Criteria", and "Pairs")
 * and verifies that the corresponding UI elements are visible after each click. It ensures that the main
 * navigation elements of the application are functional and that their associated views are correctly displayed.
 *
 * @param robot FxRobot instance for simulating user interactions with the UI.
 */
    @Test
    void testAllSideButton(FxRobot robot){
        robot.clickOn("Groups");
        robot.sleep(500);
        assertThat(robot.lookup("#allGroupsTable").tryQuery().isPresent()).isTrue();

        robot.clickOn("Criteria");
        robot.sleep(500);
        assertThat(robot.lookup("#pairingFirstPriority").tryQuery().isPresent()).isTrue();

        robot.clickOn("Pairs");
        robot.sleep(500);
        assertThat(robot.lookup("#allPairsTableView").tryQuery().isPresent()).isTrue();

    }

    /**
 * Tests the visibility and functionality of the "Groups" side button in the application's UI.
 * This method simulates a user interaction by clicking on the "Groups" button and verifies that
 * the corresponding UI element (the groups table) is visible after the click. It ensures that the
 * "Groups" navigation element is functional and that its associated view is correctly displayed.
 *
 * @param robot FxRobot instance for simulating user interactions with the UI.
 */
    @Test
    void testGroupSideButton(FxRobot robot){
        robot.clickOn("Groups");
        robot.sleep(500);
        assertThat(robot.lookup("#allGroupsTable").tryQuery().isPresent()).isTrue();
    }



}
